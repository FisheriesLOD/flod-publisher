/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Node;
import java.io.File;
import java.util.List;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStoreIntegration {
        private static Logger log = LoggerFactory.getLogger(TaskStoreIntegration.class);

//    @Test
    public void runMicroAsfis() throws  PublicationTask.InvalidTask, Exception {
        PublicationTask microAsfis_task = PublicationTask.create(new File("tasks/micro-asfis.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(microAsfis_task);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ",dependingG);
        }
    }
    @Test
    public void runSubArea() throws  PublicationTask.InvalidTask, Exception {
        PublicationTask task = PublicationTask.create(new File("tasks/CL_SUB_AREA.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(task);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ",dependingG);
        }
    }
    
    @Test
    public void runArea() throws  PublicationTask.InvalidTask, Exception {
        PublicationTask task = PublicationTask.create(new File("tasks/CL_FAO_MAJOR_AREA.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(task);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ",dependingG);
        }
    }
    
}
