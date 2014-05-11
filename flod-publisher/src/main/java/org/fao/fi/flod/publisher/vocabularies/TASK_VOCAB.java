/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.fi.flod.publisher.vocabularies;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TASK_VOCAB {
    
    
    public static final Property TASK = ResourceFactory.createProperty("http://semanticrepository/publisher/task#Task");
    public static final Property target_graph = ResourceFactory.createProperty("http://semanticrepository/publisher/task#target_graph");
    public static final Property source_graph = ResourceFactory.createProperty("http://semanticrepository/publisher/task#source_graph");
    public static final Property depending_graph = ResourceFactory.createProperty("http://semanticrepository/publisher/task#depending_graph");
    public static final Property source_endpoint = ResourceFactory.createProperty("http://semanticrepository/publisher/task#source_endpoint");
    public static final Property publication_endpoint = ResourceFactory.createProperty("http://semanticrepository/publisher/task#publication_endpoint");
    public static final Property transformation_query = ResourceFactory.createProperty("http://semanticrepository/publisher/task#query");
    public static final Property diff_query = ResourceFactory.createProperty("http://semanticrepository/publisher/task#diff_query");
    public static final Property operation = ResourceFactory.createProperty("http://semanticrepository/publisher/task#operation");
    
    
}
