/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store;

import org.fao.fi.flod.publisher.store.publication.PublicationStore;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.jena.web.DatasetGraphAccessorHTTP;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class Store {

    protected String CONFIGURATION_FILE = "publisher.properties";
    protected StoreConfiguration configuration;
    protected DatasetGraphAccessorHTTP storeAccessor;
    protected DatasetGraphAccessorHTTP storeAccessor_data;
    protected String endpoint_query_url;

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

    public Graph getGraph(Node graphId) {
        return storeAccessor_data.httpGet(graphId);
    }

    public boolean exists(Node graphId) {
        Graph g = storeAccessor_data.httpGet(graphId);
        return g != null;
    }

    public List<Node> listGraphs() {
        String qs = "select distinct ?g where {graph ?g {?s ?p ?o}}";
        List<Node> graphNs = new ArrayList();
        Query query_listPublicationTasks = QueryFactory.create(qs);
        ResultSet execSelect = QueryExecutionFactory.sparqlService(endpoint_query_url, query_listPublicationTasks).execSelect();

        List<RDFNode> graphURIs = ResultSetUtils.resultSetToList(execSelect, "?g");
        for (RDFNode graphN : graphURIs) {
            graphNs.add(graphN.asNode());
        }
        return graphNs;
    }

    protected String date() {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        return "_" + cal.getTime().toString().toLowerCase().replace(" ", "_");
    }

    protected QuadDataAcc makeQuadAcc(Node gNode, Graph graph) {
        ExtendedIterator<Triple> allTriples = GraphUtil.findAll(graph);
        QuadDataAcc qda = new QuadDataAcc();
        qda.setGraph(gNode);
        while (allTriples.hasNext()) {
            Triple triple = allTriples.next();
            qda.addTriple(triple);
        }
        return qda;
    }

}
