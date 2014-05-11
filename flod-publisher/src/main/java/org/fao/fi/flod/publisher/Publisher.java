/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fao.fi.flod.publisher.store.publication.PublicationStore;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.fao.fi.flod.publisher.store.task.PublicationTask;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class Publisher {

    public PublicationStore publicationRepository;
    public TaskStore taskRepository;

    public Publisher() {

    }

    public static void main(String[] args) {
        TaskStore taskRepository = TaskStore.getInstance();
        List<PublicationTask> tasks = taskRepository.listTasks();
        for (PublicationTask task : tasks) {
            try {
                taskRepository.runTask(task);
            } catch (Exception ex) {
                Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
