package org.nuxeo.ecm.platform.replication.exporter;

import java.io.File;

import org.nuxeo.ecm.core.api.ClientException;

public interface ExporterMBean {
    public void export(String domain, File path) throws ClientException;

    public void stop();
}
