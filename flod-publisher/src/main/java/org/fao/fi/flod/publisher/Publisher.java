/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class Publisher {

    private static Logger log = LoggerFactory.getLogger(Publisher.class);
    private HashMap<PublicationTask, Boolean> taskRegistry = new HashMap();

    public static void main(String[] args) {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

        ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(new Callable() {
            public Object call() throws Exception {
                System.out.println("Executed!");
                return "Called!";
            }
        },
                5,
                TimeUnit.SECONDS);

//        System.out.println("result = " + scheduledFuture.get());
//        scheduledExecutorService.shutdown();
    }

    private void retrieveTasks() {
        List<PublicationTask> tasks = TaskStore.getInstance().listTasks();
        for (PublicationTask pt : tasks) {
            boolean executed = this.taskRegistry.containsKey(pt);
            if (!executed) {
                this.taskRegistry.put(pt, false);
                log.info("new task registered for execution \n"
                        + "source : {} \n"
                        + "target : {} \n"
                        + "operation : {} \n"
                        + "creator: {}", 
                        pt.sourceGraphs, 
                        pt.targetGraph, 
                        pt.operation, 
                        pt.creator);
            }
        }
    }

    private void execute() {
        Set<PublicationTask> tasks = this.taskRegistry.keySet();
        for (PublicationTask pt : tasks) {
            try {
                boolean executed = this.taskRegistry.get(pt);
                if (!executed) {
                    TaskStore.getInstance().runTask(pt);
                }
            } catch (Exception ex) {
                log.info("Error in task {} execution; see cause {}", pt.title, ex.getMessage());
            }
        }

    }
}
