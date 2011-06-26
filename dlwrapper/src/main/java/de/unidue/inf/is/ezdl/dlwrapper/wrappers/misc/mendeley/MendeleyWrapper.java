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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.mendeley;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.query.Filter;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.Net;



public class MendeleyWrapper extends AbstractWrapper {

    /**
     * Internal service name for the digital library
     */
    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("mendeley");

    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "mendeley";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "api");
    /**
     * The (not so) secret API key.
     */
    private static final String CONSUMER_KEY = "bf7f57eea978ad72eb6725226e9b891704caf5784";
    /**
     * The maximum number of result that should be retrieved.
     */
    private static final int MAX_RESULTS = 500;

    private static Logger logger = Logger.getLogger(MendeleyWrapper.class);

    private QueryConverter queryConverter = new MendeleyQueryConverter();
    private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();


    public MendeleyWrapper() {
        super();
    }


    public MendeleyWrapper(Net net) {
        super(net);
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        for (StoredDocument stored : incomplete) {
            String id = getDetailInfo(stored);
            if (id != null) {
                try {
                    URI uri = new URI("http", "www.mendeley.com", "/oapi/documents/details/" + id, "consumer_key="
                                    + CONSUMER_KEY, null);

                    String json = getNet().read(uri, "UTF-8");
                    json = json.replace("\"abstract\":", "\"abstrakt\":");
                    MendeleyDocDetails docDetails = gson.fromJson(json, MendeleyDocDetails.class);

                    if (docDetails != null) {
                        Document document = stored.getDocument();
                        String abstrakt = docDetails.getAbstrakt();
                        if (abstrakt != null) {
                            document.setFieldValue(Field.ABSTRACT, abstrakt);
                        }
                        String pages = docDetails.getPages();
                        if (pages != null) {
                            document.setFieldValue(Field.PAGES, pages);
                        }
                        String volume = docDetails.getVolume();
                        if (volume != null) {
                            document.setFieldValue(Field.VOLUME, volume);
                        }
                    }

                    setDetailTimestampToCurrent(stored);
                }
                catch (JsonParseException e) {
                    logger.error(e.getMessage(), e);
                }
                catch (URISyntaxException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    public SourceID getSourceID() {
        return SOURCE_ID;
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        return true;
    }


    @Override
    protected StoredDocumentList process(DocumentQuery query) {
        String q = queryConverter.convert(query.getQuery());

        try {
            URI uri = new URI("http", "www.mendeley.com", "/oapi/documents/search/" + q, "consumer_key=" + CONSUMER_KEY
                            + "&items=" + MAX_RESULTS, null);
            String json = getNet().read(uri, "UTF-8");
            MendeleySearchResult searchResult = gson.fromJson(json, MendeleySearchResult.class);

            if (searchResult != null) {
                Filter filter = new Filter(query);
                StoredDocumentList result = new StoredDocumentList();
                MendeleyDoc[] documents = searchResult.getDocuments();
                if (documents != null) {
                    for (MendeleyDoc md : documents) {
                        Document document = new TextDocument();
                        document.setTitle(md.getTitle());
                        document.setYear(md.getYear());
                        document.addDetailURL(new URL(md.getMendeleyUrl()));
                        document.setFieldValue(Field.DOI, md.getDoi());
                        PersonList pl = new PersonList();
                        pl.add(new Person(md.getAuthors()));
                        document.setAuthorList(pl);

                        if (filter.check(document)) {
                            StoredDocument stored = new StoredDocument(document);
                            stored.addSource(new SourceInfo(SOURCE_ID, md.getUuid()));
                            result.add(stored);
                        }
                    }
                }
                return result;
            }
            else {
                return null;
            }

        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    @Override
    protected StoredDocumentList process(QueryNodeBool queryNodeBool) {
        return null;
    }

}
