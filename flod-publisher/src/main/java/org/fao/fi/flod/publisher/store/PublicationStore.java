/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.modify.request.UpdateDrop;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import java.net.URL;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class PublicationStore extends Store {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(PublicationStore.class);

    private static PublicationStore instance;

    private PublicationStore() {
        storeAccessor = new DatasetGraphAccessorHTTP(configuration.getQueryEndpointURL().toString());
        storeAccessor_data = new DatasetGraphAccessorHTTP(configuration.getDataEndpointURL().toString());
    }

    public static PublicationStore getInstance() {
        if (instance == null) {
            instance = new PublicationStore();
        }
        return instance;
    }

    public void backup(URL graphId_toBackup) {
        Node gNode = NodeFactory.createURI(graphId_toBackup.toString());
        Graph backupG = storeAccessor_data.httpGet(gNode);
        if (backupG != null) {
            Node gNode_inBackup = NodeFactory.createURI(graphId_toBackup.toString() + date());
            QuadDataAcc qda = makeQuadAcc(gNode_inBackup, backupG);
            UpdateDataInsert insert = new UpdateDataInsert(qda);
            UpdateExecutionFactory.createRemote(insert, configuration.getBackupEndpointURL().toString()).execute();
        }
    }

    public void publish(Graph transformedGraph, URL targetGraph) {
        Node gNode = NodeFactory.createURI(targetGraph.toString());

        UpdateDrop drop = new UpdateDrop(targetGraph.toString(), false);
        UpdateExecutionFactory.createRemote(drop, configuration.getUpdateEndpointURL().toString()).execute();

        QuadDataAcc qda = makeQuadAcc(gNode, transformedGraph);
        UpdateDataInsert insert = new UpdateDataInsert(qda);
        UpdateExecutionFactory.createRemote(insert, configuration.getUpdateEndpointURL().toString()).execute();

    }

}
