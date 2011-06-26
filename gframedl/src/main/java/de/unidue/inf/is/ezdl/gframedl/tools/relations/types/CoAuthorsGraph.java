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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;



/**
 * This graph type shows the coauthors of documents.
 * 
 * @author RT
 */
public class CoAuthorsGraph extends RelationGraphType {

    private static final int NODE_LIMIT = 200;

    private static I18nSupport i18n;


    public CoAuthorsGraph() {
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
        Graph<DLObject, Number> graph = new UndirectedSparseGraph<DLObject, Number>();

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

        addVertices(graph, authorsArray);

        for (Object i : authorsArray) {
            for (Object j : authorsArray) {
                if (i != j) {
                    if (isCoauthor((Person) i, (Person) j, docsArray)) {
                        graph.addEdge(new Double(Math.random()), (DLObject) i, (DLObject) j, EdgeType.UNDIRECTED);
                    }
                }
            }
        }

        return graph;
    }


    private boolean isCoauthor(Person p1, Person p2, Object[] docsArray) {
        boolean result = false;
        for (Object doc : docsArray) {
            TextDocument d = (TextDocument) doc;
            boolean a1 = false;
            boolean a2 = false;
            PersonList pl = d.getAuthorList();
            for (Person p : pl) {
                if (p.equals(p1)) {
                    a1 = true;
                }
                if (p.equals(p2)) {
                    a2 = true;
                }
                if (a1 && a2) {
                    result = true;
                    break;
                }
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
        return i18n.getLocString("ezdl.relations.types.coAuthorsGraph");
    }


    @Override
    public Layout<DLObject, Number> getLayout(Graph<DLObject, Number> g) {
        return new ISOMLayout<DLObject, Number>(g);
    }


    @Override
    public int getVertexRadius(DLObject v, Graph<DLObject, Number> g) {
        return 30 + (int) Math.round((g.inDegree(v) + g.outDegree(v)) * 0.75);
    }
}
