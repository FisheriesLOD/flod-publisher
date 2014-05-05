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
public class TaskStoreIntegrationUpdate {
        private static Logger log = LoggerFactory.getLogger(TaskStoreIntegrationUpdate.class);
    
    @Test
    public void runMicroAsfisUpdate() throws MalformedURLException, PublicationTask.InvalidTask {
        PublicationTask microAsfis_task = PublicationTask.create(new File("tasks/micro-asfis_diff.n3"));
        List<URL> dependingGs = TaskStore.getInstance().runTask(microAsfis_task, PublicationPolicy.DIFF);
        for (URL url : dependingGs) {
            log.info("remember to update also {} ",url);
        }
    }
    
    
    
}
