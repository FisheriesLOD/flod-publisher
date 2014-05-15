/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Graph;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
import org.fao.fi.flod.publisher.vocabularies.TASK_VOCAB;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.fao.fi.flod.publisher.vocabularies.PUBLICATION_POLICY_VOCAB;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class Utils {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(Utils.class);

    public static Model fooTask() throws MalformedURLException {
        Model taskModel = ModelFactory.createDefaultModel();
        Resource targetG = taskModel.createResource("http://semanticrepository/graph/target_graph");
        Resource sourceEndpoint = taskModel.createResource("http://168.202.3.223:3030/sr_staging/query");
        Resource publicationEndpoint = taskModel.createResource("http://168.202.3.223:3030/sr_public/update");
        Resource taskRes = taskModel.createResource("http://semanticrepository/task", TASK_VOCAB.TASK);
        Resource policy = taskModel.createResource(PUBLICATION_POLICY_VOCAB.PUBLISH);

        URL sourceG1 = new URL("http://semanticrepository/graph/source_graph1");
//        URL sourceG2 = new URL("http://semanticrepository/graph/source_graph2");
        List<URL> sourcG = new ArrayList<URL>();
        sourcG.add(sourceG1);
//        sourcG.add(sourceG2);
        for (URL url : sourcG) {
            Resource gRes = taskModel.createResource(url.toString());
            taskModel.add(taskRes, TASK_VOCAB.source_graph, gRes);
        }

        URL dependingG1 = new URL("http://semanticrepository/graph/depending_graph1");
        URL dependingG2 = new URL("http://semanticrepository/graph/depending_graph2");
        List<URL> dependingG = new ArrayList<URL>();
        dependingG.add(dependingG1);
        dependingG.add(dependingG2);

        for (URL url : dependingG) {
            Resource dRes = taskModel.createResource(url.toString());
            taskModel.add(taskRes, TASK_VOCAB.depending_graph, dRes);
        }

        Query q = QueryFactory.create("construct {?s ?p <http://semanticrepository/transformed>} where {?s ?p ?o}");

        taskModel.add(taskRes, TASK_VOCAB.target_graph, targetG)
                .add(taskRes, TASK_VOCAB.publication_endpoint, publicationEndpoint)
                .add(taskRes, TASK_VOCAB.source_endpoint, sourceEndpoint)
                .add(taskRes, TASK_VOCAB.transformation_query, q.serialize())
                .add(taskRes, TASK_VOCAB.operation, policy);
        return taskModel;
    }

    public static Model fooModel() {
        String fooNS = "http://foo/";
        Model foo = ModelFactory.createDefaultModel();
        Resource sbj = foo.createResource(fooNS + AnonId.create());
        Property prp = foo.createProperty(fooNS + AnonId.create().toString());
        Resource obj = foo.createProperty(fooNS + AnonId.create().toString());

        foo.add(sbj, prp, obj);
        return foo;
    }

    public static PublicationTask fooTask(Object useless) throws MalformedURLException, PublicationTask.InvalidTask {
        Model fooTask = fooTask();
        return PublicationTask.create(fooTask.getGraph());

    }

    public void publish_on_file(Model rdf, String graphId, String filename) {
        DatasetGraph dsg = DatasetGraphFactory.createMem();
        dsg.addGraph(NodeFactory.createURI(graphId), rdf.getGraph());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filename));
            RDFDataMgr.write(fos, dsg, Lang.NQUADS);
            fos.close();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    
    public static Graph microAsfisRemove() {
        File f = new File("graphs/micro-asfis-remove.rdf");
        return RDFDataMgr.loadGraph(f.toURI().toString());
    }
    public static Graph microAsfisAdd() {
        File f = new File("graphs/micro-asfis-add.rdf");
        return RDFDataMgr.loadGraph(f.toURI().toString());
    }
    public static Graph microAsfisExistenceCheck() {
        File f = new File("graphs/micro-asfis-existence_check.rdf");
        return RDFDataMgr.loadGraph(f.toURI().toString());
    }

    public static void printOut(Graph g, String name) {
        System.out.println(name);
        RDFDataMgr.write(System.out, g, Lang.RDFXML);
    }
}
