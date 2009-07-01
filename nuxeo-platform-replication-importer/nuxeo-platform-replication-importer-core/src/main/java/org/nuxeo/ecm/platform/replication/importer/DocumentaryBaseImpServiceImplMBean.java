package org.nuxeo.ecm.platform.replication.importer;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

public interface DocumentaryBaseImpServiceImplMBean extends
        DocumentaryBaseImporterService {

    void importDocuments(CoreSession session,
            Map<String, Serializable> parameter, File path, boolean resume,
            boolean exportVersions, boolean exportProxies,
            boolean useMultiThread) throws ClientException;

    void stop();

    void setListener(StatusListener listener);

    void setXmlTransformer(DocumentXmlTransformer xmlTransformer);
}
