package org.nuxeo.ecm.platform.replication.exporter.core;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.exporter.api.DocumentaryBaseExporterService;

public interface DocumentaryBaseExpServiceImplMBean extends
        DocumentaryBaseExporterService {

    public void export(String domain, Map<String, Serializable> parameter,
            File path, boolean resume, boolean exportVersions,
            boolean exportProxies) throws ClientException;

}
