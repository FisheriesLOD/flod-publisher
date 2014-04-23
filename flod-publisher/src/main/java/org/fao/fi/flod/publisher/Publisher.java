/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.fi.flod.publisher;

import org.fao.fi.flod.publisher.utils.PublicationPolicy;
import java.util.List;
import org.fao.fi.flod.publisher.store.PublicationStore;
import org.fao.fi.flod.publisher.store.TaskStore;
import org.fao.fi.flod.publisher.utils.PublicationTask;

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
            taskRepository.runTask(task, PublicationPolicy.REPUBLISH);
        }
    }

}
