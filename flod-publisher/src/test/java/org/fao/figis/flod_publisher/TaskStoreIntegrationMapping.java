/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import java.io.File;
import java.util.List;
import org.fao.fi.flod.publisher.store.publication.PublicationStore;
import org.fao.fi.flod.publisher.store.task.TaskStore;
import org.fao.fi.flod.publisher.store.task.PublicationTask;
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
    public void runMappingTask() throws PublicationTask.InvalidTask, Exception {
        PublicationTask mapping_task = PublicationTask.create(new File("tasks/area_hasPart_subArea.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(mapping_task);
        for (Node dependingG : dependingGs) {
            log.info("depending graph : {} ", dependingG);
        }
    }

    @Test
    public void runMappingTask_consistency() throws PublicationTask.InvalidTask, Exception {
        PublicationTask mapping_task = PublicationTask.create(new File("tasks/area_hasPart_subArea.n3"));
        List<Node> dependingGs = TaskStore.getInstance().runTask(mapping_task);
        for (Node dependingG : dependingGs) {
            log.info("checking consistency of mapping with source graph : {} ", dependingG);
            Graph sourceG = PublicationStore.getInstance().getGraph(dependingG);
            Graph mappingG = PublicationStore.getInstance().getGraph(mapping_task.targetGraph);
            org.fao.fi.flod.publisher.store.task.TaskStoreConsistency.existance(sourceG, mappingG);
        }
    }

}
