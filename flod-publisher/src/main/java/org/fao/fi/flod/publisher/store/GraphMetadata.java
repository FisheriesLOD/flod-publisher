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
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.fao.fi.flod.publisher.vocabularies.METADATA_VOCAB;
import org.fao.fi.flod.publisher.vocabularies.TASK_VOCAB;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class GraphMetadata {

    private String title;
    private Node creator;
    private String creatorL;
    private String editorialNote;
    private String date;
    private Node sourceEndpoint;
    private Node license;
    private String licenseL;
    private Node rightsHolder; 
    private String rightsHolderL; 

    private GraphMetadata(){
        
    }
    
    public GraphMetadata(Graph g) throws InvalidMetadata {
        setCreator(g);
        setCreatorL(g);
        setDate(g);
        setEditorialNote(g);
        setLicense(g);
        setLicenseL(g);
        setRightsHolder(g);
        setRightsHolderL(g);
        setSourceEndpoint(g);
        setTitle(g);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the creator
     */
    public Node getCreator() {
        return creator;
    }

    /**
     * @return the creatorL
     */
    public String getCreatorL() {
        return creatorL;
    }

    /**
     * @return the editorialNote
     */
    public String getEditorialNote() {
        return editorialNote;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the sourceEndpoint
     */
    public Node getSourceEndpoint() {
        return sourceEndpoint;
    }


    /**
     * @return the license
     */
    public Node getLicense() {
        return license;
    }

    /**
     * @return the licenseL
     */
    public String getLicenseL() {
        return licenseL;
    }

    /**
     * @return the rightsHolder
     */
    public Node getRightsHolder() {
        return rightsHolder;
    }

    /**
     * @return the rightsHolderL
     */
    public String getRightsHolderL() {
        return rightsHolderL;
    }

    private void setTitle(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, DCTerms.title.asNode(), Node.ANY);
        String title = it.next().getObject().getLiteralLexicalForm();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.title = title;
    }

    private void setCreator(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, DCTerms.creator.asNode(), Node.ANY);
        Node creator = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.creator = creator;
    }

    private void setCreatorL(Graph g) throws InvalidMetadata {
        setCreator(g);
        ExtendedIterator<Triple> it = g.find(this.creator, RDFS.label.asNode(), Node.ANY);
        Node creatorL = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.creatorL = creatorL.getLiteralLexicalForm();
    }

    private void setEditorialNote(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, METADATA_VOCAB.skos_editorial_note.asNode(), Node.ANY);
        String editorialNote = it.next().getObject().getLiteralLexicalForm();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.editorialNote = editorialNote;
    }

    private void setDate(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, METADATA_VOCAB.skos_date.asNode(), Node.ANY);
        String date = it.next().getObject().getLiteralLexicalForm();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.date = date;
    }

    private void setSourceEndpoint(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, TASK_VOCAB.source_endpoint.asNode(), Node.ANY);
        Node sourceEndpoint = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.sourceEndpoint = sourceEndpoint;
    }


    private void setLicense(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, METADATA_VOCAB.cc_license.asNode(), Node.ANY);
        Node license = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.license = license;
     }

    private void setLicenseL(Graph g) throws InvalidMetadata {
        setLicense(g);
        ExtendedIterator<Triple> it = g.find(this.license, RDFS.label.asNode(), Node.ANY);
        Node licenseL = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.licenseL = licenseL.getLiteralLexicalForm();
    }

    private void setRightsHolder(Graph g) throws InvalidMetadata {
        ExtendedIterator<Triple> it = g.find(Node.ANY, DCTerms.rightsHolder.asNode(), Node.ANY);
        Node rightsHolder = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.rightsHolder = rightsHolder;
    }

    private void setRightsHolderL(Graph g) throws InvalidMetadata {
        setRightsHolder(g); 
        ExtendedIterator<Triple> it = g.find(this.rightsHolder, RDFS.label.asNode(), Node.ANY);
        Node rightsHolderL = it.next().getObject();
        if (it.hasNext()) {
            throw new GraphMetadata.InvalidMetadata();
        }
        this.rightsHolderL = rightsHolderL.getLiteralLexicalForm();
    }

     public static class InvalidMetadata extends Exception {

        public InvalidMetadata() {
        }
    }
    
}
