package org.nuxeo.ecm.platform.replication.exporter.core;

import java.io.File;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.io.DocumentPipe;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentTreeReader;

public class UnrestrictedExporter extends UnrestrictedSessionRunner {

    public UnrestrictedExporter(String repositoryName) {
        super(repositoryName);
    }

    @Override
    public void run() throws ClientException {
        try {
            DocumentModel root = session.getDocument(new PathRef("/"));
            DocumentTreeReader reader = new DocumentTreeReader(session, root,
                    false);
            // ((DocumentModelReader)reader).setInlineBlobs(true);
            DocumentWriter writer = new ReplicationWriter(new File(
                    System.getProperty("user.home"), "test.folder"));

            DocumentPipe pipe = new DocumentPipeImpl(10);
            pipe.setReader(reader);
            pipe.setWriter(writer);
            pipe.run();

        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

}
