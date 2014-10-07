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
public class METADATA_VOCAB {
    
    
    public static final Property source_endpoint = ResourceFactory.createProperty("http://semanticrepository/publisher/task#source_endpoint");
    public static final Property skos_editorial_note = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#editorialNote");
    public static final Property skos_date = ResourceFactory.createProperty("http://purl.org/dc/terms/date");
    public static final Property cc_license = ResourceFactory.createProperty("http://creativecommons.org/ns#license");
    
    
}
