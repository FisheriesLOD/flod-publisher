/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store.publication;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataDelete;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.modify.request.UpdateDrop;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import java.net.URL;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.fao.fi.flod.publisher.store.Store;
import org.fao.fi.flod.publisher.vocabularies.PUBLICATION_POLICY_VOCAB;
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

    public void backup(Node graphId_toBackup) {
        Graph backupG = storeAccessor_data.httpGet(graphId_toBackup);
        if (backupG != null) {
            Node gNode_inBackup = NodeFactory.createURI(graphId_toBackup.toString() + date());
            QuadDataAcc qda = makeQuadAcc(gNode_inBackup, backupG);
            UpdateDataInsert insert = new UpdateDataInsert(qda);
            UpdateExecutionFactory.createRemote(insert, configuration.getBackupEndpointURL().toString()).execute();
        }
    }

    public void publish(Graph sourceG, Node targetGraph, String method) {
        if (method.equals(PUBLICATION_POLICY_VOCAB.PUBLISH)) {
            rewrite(sourceG, targetGraph);
            log.info("Operation {} executed; {} triples were published", method, sourceG.size());
        }
        if (method.equals(PUBLICATION_POLICY_VOCAB.ADD)) {
            add(sourceG, targetGraph);
            log.info("Operation {} executed; {} triples were added", method, sourceG.size());
        }
        if (method.equals(PUBLICATION_POLICY_VOCAB.REMOVE)) {
            remove(sourceG, targetGraph);
            log.info("Operation {} executed; {} triples were removed", method, sourceG.size());
        }
        
    }

    public void rewrite(Graph sourceG, Node targetG_node) {
        UpdateDrop drop = new UpdateDrop(targetG_node.toString(), false);
        UpdateExecutionFactory.createRemote(drop, configuration.getUpdateEndpointURL().toString()).execute();

        QuadDataAcc qda = makeQuadAcc(targetG_node, sourceG);
        UpdateDataInsert insert = new UpdateDataInsert(qda);
        UpdateExecutionFactory.createRemote(insert, configuration.getUpdateEndpointURL().toString()).execute();

    }

    public void add(Graph sourceG, Node targetG_node) {
        QuadDataAcc qda = makeQuadAcc(targetG_node, sourceG);
        UpdateDataInsert insert = new UpdateDataInsert(qda);
        UpdateExecutionFactory.createRemote(insert, configuration.getUpdateEndpointURL().toString()).execute();

    }

    public void remove(Graph sourceG, Node targetG_node) {
        QuadDataAcc qda = makeQuadAcc(targetG_node, sourceG);
        UpdateDataDelete delete = new UpdateDataDelete(qda);
        UpdateExecutionFactory.createRemote(delete, configuration.getUpdateEndpointURL().toString()).execute();
    }

}
