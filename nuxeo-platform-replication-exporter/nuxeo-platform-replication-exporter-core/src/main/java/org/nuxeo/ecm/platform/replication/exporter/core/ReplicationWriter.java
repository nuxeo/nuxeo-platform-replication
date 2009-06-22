package org.nuxeo.ecm.platform.replication.exporter.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.plugins.XMLDirectoryWriter;

public class ReplicationWriter extends XMLDirectoryWriter {
    private static final Logger LOG = Logger.getLogger(ReplicationWriter.class);

    public ReplicationWriter(File file) throws IOException {
        super(file);
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument doc)
            throws IOException {
        LOG.info(doc);
        return super.write(doc);
    }
}
