/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store.task;

import org.fao.fi.flod.publisher.store.publication.PublicationStore;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.fao.fi.flod.publisher.store.Store;
import static org.fao.fi.flod.publisher.vocabularies.PUBLICATION_POLICY_VOCAB.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStore extends Store {

    private static Logger log = LoggerFactory.getLogger(TaskStore.class);
    private static String allTaskURI = "http://sematicrepository/task/all";

    private static TaskStore instance;
    private Node transformedG_node = NodeFactory.createURI("http://semanticrepository/staging/graph/transformed");

    private TaskStore() {
        storeAccessor = new DatasetGraphAccessorHTTP(configuration.getTaskEndpointURL().toString());
        storeAccessor_data = new DatasetGraphAccessorHTTP(configuration.getDataTaskEndpointURL().toString());
    }

    public static TaskStore getInstance() {
        if (instance == null) {
            instance = new TaskStore();
        }
        return instance;
    }

    public List<PublicationTask> listTasks() {
        List<PublicationTask> tasks = Collections.EMPTY_LIST;
        Query query_listPublicationTasks = configuration.query_listPublicationTasks();
        ResultSet execSelect = QueryExecutionFactory.sparqlService(configuration.getQueryEndpointURL().toString(), query_listPublicationTasks).execSelect();

        List<RDFNode> graphURIs = ResultSetUtils.resultSetToList(execSelect, "graphId");
        for (RDFNode taskGnode : graphURIs) {
            try {
                tasks.add(PublicationTask.create(storeAccessor_data.httpGet(taskGnode.asNode())));
            } catch (Exception ex) {
                log.error("The task {} was found with bed description", taskGnode, ex.getStackTrace());
            }
        }
        return tasks;
    }

    public List<Node> runTask(PublicationTask task) throws Exception {
        PublicationStore publicationStore = PublicationStore.getInstance();
        Graph deltaG = GraphFactory.createGraphMem();
        Graph transformedGraph = transform(task.sourceGraphs, task.transformationQuery, task.sourceEndpoint);
        printOut(transformedGraph, "transformed");
        if (task.operation.toString().equals(ADD)
                || task.operation.toString().equals(REMOVE)) {
            log.info("executing {}, on {} ", task.operation, task.targetGraph.toString());
            if (publicationStore.exists(task.targetGraph)) {
                Graph targetG = publicationStore.getGraph(task.targetGraph);
                printOut(targetG, "target");
//                deltaG = deltaGraph(targetG, transformedGraph, task.targetGraph, transformedG_node, task.diffQuery);
                deltaG = microAsfisAdd_fromFile();
                printOut(deltaG, "delta");
            } else {
                throw new PublicationTask.InvalidTask();
            }
        }
        if (task.operation.toString().equals(PUBLISH)) {
            log.info("executing {}, on {} ", task.operation, task.targetGraph.toString());
            deltaG = transformedGraph;
        }
        publicationStore.backup(task.targetGraph);
        publicationStore.publish(deltaG, task.targetGraph, task.operation.toString());
        return task.dependingGraphs;

    }

    public void importTask(PublicationTask task) {
        Node gNode = NodeFactory.createURI(allTaskURI);
        QuadDataAcc qda = makeQuadAcc(gNode, task.taskG);
        UpdateDataInsert insert = new UpdateDataInsert(qda);
        UpdateExecutionFactory.createRemote(insert, configuration.getTaskUpdateEndpointURL().toString()).execute();
    }

    private Graph transform(List<Node> sourceGraphs, Query transformationQuery, Node sourceEndpoint) throws Exception {
        List<String> graphs = new ArrayList<String>();
        for (Node sourceG : sourceGraphs) {
            graphs.add(sourceG.toString());
        }
        return QueryExecutionFactory.sparqlService(sourceEndpoint.toString(), transformationQuery, graphs, null).execConstruct().getGraph();
    }

    private Graph deltaGraph(Graph narrowG, Graph broaderG, Node narrowG_node, Node broaderG_node, Query diffQ) {
        DatasetGraph dsg = DatasetGraphFactory.createMem();
        dsg.addGraph(broaderG_node, broaderG);
        dsg.addGraph(narrowG_node, narrowG);
        return QueryExecutionFactory.create(diffQ, DatasetFactory.create(dsg)).execConstruct().getGraph();
    }

    private Graph microAsfisAdd_fromFile() {
        File f = new File("graphs/micro-asfis-add.rdf");
        return RDFDataMgr.loadGraph(f.toURI().toString());
    }

    private Graph microAsfisRemove_fromFile() {
        File f = new File("graphs/micro-asfis-remove.rdf");
        return RDFDataMgr.loadGraph(f.toURI().toString());
    }

    public static void main(String[] args) throws MalformedURLException, PublicationTask.InvalidTask {
        File taskFile = new File(args[1]);
        PublicationTask task = PublicationTask.create(taskFile);
        TaskStore.getInstance().importTask(task);
    }

    private void printOut(Graph g, String name) {
        System.out.println(name);
        RDFDataMgr.write(System.out, g, Lang.RDFXML);
    }

}