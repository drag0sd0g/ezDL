/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import java.util.Collection;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DocumentRepository;



/**
 * DocumentInserter inserts documents into a repository.
 * 
 * @author mjordan
 */
public class DocumentInserter implements Runnable, Haltable {

    private Logger logger = Logger.getLogger(DocumentInserter.class);

    private DocumentRepository repository;

    private static final int PRIO_RESULT_ITEM = 50;
    private static final int PRIO_DETAIL = 100;


    public DocumentInserter(DocumentRepository repository) {
        this.repository = repository;
    }


    private class QueueItem implements Comparable<QueueItem> {

        public StoredDocument stored;
        public int prio;


        public QueueItem(int prio, StoredDocument stored) {
            if (stored == null) {
                throw new IllegalArgumentException("Cannot store null reference");
            }
            this.stored = stored;
            this.prio = prio;
        }


        @Override
        public int compareTo(QueueItem o) {
            if (prio < o.prio) {
                return -1;
            }
            else if (prio > o.prio) {
                return 1;
            }
            return 0;
        };
    }


    private PriorityBlockingQueue<QueueItem> incomingDocuments = new PriorityBlockingQueue<QueueItem>();

    private boolean running = true;


    /**
     * @see DocumentRepository#addDocument(Document)
     */
    @Override
    public void run() {
        while (!isHalted() || !incomingDocuments.isEmpty()) {
            while (incomingDocuments.size() != 0) {
                final QueueItem item = incomingDocuments.poll();
                final StoredDocument document = item.stored;
                final String oid = document.getOid();
                logger.debug("Inserting " + item.prio + " " + oid + ": " + document);
                repository.addDocument(document.getOid(), document);
            }

            if (!isHalted()) {
                logger.debug("pausing");
                synchronized (this) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.debug("Queue size when halting inserter: " + incomingDocuments.size());
        }

    }


    @Override
    public void halt() {
        running = false;
        synchronized (this) {
            notify();
        }
    }


    @Override
    public boolean isHalted() {
        return !running;
    }


    public void addResultItem(StoredDocument document) {
        add(PRIO_RESULT_ITEM, document);
    }


    public void addDetail(StoredDocument document) {
        add(PRIO_DETAIL, document);
    }


    private void add(int prio, StoredDocument document) {
        logger.debug("adding to queue: " + document);
        try {
            QueueItem item = new QueueItem(prio, document);
            item.stored = document;
            item.prio = prio;
            incomingDocuments.add(item);
            synchronized (this) {
                notify();
            }
        }
        catch (IllegalArgumentException e) {
            logger.error("Tried to add a null document", e);
        }
    }


    public StoredDocumentList getDocuments(Collection<String> oids) {
        QueueItem[] items = incomingDocuments.toArray(new QueueItem[0]);
        StoredDocumentList out = new StoredDocumentList();
        for (QueueItem item : items) {
            final String itemOid = item.stored.getOid();
            if (oids.contains(itemOid)) {
                out.add(item.stored);
            }
        }
        return out;
    }

}
