/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.fi.flod.publisher.store.task;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.UniqueFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStoreConsistency {
    private static Logger log = LoggerFactory.getLogger(TaskStoreConsistency.class);
    
    public static void existance(Graph targetG, Graph sourceG){
        if(targetG.isEmpty() || sourceG.isEmpty()){
            log.warn("Target graph or Source graph are empty!");
            return;
        }
        boolean allgood = true;
        UniqueFilter uniqueFilter = new UniqueFilter();
        ExtendedIterator<Node> nodes = GraphUtil.listSubjects(sourceG, Node.ANY, Node.ANY);
        nodes.andThen(GraphUtil.listObjects(sourceG, Node.ANY, Node.ANY));
        nodes.filterKeep(uniqueFilter);
        while (nodes.hasNext()) {
            Node sourceGnode = nodes.next();
            boolean containsNode = GraphUtil.containsNode(targetG, sourceGnode);
            if(!containsNode){
                log.warn("missing node {} in target ", sourceGnode.toString());
                allgood = false;
            }
        }
        if(allgood)
                log.info("All nodes from source graph exist in the target");
        
    }
    
}
