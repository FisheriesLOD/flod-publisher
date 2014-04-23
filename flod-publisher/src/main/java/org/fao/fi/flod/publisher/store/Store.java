/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.fao.fi.flod.publisher.StoreConfiguration;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class Store {

    protected String CONFIGURATION_FILE = "publisher.properties";
    protected StoreConfiguration configuration;
    protected DatasetGraphAccessorHTTP storeAccessor;
    protected DatasetGraphAccessorHTTP storeAccessor_data;
    protected Store() {
        Properties properties = new Properties();
         
        try {
            properties.load(PublicationStore.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE));
        } catch (Exception e) {
            throw new IllegalStateException("missing configuration: configuration file " + CONFIGURATION_FILE + " not on classpath");
        }
        try {
            configuration = new StoreConfiguration(properties);
        } catch (Exception e) {
            throw new IllegalStateException("invalid configuration (see cause) ", e);
        }
    }
   

    public Graph getGraph(String graphId) {
        Node gNode = NodeFactory.createURI(graphId.toString());
        return storeAccessor.httpGet(gNode);
    }
    
    protected String date() {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        return "_" + cal.getTime().toString().toLowerCase().replace(" ", "_");
    }

    protected QuadDataAcc makeQuadAcc(Node gNode, Graph graph) {
        ExtendedIterator<Triple> allTriples = graph.find(Node.ANY,Node.ANY,Node.ANY);
        QuadDataAcc qda = new QuadDataAcc();
        qda.setGraph(gNode);
        while (allTriples.hasNext()) {
            Triple triple = allTriples.next();
            qda.addTriple(triple);
        }
        return qda;
    }

}
