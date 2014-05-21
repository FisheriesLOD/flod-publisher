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
public class TaskStoreIntegrationUpdate {
        private static Logger log = LoggerFactory.getLogger(TaskStoreIntegrationUpdate.class);
    
    @Test
    public void run_add() throws PublicationTask.InvalidTask, Exception {
        PublicationTask addTask = PublicationTask.create(new File("tasks/CL_SUB_AREA_add.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(addTask);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ",dependingG);
        }
    }
    
    @Test
    public void run_remove() throws PublicationTask.InvalidTask, Exception {
        PublicationTask removeTask = PublicationTask.create(new File("tasks/CL_SUB_AREA_remove.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(removeTask);
        for (Node dependingG : dependingGs) {
            log.info("remember to update also {} ",dependingG);
        }
    }
    
    
    
}
