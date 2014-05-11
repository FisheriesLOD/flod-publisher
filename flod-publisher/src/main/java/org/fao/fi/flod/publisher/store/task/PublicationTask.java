/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher.store.task;

import org.fao.fi.flod.publisher.vocabularies.TASK_VOCAB;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class PublicationTask {

    public Graph taskG;

    private PublicationTask() {

    }

    private List<Node> listDependingGraphs() throws MalformedURLException, InvalidTask {
        List<Node> dependingGrpahs = new ArrayList<Node>();
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.depending_graph.asNode(), Node.ANY);
        while (it.hasNext()) {
            Triple triple = it.next();
            dependingGrpahs.add(triple.getObject());
        }
        return dependingGrpahs;
    }

    private Node getTargetGraph() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.target_graph.asNode(), Node.ANY);
        Node targetG = it.next().getObject();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return targetG;
    }

    private List<Node> listSourceGraphs() throws MalformedURLException, InvalidTask {
        List<Node> sources = new ArrayList<Node>();
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.source_graph.asNode(), Node.ANY);
        while (it.hasNext()) {
            Triple triple = it.next();
            sources.add(triple.getObject());
        }
        if (sources.isEmpty()) {
            throw new InvalidTask();
        }
        return sources;
    }

    private Query getDiffQuery() throws InvalidTask {
        Query q = QueryFactory.create();
        boolean pExists = taskG.contains(Node.ANY, TASK_VOCAB.diff_query.asNode(), Node.ANY);
        if (pExists) {
            ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.diff_query.asNode(), Node.ANY);
            q = QueryFactory.create(it.next().getObject().getLiteral().toString());
            if (it.hasNext()) {
                throw new InvalidTask();
            }
        }
        return q;
    }

    private Query getTransformationQuery() throws InvalidTask {
        Query q = QueryFactory.create();
        boolean pExists = taskG.contains(Node.ANY, TASK_VOCAB.transformation_query.asNode(), Node.ANY);
        if (pExists) {
            ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.transformation_query.asNode(), Node.ANY);
            q = QueryFactory.create(it.next().getObject().getLiteral().toString());
            if (it.hasNext()) {
                throw new InvalidTask();
            }
        }
        return q;
    }

    private Node getSourceEndpoint() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.source_endpoint.asNode(), Node.ANY);
        Node sourceGraph = it.next().getObject();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return sourceGraph;
    }

    private Node getPublicationEndpoint() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.publication_endpoint.asNode(), Node.ANY);
        Node publicationE = it.next().getObject();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return publicationE;
    }

    private Node getOperation() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, TASK_VOCAB.operation.asNode(), Node.ANY);
        Node policy = it.next().getObject();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return policy;
    }

    private Node getCreator() throws MalformedURLException, InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, DCTerms.creator.asNode(), Node.ANY);
        Node creator = it.next().getObject();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return creator;
    }

    private Date getDate() throws MalformedURLException, InvalidTask {
        return Calendar.getInstance(Locale.ITALY).getTime();
    }

    private String getTitle() throws InvalidTask {
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, RDFS.label.asNode(), Node.ANY);
        String title = it.next().getObject().getLiteralLexicalForm();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return title;
    }
    private String getEditorialNote() throws InvalidTask {
        Node skos_enditorialNote = NodeFactory.createURI("http://www.w3.org/2004/02/skos/core#editorialNote");
        ExtendedIterator<Triple> it = taskG.find(Node.ANY, skos_enditorialNote, Node.ANY);
        String editorialNote = it.next().getObject().getLiteralLexicalForm();
        if (it.hasNext()) {
            throw new InvalidTask();
        }
        return editorialNote;
    }

    public Node targetGraph;
    public Node operation;
    public String title;
    public String editorialNote;
    public Date date;
    public Node creator;
    public List<Node> sourceGraphs;
    public Query transformationQuery;
    public Query diffQuery;
    public Node sourceEndpoint;
    public Node publicationEndpoint;
    public List<Node> dependingGraphs;

    public static PublicationTask create(Graph graph) throws MalformedURLException, InvalidTask {

        PublicationTask pt = new PublicationTask();
        pt.taskG = graph;
        pt.title = pt.getTitle();
        pt.creator = pt.getCreator();
        pt.editorialNote = pt.getEditorialNote();
        pt.date = pt.getDate();
        pt.operation = pt.getOperation();
        pt.targetGraph = pt.getTargetGraph();
        pt.sourceGraphs = pt.listSourceGraphs();
        pt.transformationQuery = pt.getTransformationQuery();
        pt.diffQuery = pt.getDiffQuery();
        pt.sourceEndpoint = pt.getSourceEndpoint();
        pt.publicationEndpoint = pt.getPublicationEndpoint();
        pt.dependingGraphs = pt.listDependingGraphs();

        return pt;
    }
    
    public boolean equals(PublicationTask pt){
        return this.diffQuery.equals(pt.diffQuery) 
               & this.operation.equals(pt.operation) 
               & this.publicationEndpoint.equals(pt.publicationEndpoint) 
               & this.sourceEndpoint.equals(pt.sourceEndpoint) 
               & this.sourceGraphs.equals(pt.sourceGraphs) 
               & this.targetGraph.equals(pt.targetGraph) 
               & this.transformationQuery.equals(pt.transformationQuery); 
        
    }

    public static PublicationTask create(File f) throws MalformedURLException, InvalidTask {
        Model taskM = RDFDataMgr.loadModel(f.toURI().toString());
        return create(taskM.getGraph());
    }

    public static File toFile(PublicationTask t, File f) throws FileNotFoundException {
        RDFDataMgr.createGraphWriter(Lang.NT).write(new FileOutputStream(f), t.taskG, null, null, Context.emptyContext);
        return f;
    }

    public static class InvalidTask extends Exception {

        public InvalidTask() {
        }
    }

}
