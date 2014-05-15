/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher;

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

    final private Map<PublicationTask, Boolean> taskRegistry = new HashMap<PublicationTask, Boolean>();
    final private ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

    final static int SCHEDULED_FREQUENCY = 2 * 1000;
    
    public static void main(String[] args) {
    	final Publisher $this = new Publisher();
    	
    	$this.SCHEDULER.scheduleAtFixedRate(new Runnable() {
    		@Override
    		public void run() {
    			long id = System.currentTimeMillis();
    			
    			System.out.println("RETRIEVE TASKS #" + id + ": BEGIN");

//    			$this.retrieveTasks();

    			long sleepTime = Math.round(Math.random() * 2.5 * SCHEDULED_FREQUENCY);
    			try {
    				System.out.println("Retriever going to sleep for " + sleepTime + " mSec... 'nite!");

    				//Simulates long, random computation times...
    				Thread.sleep(sleepTime);
    			} catch(InterruptedException Ie) {
    				throw new RuntimeException(Ie);
    			}
    			
    			System.out.println("RETRIEVE TASKS #" + id + ": END");
    		}
    	}, 0, SCHEDULED_FREQUENCY, TimeUnit.MILLISECONDS);
    	
    	$this.SCHEDULER.scheduleAtFixedRate(new Runnable() {
    		@Override
    		public void run() {
    			long id = System.currentTimeMillis();
    			
    			System.out.println("EXECUTE #" + id + ": BEGIN");
  
//    			$this.execute();

    			long sleepTime = Math.round(Math.random() * 2.5 * SCHEDULED_FREQUENCY);
    			
    			try {
    				System.out.println("Executor going to sleep for " + sleepTime + " mSec... 'nite!");
    				
    				//Simulates long, random computation times...
    				Thread.sleep(sleepTime);
    			} catch(InterruptedException Ie) {
    				throw new RuntimeException(Ie);
    			}
    			
    			System.out.println("EXECUTE #" + id + ": END");
    		}    		
    	}, SCHEDULED_FREQUENCY / 2, SCHEDULED_FREQUENCY, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unused")
	private void retrieveTasks() {
        List<PublicationTask> tasks = TaskStore.getInstance().listTasks();
        
        synchronized(this.taskRegistry) {
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
    }

    @SuppressWarnings("unused")
	private void execute() {
    	synchronized(this.taskRegistry) {
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
}
