package org.nuxeo.ecm.platform.replication.exporter;

import java.io.File;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.runtime.api.Framework;

public class Exporter implements ExporterMBean {

    private DocumentaryBaseExporterService service = null;

    private static final Logger LOG = Logger.getLogger(Exporter.class);

    public Exporter() throws Exception {
        try {
            Thread.currentThread().setContextClassLoader(
                    Framework.class.getClassLoader());
            service = Framework.getService(DocumentaryBaseExporterService.class);
        } catch (Exception e) {
            LOG.fatal("Error", e);
        }
    }

    public void export(String domain, File path) throws ClientException {
        service.export(domain, null, path, false, false, false);
    }

    public void stop() {
        service.stop();
    }

}
