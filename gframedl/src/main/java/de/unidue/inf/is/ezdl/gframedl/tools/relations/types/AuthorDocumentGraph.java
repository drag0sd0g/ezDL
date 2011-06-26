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

package de.unidue.inf.is.ezdl.gframedl.tools.relations.types;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.tools.relations.RelationGraphType;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;



/**
 * This graph type shows the coauthors of documents.
 * 
 * @author RT
 */
public class AuthorDocumentGraph extends RelationGraphType {

    private static final int NODE_LIMIT = 200;

    private static I18nSupport i18n;


    public AuthorDocumentGraph() {
        i18n = I18nSupport.getInstance();
    }


    @Override
    public Dimension getGraphSize(Graph<DLObject, Number> g) {
        int s = g.getVertexCount() * 40;
        s = Math.max(s, 500);
        int w = (int) Math.round(s * 1.5);
        return new Dimension(w, s);
    }


    @Override
    public Graph<DLObject, Number> getGraph(List<DLObject> objects) {
        Graph<DLObject, Number> graph = new DirectedSparseGraph<DLObject, Number>();

        Set<TextDocument> docs = new HashSet<TextDocument>();
        Set<Person> authors = new HashSet<Person>();

        int nodeCount = 0;

        for (DLObject o : objects) {
            if (o instanceof TextDocument) {
                TextDocument d = (TextDocument) o;
                docs.add(d);
                PersonList pl = d.getAuthorList();
                for (Person p : pl) {
                    authors.add(p);
                }
                nodeCount++;
                if (nodeCount > NODE_LIMIT) {
                    break;
                }
            }
        }

        Object[] docsArray = docs.toArray();
        Object[] authorsArray = authors.toArray();

        addVertices(graph, docsArray);
        addVertices(graph, authorsArray);

        for (Object author : authorsArray) {
            for (Object doc : docsArray) {
                if (isAuthorOf((Person) author, (TextDocument) doc)) {
                    graph.addEdge(new Double(Math.random()), (DLObject) author, (DLObject) doc, EdgeType.DIRECTED);
                }
            }
        }
        return graph;
    }


    private boolean isAuthorOf(Person p, TextDocument d) {
        boolean result = false;
        for (Person author : d.getAuthorList()) {
            if (p.equals(author)) {
                result = true;
                break;
            }
        }
        return result;
    }


    private void addVertices(Graph<DLObject, Number> g, Object[] vertices) {
        for (Object o : vertices) {
            g.addVertex((DLObject) o);
        }
    }


    @Override
    public String getName() {
        return i18n.getLocString("ezdl.relations.types.AuthorsDocumentGraph");
    }


    @Override
    public Layout<DLObject, Number> getLayout(Graph<DLObject, Number> g) {
        return new ISOMLayout<DLObject, Number>(g);
    }


    @Override
    public int getVertexRadius(DLObject v, Graph<DLObject, Number> g) {
        return 30 + (g.inDegree(v) + g.outDegree(v)) * 2;
    }

}
