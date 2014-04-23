/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.utils;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class PublicationTask {

    public static TASK_VOCAB TASK_VOCAB;
    public Graph taskG;

    private PublicationTask() {

    }

    private  List<URL> listDependingGraphs() throws MalformedURLException, InvalidTask {
        List<URL> dependingGrpahs = new ArrayList<URL>();
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.depending_graph.asNode(), Node.ANY);
        while (it.hasNext()) {
            Triple triple = it.next();
            dependingGrpahs.add(new URL(triple.getObject().getURI()));
        }
        if (dependingGrpahs.isEmpty()) {
            throw new InvalidTask();
        }
        return dependingGrpahs;
    }

    private  URL getTargetGraph() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.target_graph.asNode(), Node.ANY);
        URL targetG = new URL(it.next().getObject().getURI());
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return targetG;
    }

    private  List<URL> listSourceGraphs() throws MalformedURLException, InvalidTask {
        List<URL> sources = new ArrayList<URL>();
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.source_graph.asNode(), Node.ANY);
        while (it.hasNext()) {
            Triple triple = it.next();
            sources.add(new URL(triple.getObject().getURI()));
        }
        if (sources.isEmpty()) {
            throw new InvalidTask();
        }
        return sources;
    }

    private  Query getTransformationQuery() throws InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.transformation_query.asNode(), Node.ANY);
        Query q = QueryFactory.create(it.next().getObject().getLiteral().toString());
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return q;
    }

    private  URL getSourceEndpoint() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.source_endpoint.asNode(), Node.ANY);
        URL sourceGraph = new URL(it.next().getObject().getURI());
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return sourceGraph;
    }

    private  URL getPublicationEndpoint() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.publication_endpoint.asNode(), Node.ANY);
        URL publicationE = new URL(it.next().getObject().getURI());
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return publicationE;
    }

    public URL targetGraph;
    public List<URL> sourceGraphs;
    public Query transformationQuery;
    public URL sourceEndpoint;
    public URL publicationEndpoint;
    public List<URL> dependingGraphs;

    public static PublicationTask create(Graph graph) throws MalformedURLException, InvalidTask {

        PublicationTask pt = new PublicationTask();
        pt.taskG = graph;
        pt.targetGraph = pt.getTargetGraph();
        pt.sourceGraphs = pt.listSourceGraphs();
        pt.transformationQuery = pt.getTransformationQuery();
        pt.sourceEndpoint = pt.getSourceEndpoint();
        pt.publicationEndpoint = pt.getPublicationEndpoint();
        pt.dependingGraphs = pt.listDependingGraphs();
        
        return pt;
    }
    
    public static PublicationTask create (File f) throws MalformedURLException, InvalidTask{
        Model taskM = RDFDataMgr.loadModel(f.toURI().toString());
//        Model taskM = FileManager.get().loadModel(f.getAbsolutePath());
        return create(taskM.getGraph());
    }
    
    public static File toFile (PublicationTask t, File f) throws FileNotFoundException{
//        Model taskM = ModelFactory.createDefaultModel();
//        StmtIterator stmts = ModelUtils.triplesToStatements(GraphUtil.findAll(t.taskG), taskM);
//  
//        taskM.add(stmts);
        RDFDataMgr.createGraphWriter(Lang.NT).write(new FileOutputStream(f), t.taskG, null, null, Context.emptyContext);
        return f;
    }

    public static class InvalidTask extends Exception {

        public InvalidTask() {
        }
    }

}
