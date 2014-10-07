/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.figis.flod_publisher;

import java.io.File;
import java.net.MalformedURLException;
import org.fao.fi.flod.publisher.store.GraphMetadata;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class GraphMetadataTest {

    private static Logger log = LoggerFactory.getLogger(GraphMetadataTest.class);
    private File taskFile = new File("tasks/asfis_alpha3.task.n3");

   
    @Test
    public void describeGraph() throws GraphMetadata.InvalidMetadata  {

        GraphMetadata md = new GraphMetadata(Utils.fooGraphMetadata().getGraph());
        log.info("creator uri : {}", md.getCreator());
        log.info("creator : {}", md.getCreatorL());
        log.info("date : {}", md.getDate());
        log.info("editorial note : {}", md.getEditorialNote());
        log.info("license of usage uri : {}", md.getLicense());
        log.info("license of usage : {}", md.getLicenseL());
        log.info("rights holder uri : {}", md.getRightsHolder());
        log.info("rights holder : {}", md.getRightsHolderL());
        log.info("source endpoint : {}", md.getSourceEndpoint());
        log.info("title : {}", md.getTitle());
    }

}
