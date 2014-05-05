/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.figis.flod_publisher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.fao.fi.flod.publisher.store.TaskStore;
import org.fao.fi.flod.publisher.utils.PublicationPolicy;
import org.fao.fi.flod.publisher.utils.PublicationTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStoreIntegrationMapping {
        private static Logger log = LoggerFactory.getLogger(TaskStoreIntegrationMapping.class);
    
    @Test
    public void runMicroAsfisUpdate() throws MalformedURLException, PublicationTask.InvalidTask {
        PublicationTask mapping_task = PublicationTask.create(new File("tasks/mapping.n3"));
        List<URL> dependingGs = TaskStore.getInstance().runTask(mapping_task, PublicationPolicy.REPUBLISH);
        for (URL url : dependingGs) {
            log.info("remember to update also {} ",url);
        }
    }
    
    
    
}
