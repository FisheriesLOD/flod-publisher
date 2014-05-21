/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher;

import com.hp.hpl.jena.graph.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    final private Map<Node, Boolean> taskRegistry = new HashMap<Node, Boolean>();
    final private ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

    final static int SCHEDULED_FREQUENCY = 10 * 1000;

    public static void main(String[] args) {
        final Publisher $this = new Publisher();

        $this.SCHEDULER.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                $this.retrieveTasks();
            }
        }, 0, SCHEDULED_FREQUENCY, TimeUnit.MILLISECONDS);

        $this.SCHEDULER.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                $this.execute();
            }
        }, SCHEDULED_FREQUENCY / 2, SCHEDULED_FREQUENCY, TimeUnit.MILLISECONDS);
    }

    private void retrieveTasks() {
        List<Node> tasks = TaskStore.getInstance().listTasks();
        synchronized (this.taskRegistry) {
            for (Node taskN : tasks) {
                boolean executed = this.taskRegistry.containsKey(taskN);
                if (!executed) {
                    this.taskRegistry.put(taskN, false);
                    System.out.println("new task registered " + taskN);
//	                log.info("new task registered for execution \n"
//	                        + "source : {} \n"
//	                        + "target : {} \n"
//	                        + "operation : {} \n"
//	                        + "creator: {}", 
//	                        pt.sourceGraphs, 
//	                        pt.targetGraph, 
//	                        pt.operation, 
//	                        pt.creator);
                } else //                        log.info("{} not a new task",pt.title);
                {
                    System.out.println(taskN + " is a known task");
                }
            }
        }
    }

    private void execute() {
        synchronized (this.taskRegistry) {
            Set<Node> tasks = this.taskRegistry.keySet();
            for (Node taskN : tasks) {
                try {
                    boolean executed = this.taskRegistry.get(taskN);
                    if (!executed) {
                        TaskStore ts = TaskStore.getInstance();
                        PublicationTask pt = PublicationTask.create(ts.getGraph(taskN));
                        ts.runTask(pt);
                    }
                } catch (Exception ex) {
                    log.info("Error in task {} execution; see cause {}", taskN, ex.getMessage());
                }
            }
            System.out.println("There is no new task to execute...");
        }
    }
}
