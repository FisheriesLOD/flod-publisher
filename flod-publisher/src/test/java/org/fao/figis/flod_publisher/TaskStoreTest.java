/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStoreTest {

    private static Logger log = LoggerFactory.getLogger(TaskStoreTest.class);
    private File taskFile = new File("tasks/asfis_alpha3.task.n3");

    @Test
    public void readStore() throws MalformedURLException {
        List<Node> tasks = TaskStore.getInstance().listTasks();
        log.info("The task repository contains {} tasks definitions", tasks.size());
    }

    @Test
    public void describeTask() throws MalformedURLException, PublicationTask.InvalidTask {

        PublicationTask task = PublicationTask.create(Utils.fooTask().getGraph());
        log.info("depending task found : {}", task.dependingGraphs);
        log.info("publication endpoint found : {}", task.publicationEndpoint);
        log.info("source endpoint found : {}", task.sourceEndpoint);
        log.info("source graphs found : {}", task.sourceGraphs);
        log.info("target graphs found : {}", task.targetGraph);
        log.info("transformation query found : {}", task.transformationQuery);
        log.info("transformation query found : {}", task.diffQuery);
    }

    @Test
    public void writeTaskOnFile() throws FileNotFoundException, MalformedURLException, PublicationTask.InvalidTask {
        PublicationTask.toFile(Utils.fooTask(null), taskFile);
    }

    @Test
    public void importTask() throws MalformedURLException, PublicationTask.InvalidTask {
        PublicationTask CL_SUB_AREA_task_p = PublicationTask.create(new File("tasks/CL_SUB_AREA.n3"));
        PublicationTask CL_SUB_AREA_task_add = PublicationTask.create(new File("tasks/CL_SUB_AREA_add.n3"));
        PublicationTask CL_SUB_AREA_task_remove = PublicationTask.create(new File("tasks/CL_SUB_AREA_remove.n3"));
        PublicationTask CL_FAO_MAJOR_AREA_task = PublicationTask.create(new File("tasks/CL_FAO_MAJOR_AREA.n3"));
        PublicationTask area_hasPart_subArea_task = PublicationTask.create(new File("tasks/area_hasPart_subArea.n3"));
//        TaskStore.getInstance().importTask(CL_FAO_MAJOR_AREA_task);
//        TaskStore.getInstance().importTask(CL_SUB_AREA_task_p);
        TaskStore.getInstance().importTask(CL_SUB_AREA_task_add);
//        TaskStore.getInstance().importTask(CL_SUB_AREA_task_remove);
//        TaskStore.getInstance().importTask(area_hasPart_subArea_task);
    }

    @Test
    public void testRun() throws PublicationTask.InvalidTask, Exception {

        //populate statign with test graph
        Node gNode_source = NodeFactory.createURI("http://semanticrepository/graph/source_graph1");
        QuadDataAcc qda_source = makeQuadAcc(gNode_source, Utils.fooModel().getGraph());
        UpdateDataInsert insert_source = new UpdateDataInsert(qda_source);
        UpdateExecutionFactory.createRemote(insert_source, "http://168.202.3.223:3030/sr_staging/update").execute();

        PublicationTask fooTask = Utils.fooTask(this);
        List<Node> dependingGs = TaskStore.getInstance().runTask(fooTask);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ", dependingG);
        }
    }

    private QuadDataAcc makeQuadAcc(Node gNode, Graph graph) {
        ExtendedIterator<Triple> allTriples = graph.find(Node.ANY, Node.ANY, Node.ANY);
        QuadDataAcc qda = new QuadDataAcc();
        qda.setGraph(gNode);
        while (allTriples.hasNext()) {
            Triple triple = allTriples.next();
            qda.addTriple(triple);
        }
        return qda;
    }

}
