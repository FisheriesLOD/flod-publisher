/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import java.net.MalformedURLException;
import org.fao.fi.flod.publisher.store.publication.PublicationStore;
import org.fao.fi.flod.publisher.vocabularies.PUBLICATION_POLICY_VOCAB;
import org.junit.Test;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class PublicationStoreTest {
    
    @Test
    public void backupGraph() throws MalformedURLException{
        Node url = NodeFactory.createURI("http://semanticrepository/graph/test");
        
        PublicationStore.getInstance().backup(url);
    }
    
    @Test
    public void publish() throws MalformedURLException{
        Node url = NodeFactory.createURI("http://semanticrepository/graph/test");
        
        PublicationStore.getInstance().publish(Utils.fooModel().getGraph(), url, PUBLICATION_POLICY_VOCAB.PUBLISH);

    }
    
}
