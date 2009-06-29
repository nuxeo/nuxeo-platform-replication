package org.nuxeo.ecm.platform.replication.importer;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.importer.source.SourceNode;
import org.nuxeo.ecm.platform.importer.threading.ImporterThreadingPolicy;

public class MonoThreadPolicy implements ImporterThreadingPolicy {

    public boolean needToCreateThreadAfterNewFolderishNode(
            DocumentModel parent, SourceNode node, long uploadedSources,
            int batchSize, int scheduledTasks) {
        return false;
    }

}
