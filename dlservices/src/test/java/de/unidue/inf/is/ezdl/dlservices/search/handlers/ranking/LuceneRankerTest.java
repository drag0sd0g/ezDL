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

package de.unidue.inf.is.ezdl.dlservices.search.handlers.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.data.DocumentListConverter;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;



public class LuceneRankerTest extends AbstractBackendTestBase {

    private LuceneRanker luceneRanker;


    @Before
    public void init() {
        luceneRanker = new LuceneRanker();
    }


    private ResultDocumentList newDocumentList() {
        ResultDocumentList documentList = new ResultDocumentList();

        ResultDocument doc1 = DocumentFactory.createResultDocument(null, "Information Retrieval for Dummies", 2010,
                        "Norbert Fuhr");
        doc1.setOid("1");
        documentList.add(doc1);

        ResultDocument doc2 = DocumentFactory.createResultDocument(null, "Important publication", 1492, "Chuck Norris",
                        "Bruce Schneier");
        doc2.setOid("2");
        documentList.add(doc2);

        ResultDocument doc3 = DocumentFactory.createResultDocument(null, "Very very very very important publication",
                        2000, "Miss Marple", "Sherlock Holmes");
        doc3.setOid("3");
        documentList.add(doc3);
        return documentList;
    }


    private DocumentQuery newTitleDocumentQuery() {
        QueryNodeCompare queryNodeCompare = new QueryNodeCompare(Field.TITLE, Predicate.EQ, "important");
        DocumentQuery documentQuery = new DocumentQuery(new DefaultQuery(queryNodeCompare),
                        Collections.<String> emptyList());
        return documentQuery;
    }


    private DocumentQuery newAuthorAndTitleDocumentQuery() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.AND);
        queryNodeBool.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "important"));
        queryNodeBool.addChild(new QueryNodeCompare(Field.AUTHOR, Predicate.EQ, "Holmes"));
        DocumentQuery documentQuery = new DocumentQuery(new DefaultQuery(queryNodeBool),
                        Collections.<String> emptyList());
        return documentQuery;
    }


    private List<String> rank(ResultDocumentList documentList) {
        Collections.sort(documentList, new Comparator<ResultDocument>() {

            @Override
            public int compare(ResultDocument o1, ResultDocument o2) {
                return Double.valueOf(o2.getRsv()).compareTo(o1.getRsv());
            }
        });
        List<String> result = new ArrayList<String>();
        for (ResultDocument document : documentList) {
            result.add(document.getOid());
        }
        return result;
    }


    @Test
    public void testRank1() {
        ResultDocumentList documentList = newDocumentList();
        luceneRanker.rank(documentList, newTitleDocumentQuery());

        Assert.assertTrue("Ranked list has unexpected order", rank(documentList).equals(Arrays.asList("2", "3", "1")));
    }


    @Test
    public void testRank2() {
        ResultDocumentList documentList = newDocumentList();
        luceneRanker.rank(documentList, newAuthorAndTitleDocumentQuery());

        Assert.assertTrue("Ranked list has unexpected order", rank(documentList).equals(Arrays.asList("3", "1", "2")));
    }


    /**
     * This is a test case for bug #293: In a search for Title:mapreduce OR
     * Title:"map reduce", the documents containing "mapreduce" get an RSV of 0,
     * which is lower than expected.
     */
    @Test
    public void testMapReduce() {
        ResultDocumentList serializedList = getMapReduceSerializedResultList();
        getLogger().debug("seri: " + serializedList);

        ResultDocumentList workList = serializedList;

        luceneRanker.rank(workList, getMapReduceDocumentQuery());

        getLogger().debug(workList);

        for (ResultDocument result : workList) {
            getLogger().debug(result.getRsv() + "  --  " + result.getDocument().getTitle());
        }
    }


    private ResultDocumentList getMapReduceSerializedResultList() {
        StoredDocumentList serialized = getSerializedList();
        ResultDocumentList serializedList = DocumentListConverter.toResultDocumentList(serialized);
        return serializedList;
    }


    // private ResultDocumentList getMapReduceSolrResultList() {
    // StoredDocumentList results = getMapReduceResultsFromSolrWrapper();
    // generateSourceLines(results);
    // ResultDocumentList resultList =
    // DocumentListConverter.toResultDocumentList(results);
    // return resultList;
    // }
    // private StoredDocumentList getMapReduceResultsFromSolrWrapper() {
    // DBLPSolrWrapper solr = new DBLPSolrWrapper();
    // DocumentQuery query = getMapReduceDocumentQuery();
    // StoredDocumentList results = solr.askDocument(query, false);
    // return results;
    // }

    // private void generateSourceLines(StoredDocumentList results) {
    // for (StoredDocument stored : results) {
    // Document doc = stored.getDocument();
    // String authors = "";
    // for (Person author : doc.getAuthorList()) {
    // authors += ", new Person(\"" + author.asString() + "\")";
    // }
    // System.out.println("addDoc(results, " + doc.getYear() + ", \"" +
    // doc.getTitle() + "\" " + authors + ");");
    // }
    // }

    private DocumentQuery getMapReduceDocumentQuery() {
        QueryNodeBool queryNodeBool = new QueryNodeBool(NodeType.OR);
        queryNodeBool.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "mapreduce"));
        queryNodeBool.addChild(new QueryNodeCompare(Field.TITLE, Predicate.EQ, "map reduce"));
        DocumentQuery query = new DocumentQuery(new DefaultQuery(queryNodeBool), Arrays.asList("dblp"));
        return query;
    }


    private StoredDocumentList getSerializedList() {
        StoredDocumentList results = new StoredDocumentList();

        addDoc(results, 2006, "Map-Reduce for Machine Learning on Multicore.", new Person("Cheng-Tao Chu"), new Person(
                        "Sang Kyun Kim"), new Person("Yi-An Lin"), new Person("YuanYuan Yu"), new Person(
                        "Gary R. Bradski"), new Person("Andrew Y. Ng"), new Person("Kunle Olukotun"));
        addDoc(results, 2009, "Implementing Parallel Google Map-Reduce in Eden.", new Person("Jost Berthold"),
                        new Person("Mischa Dieterle"), new Person("Rita Loogen"));
        addDoc(results, 2008, "Map-reduce as a Programming Model for Custom Computing Machines.", new Person(
                        "Jackson H. C. Yeung"), new Person("C. C. Tsang"), new Person("Kuen Hung Tsoi"), new Person(
                        "Bill S. H. Kwan"), new Person("Chris C. C. Cheung"), new Person("Anthony P. C. Chan"),
                        new Person("Philip Heng Wai Leong"));
        addDoc(results, 2009, "Traverse: Simplified Indexing on Large Map-Reduce-Merge Clusters.", new Person(
                        "Hung-chih Yang"), new Person("D. Stott Parker"));
        addDoc(results, 2009, "Distributed Algorithm for Computing Formal Concepts Using Map-Reduce Framework.",
                        new Person("Petr Krajca"), new Person("Vilém Vychodil"));
        addDoc(results, 2009, "An Efficient Hierarchical Clustering Method for Large Datasets with Map-Reduce.",
                        new Person("Tianyang Sun"), new Person("Chengchun Shu"), new Person("Feng Li"), new Person(
                                        "Haiyan Yu"), new Person("Lili Ma"), new Person("Yitong Fang"));
        addDoc(results,
                        2009,
                        "Impementation of Map, Reduce and Filter Prolog Predicates on the Cell Broadband Engine Architecture.",
                        new Person("Mark Chernault"));
        addDoc(results, 2007, "Map-reduce-merge: simplified relational data processing on large clusters.", new Person(
                        "Hung-chih Yang"), new Person("Ali Dasdan"), new Person("Ruey-Lung Hsiao"), new Person(
                        "Douglas Stott Parker Jr."));
        addDoc(results, 2009, "Filtered-Push: A Map-Reduce Platform for Collaborative Taxonomic Data Management.",
                        new Person("Zhimin Wang"), new Person("Hui Dong"), new Person("Maureen Kelly"), new Person(
                                        "James A. Macklin"), new Person("Paul J. Morris"), new Person(
                                        "Robert A. Morris"));

        addDoc(results, 2010, "FPMR: MapReduce framework on FPGA.", new Person("Yi Shan"), new Person("Bo Wang"),
                        new Person("Jing Yan"), new Person("Yu Wang 0002"), new Person("Ning-Yi Xu"), new Person(
                                        "Huazhong Yang"));

        addDoc(results, 2011, "FPMR: Map-Reduce framework on FPGA.", new Person("Yi Shan"), new Person("Bo Wang"),
                        new Person("Jing Yan"), new Person("Yu Wang 0002"), new Person("Ning-Yi Xu"), new Person(
                                        "Huazhong Yang"));

        addDoc(results, 2012, "FPMR: Map-Reduce merge on FPGA.", new Person("Yi Shan"), new Person("Bo Wang"),
                        new Person("Jing Yan"), new Person("Yu Wang 0002"), new Person("Ning-Yi Xu"), new Person(
                                        "Huazhong Yang"));

        addDoc(results, 2008, "MRBench: A Benchmark for MapReduce Framework.", new Person("Kiyoung Kim"), new Person(
                        "Kyungho Jeon"), new Person("Hyuck Han"), new Person("Shin Gyu Kim"), new Person(
                        "Hyungsoo Jung"), new Person("Heon Young Yeom"));
        addDoc(results, 2009, "On single-pass indexing with MapReduce.", new Person("Richard M. C. McCreadie"),
                        new Person("Craig Macdonald"), new Person("Iadh Ounis"));
        addDoc(results, 2009, "Modeling MapReduce with CSP.", new Person("Wen Su"), new Person("Fan Yang"), new Person(
                        "Huibiao Zhu"), new Person("Qin Li"));
        addDoc(results, 2007, "Parallel PSO using MapReduce.", new Person("Andrew W. McNabb"), new Person(
                        "Christopher K. Monson"), new Person("Kevin D. Seppi"));
        addDoc(results,
                        2008,
                        "DisCo: Distributed Co-clustering with Map-Reduce: A Case Study towards Petabyte-Scale End-to-End Mining.",
                        new Person("Spiros Papadimitriou"), new Person("Jimeng Sun"));
        addDoc(results, 2008, "Mars: a MapReduce framework on graphics processors.", new Person("Bingsheng He"),
                        new Person("Wenbin Fang"), new Person("Qiong Luo"), new Person("Naga K. Govindaraju"),
                        new Person("Tuyong Wang"));
        addDoc(results, 2007, "MRPSO: MapReduce particle swarm optimization.", new Person("Andrew W. McNabb"),
                        new Person("Christopher K. Monson"), new Person("Kevin D. Seppi"));
        addDoc(results, 2009, "Scaling Genetic Algorithms Using MapReduce.", new Person("Abhishek Verma"), new Person(
                        "Xavier Llorà"), new Person("David E. Goldberg"), new Person("Roy H. Campbell"));
        addDoc(results, 2008, "Improving MapReduce Performance in Heterogeneous Environments.", new Person(
                        "Matei Zaharia"), new Person("Andy Konwinski"), new Person("Anthony D. Joseph"), new Person(
                        "Randy H. Katz"), new Person("Ion Stoica"));
        addDoc(results, 2009, "Towards Efficient MapReduce Using MPI.", new Person("Torsten Hoefler"), new Person(
                        "Andrew Lumsdaine"), new Person("Jack Dongarra"));
        addDoc(results, 2009, "Scalable Distributed Reasoning Using MapReduce.", new Person("Jacopo Urbani"),
                        new Person("Spyros Kotoulas"), new Person("Eyal Oren"), new Person("Frank van Harmelen"));
        addDoc(results, 2009, "Experiences on Processing Spatial Data with MapReduce.", new Person("Ariel Cary"),
                        new Person("Zhengguo Sun"), new Person("Vagelis Hristidis"), new Person("Naphtali Rishe"));
        addDoc(results, 2009, "Parallel -Means Clustering Based on MapReduce.", new Person("Weizhong Zhao"),
                        new Person("Huifang Ma"), new Person("Qing He"));
        addDoc(results, 2006, "Experiences with MapReduce, an abstraction for large-scale computation.", new Person(
                        "Jeffrey Dean"));
        addDoc(results, 2009, "SecureMR: A Service Integrity Assurance Framework for MapReduce.",
                        new Person("Wei Wei"), new Person("Juan Du"), new Person("Ting Yu"), new Person("Xiaohui Gu"));
        addDoc(results, 2009, "Evaluating SPLASH-2 Applications Using MapReduce.", new Person("Shengkai Zhu"),
                        new Person("Zhiwei Xiao"), new Person("Haibo Chen"), new Person("Rong Chen"), new Person(
                                        "Weihua Zhang"), new Person("Binyu Zang"));
        addDoc(results, 2009, "Query processing of massive trajectory data based on mapreduce.",
                        new Person("Qiang Ma"), new Person("Bin Yang"), new Person("Weining Qian"), new Person(
                                        "Aoying Zhou"));
        addDoc(results, 2009, "MapReduce Programming Model for .NET-Based Cloud Computing.", new Person("Chao Jin"),
                        new Person("Rajkumar Buyya"));
        addDoc(results, 2007, "Evaluating MapReduce for Multi-core and Multiprocessor Systems.", new Person(
                        "Colby Ranger"), new Person("Ramanan Raghuraman"), new Person("Arun Penmetsa"), new Person(
                        "Gary R. Bradski"), new Person("Christos Kozyrakis"));
        addDoc(results, 2009, "Kahn Process Networks are a Flexible Alternative to MapReduce.", new Person(
                        "Zeljko Vrba"), new Person("Pål Halvorsen"), new Person("Carsten Griwodz"), new Person(
                        "Paul B. Beskow"));
        addDoc(results, 2009, "CLOUDLET: towards mapreduce implementation on virtual machines.", new Person(
                        "Shadi Ibrahim"), new Person("Hai Jin"), new Person("Bin Cheng"), new Person("Haijun Cao"),
                        new Person("Song Wu"), new Person("Li Qi"));
        addDoc(results, 2009, "Efficient Dense Structure Mining Using MapReduce.", new Person("Shengqi Yang"),
                        new Person("Bai Wang"), new Person("Haizhou Zhao"), new Person("Bin Wu"));
        addDoc(results, 2009, "A Mapreduce Framework for Change Propagation in Geographic Databases.", new Person(
                        "Ferdinando Di Martino"), new Person("Salvatore Sessa"), new Person("Giuseppe Polese"),
                        new Person("Mario Vacca"));
        addDoc(results, 2009, "A MapReduce-Enabled Scientific Workflow Composition Framework.", new Person("Xubo Fei"),
                        new Person("Shiyong Lu"), new Person("Cui Lin"));
        addDoc(results, 2004, "MapReduce: Simplified Data Processing on Large Clusters.", new Person("Jeffrey Dean"),
                        new Person("Sanjay Ghemawat"));
        addDoc(results, 2009, "MapReduce System over Heterogeneous Mobile Devices.", new Person("Peter R. Elespuru"),
                        new Person("Sagun Shakya"), new Person("Shivakant Mishra"));
        addDoc(results, 2009, "MapReduce optimization using regulated dynamic prioritization.", new Person(
                        "Thomas Sandholm"), new Person("Kevin Lai"));
        addDoc(results, 2009, "Evaluating MapReduce on Virtual Machines: The Hadoop Case.",
                        new Person("Shadi Ibrahim"), new Person("Hai Jin"), new Person("Lu Lu"), new Person("Li Qi"),
                        new Person("Song Wu"), new Person("Xuanhua Shi"));
        addDoc(results, 2008, "Towards Large Scale Semantic Annotation Built on MapReduce Architecture.", new Person(
                        "Michal Laclavik"), new Person("Martin Seleng"), new Person("Ladislav Hluchý"));
        addDoc(results, 2009, "Toolkit-Based High-Performance Data Mining of Large Data on MapReduce Clusters.",
                        new Person("Dennis Wegener"), new Person("Michael Mock"), new Person("Deyaa Adranale"),
                        new Person("Stefan Wrobel"));
        addDoc(results, 2009, "Speeding Up Distributed MapReduce Applications Using Hardware Accelerators.",
                        new Person("Yolanda Becerra"), new Person("Vicenç Beltran"), new Person("David Carrera"),
                        new Person("Marc González"), new Person("Jordi Torres"), new Person("Eduard Ayguadé"));
        addDoc(results, 2009, "Efficiently support MapReduce-like computation models inside parallel DBMS.",
                        new Person("Qiming Chen"), new Person("Andy Therber"), new Person("Meichun Hsu"), new Person(
                                        "Hans Zeller"), new Person("Bin Zhang"), new Person("Ren Wu"));
        addDoc(results, 2008, "Fast parallel outlier detection for categorical datasets using MapReduce.", new Person(
                        "Anna Koufakou"), new Person("Jimmy Secretan"), new Person("John Reeder"), new Person(
                        "Kelvin Cardona"), new Person("Michael Georgiopoulos"));
        addDoc(results, 2009, "CellMR: A framework for supporting mapreduce on asymmetric cell-based clusters.",
                        new Person("M. Mustafa Rafique"), new Person("Benjamin Rose"), new Person("Ali Raza Butt"),
                        new Person("Dimitrios S. Nikolopoulos"));
        addDoc(results, 2009,
                        "MapReduce as a general framework to support research in Mining Software Repositories (MSR).",
                        new Person("Weiyi Shang"), new Person("Zhen Ming Jiang"), new Person("Bram Adams"), new Person(
                                        "Ahmed E. Hassan"));
        addDoc(results,
                        2009,
                        "Brute force and indexed approaches to pairwise document similarity comparisons with MapReduce.",
                        new Person("Jimmy J. Lin"));
        addDoc(results, 2007, "MapReduce and Other Building Blocks for Large-Scale Distributed Systems at Google.",
                        new Person("Jeffrey Dean"));
        addDoc(results, 2009, "Phoenix rebirth: Scalable MapReduce on a large-scale shared-memory system.", new Person(
                        "Richard M. Yoo"), new Person("Anthony Romano"), new Person("Christos Kozyrakis"));
        addDoc(results, 2009, "A Data Distribution Aware Task Scheduling Strategy for MapReduce System.", new Person(
                        "Leitao Guo"), new Person("Hongwei Sun"), new Person("Zhiguo Luo"));
        addDoc(results, 2009, "Storage and Retrieval of Large RDF Graph Using Hadoop and MapReduce.", new Person(
                        "Mohammad Farhan Husain"), new Person("Pankil Doshi"), new Person("Latifur Khan"), new Person(
                        "Bhavani M. Thuraisingham"));
        addDoc(results,
                        2009,
                        "MapReduce-Based Pattern Finding Algorithm Applied in Motif Detection for Prescription Compatibility Network.",
                        new Person("Yang Liu"), new Person("Xiaohong Jiang"), new Person("Huajun Chen"), new Person(
                                        "Jun Ma"), new Person("Xiangyu Zhang"));
        addDoc(results,
                        2008,
                        "Page-Based Anomaly Detection in Large Scale Web Clusters Using Adaptive MapReduce (Extended Abstract).",
                        new Person("Junsup Lee"), new Person("Sung Deok Cha"));
        addDoc(results,
                        2008,
                        "Scalable Language Processing Algorithms for the Masses: A Case Study in Computing Word Co-occurrence Matrices with MapReduce.",
                        new Person("Jimmy J. Lin"));
        return results;
    }


    private void addDoc(StoredDocumentList list, int year, String title, Person... persons) {
        TextDocument doc = new TextDocument();

        PersonList authors = new PersonList();
        authors.addAll(Arrays.asList(persons));

        doc.setAuthorList(authors);
        doc.setTitle(title);
        doc.setYear(year);
        doc.setOid(OIDFactory.calcOid(doc));

        StoredDocument stored = new StoredDocument(doc);
        stored.addSource(new SourceInfo(new SourceID("dblp", ""), ""));
        list.add(stored);
    }
}
