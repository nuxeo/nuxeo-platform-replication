package org.nuxeo.ecm.platform.replication.exporter.core;

import java.io.File;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentTreeReader;
import org.nuxeo.ecm.platform.replication.exporter.api.StatusListener;

public class UnrestrictedExporter extends UnrestrictedSessionRunner {
    private ReplicationPipe pipe = null;

    private StatusListener listener = null;

    private File path = null;

    public UnrestrictedExporter(String repositoryName, File path) {
        super(repositoryName);
        setPath(path);
    }

    @Override
    public void run() throws ClientException {
        try {
            DocumentModel root = session.getDocument(new PathRef("/"));
            DocumentTreeReader reader = new DocumentTreeReader(session, root,
                    false);
            // ((DocumentModelReader)reader).setInlineBlobs(true);
            DocumentWriter writer = new ReplicationWriter(path, session);

            pipe = new ReplicationPipe(10);
            pipe.setListener(listener);
            pipe.setReader(reader);
            pipe.setWriter(writer);
            pipe.run();

            if (getListener() != null) {
                getListener().onUpdateStatus(StatusListener.DONE);
            }

        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public void stop() {
        if (pipe != null)
            pipe.stop();
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public StatusListener getListener() {
        return listener;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }

}
