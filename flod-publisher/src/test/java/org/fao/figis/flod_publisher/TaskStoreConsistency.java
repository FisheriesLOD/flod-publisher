/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.figis.flod_publisher;

import com.hp.hpl.jena.graph.Graph;
import org.junit.Test;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class TaskStoreConsistency {
        @Test
        public void checkConsistency(){
            Graph microAsfisAdd = Utils.microAsfisAdd();
            Graph microAsfisExistenceCheck = Utils.microAsfisExistenceCheck();
            org.fao.fi.flod.publisher.store.task.TaskStoreConsistency.existance(microAsfisExistenceCheck,microAsfisAdd);
        }
}
