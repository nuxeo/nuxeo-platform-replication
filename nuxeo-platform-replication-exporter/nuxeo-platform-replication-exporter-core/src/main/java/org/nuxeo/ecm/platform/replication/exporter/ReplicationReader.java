package org.nuxeo.ecm.platform.replication.exporter;

import java.io.IOException;
import java.util.Iterator;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentModelReader;

public class ReplicationReader extends DocumentModelReader {

    protected Iterator<DocumentModel> iterator = null;

    protected ReplicationReader(CoreSession session) throws ClientException {
        super(session);
        iterator = session.query("SELECT * FROM Document").iterator();
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (iterator.hasNext()) {
            DocumentModel docModel = iterator.next();
            return new ExportedDocumentImpl(docModel, inlineBlobs);

        }
        return null;
    }

}
