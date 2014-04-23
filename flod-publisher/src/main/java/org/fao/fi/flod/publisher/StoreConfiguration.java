package org.fao.fi.flod.publisher;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import java.net.URI;
import java.util.Properties;
import static org.fao.fi.flod.publisher.utils.Utils.*;

public class StoreConfiguration {

    private final Properties properties;

    public StoreConfiguration(Properties properties) {

        this.properties = properties;

        notNull("configuration", properties);
        notNull("read data endpoing", properties.getProperty("endpoint_data"));
        notNull("update sparql endpoing", properties.getProperty("endpoint_update"));
        notNull("read only sparql endpoint", properties.getProperty("endpoint_query"));
        notNull("backup sparql endpoint", properties.getProperty("endpoint_backup"));
        notNull("read task sparql endpoint", properties.getProperty("endpoint_task_query"));
        notNull("read task data endpoint", properties.getProperty("endpoint_task_data"));
        notNull("udpate task sparql endpoint", properties.getProperty("endpoint_task_update"));
        notNull("retrieve the description of a publication task", properties.getProperty("query_publication_tasks"));
    }

    public URI getQueryEndpointURL() {
        return URI.create(properties.getProperty("endpoint_query"));
    }

    public URI getDataEndpointURL() {
        return URI.create(properties.getProperty("endpoint_data"));
    }

    public URI getUpdateEndpointURL() {
        return URI.create(properties.getProperty("endpoint_update"));
    }

    public URI getBackupEndpointURL() {
        return URI.create(properties.getProperty("endpoint_backup"));
    }

    public URI getTaskEndpointURL() {
        return URI.create(properties.getProperty("endpoint_task_query"));
    }

    public URI getTaskUpdateEndpointURL() {
        return URI.create(properties.getProperty("endpoint_task_update"));
    }

    public URI getDataTaskEndpointURL() {
        return URI.create(properties.getProperty("endpoint_task_data"));
    }

    public Query query_listPublicationTasks() {
        String query = properties.getProperty("query_publication_tasks");
        return QueryFactory.create(query);
    }
}
