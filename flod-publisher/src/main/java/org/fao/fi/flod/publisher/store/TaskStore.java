/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import static org.fao.fi.flod.publisher.utils.PublicationPolicy.*;
import org.fao.fi.flod.publisher.utils.PublicationTask;
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

    private TaskStore() {
        storeAccessor = new DatasetGraphAccessorHTTP(configuration.getTaskEndpointURL().toString());
        storeAccessor_data = new DatasetGraphAccessorHTTP(configuration.getDataTaskEndpointURL().toString());
//        loadTasks();
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

        List<String> graphURIs = ResultSetUtils.resultSetToStringList(execSelect, "graphId", "Resource");
        for (String taskGuri : graphURIs) {
            try {
                Node taskGnode = NodeFactory.createURI(taskGuri);
                tasks.add(PublicationTask.create(storeAccessor_data.httpGet(taskGnode)));
            } catch (Exception ex) {
                log.error("The task {} was found with bed description", taskGuri, ex.getStackTrace());
            }
        }
        return tasks;
    }

    public List<URL> runTask(PublicationTask task, String policy) {
        try {
            PublicationStore publicationStore = PublicationStore.getInstance();
            Graph transformedGraph = transform(task.sourceGraphs, task.transformationQuery, task.sourceEndpoint);

            if (policy.equals(DIFF)) {
                log.info(DIFF);
                Graph targetG = publicationStore.getGraph(task.targetGraph.toString());
                Graph diffG = diffGraph(transformedGraph, targetG, task.diffQuery);
                publicationStore.backup(task.targetGraph);
                publicationStore.updated(diffG, task.targetGraph);

            }
            if (policy.equals(REPUBLISH)) {
                log.info(REPUBLISH);
                publicationStore.backup(task.targetGraph);
                publicationStore.publish(transformedGraph, task.targetGraph);
            }
            return task.dependingGraphs;
        } catch (Exception ex) {
            log.error("query execution error");
        }
        return Collections.EMPTY_LIST;
    }

    public void importTask(PublicationTask task) {
        Node gNode = NodeFactory.createURI(allTaskURI);
        QuadDataAcc qda = makeQuadAcc(gNode, task.taskG);
        UpdateDataInsert insert = new UpdateDataInsert(qda);
        UpdateExecutionFactory.createRemote(insert, configuration.getTaskUpdateEndpointURL().toString()).execute();
    }

    private Graph transform(List<URL> sourceGraphs, Query transformationQuery, URL sourceEndpoint) throws Exception {
        List<String> graphs = new ArrayList<String>();
        for (URL url : sourceGraphs) {
            graphs.add(url.toString());
        }
        return QueryExecutionFactory.sparqlService(sourceEndpoint.toString(), transformationQuery, graphs, null).execConstruct().getGraph();
    }

    private Graph diffGraph(Graph sourceG, Graph targetG, Query diffQ) {
        Model targetM = ModelFactory.createDefaultModel();
        targetM.add(ModelUtils.triplesToStatements(GraphUtil.findAll(targetG), targetM));
        String selectDistinctVar = diffQ.getResultVars().get(0);
        ResultSet targetRes = QueryExecutionFactory.create(diffQ, targetM).execSelect();
        List<RDFNode> targetNodes = ResultSetUtils.resultSetToList(targetRes, selectDistinctVar);
        for (RDFNode targetN : targetNodes) {
            sourceG.remove(targetN.asNode(), Node.ANY, Node.ANY);
            sourceG.remove(Node.ANY, Node.ANY, targetN.asNode());
        }
        return sourceG;

    }
}
