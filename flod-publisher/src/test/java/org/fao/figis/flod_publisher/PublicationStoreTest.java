/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fao.figis.flod_publisher;

import java.net.MalformedURLException;
import java.net.URL;
import org.fao.fi.flod.publisher.store.PublicationStore;
import org.fao.fi.flod.publisher.utils.Utils;
import org.junit.Test;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class PublicationStoreTest {
    
    @Test
    public void backupGraph() throws MalformedURLException{
        URL url = new URL("http://semanticrepository/graph/test");
        
        PublicationStore.getInstance().backup(url);
    }
    
    @Test
    public void publish() throws MalformedURLException{
        URL url = new URL("http://semanticrepository/graph/test");
        
        PublicationStore.getInstance().publish(Utils.fooModel().getGraph(), url);

    }
    
}
