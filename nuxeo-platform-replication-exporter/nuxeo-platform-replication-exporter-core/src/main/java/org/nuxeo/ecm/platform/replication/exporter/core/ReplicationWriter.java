package org.nuxeo.ecm.platform.replication.exporter.core;

import static org.nuxeo.ecm.platform.replication.exporter.core.ReplicationConstants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.VersionModel;
import org.nuxeo.ecm.core.api.facet.VersioningDocument;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.plugins.XMLDirectoryWriter;
import org.nuxeo.ecm.core.schema.types.primitives.DateType;

public class ReplicationWriter extends XMLDirectoryWriter {
    private static final Logger LOG = Logger.getLogger(ReplicationWriter.class);

    private CoreSession session = null;

    public ReplicationWriter(File file, CoreSession session) throws IOException {
        super(file);
        this.session = session;
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument doc)
            throws IOException {
        super.write(doc);
        LOG.info(doc);

        try {
            DocumentModel document = session.getDocument(new IdRef(doc.getId()));
            Properties metadata = getDocumentMetadata(session, document);
            File parent = new File(getDestination().toString(),
                    doc.getPath().toString());

            File metadataFile = new File(parent, "metadata.properties");
            metadata.store(new FileOutputStream(metadataFile),
                    "Document Metadata");

        } catch (Exception e) {
            LOG.error(e);
        }
        return null;
    }

    public static Properties getDocumentMetadata(CoreSession documentManager,
            DocumentModel document) throws ClientException, DocumentException {
        Properties props = new Properties();

        DocumentRef ref = document.getRef();

        if (document.isProxy()) {
            DocumentModel version = documentManager.getSourceDocument(ref);
            DocumentModel sourceDocument = documentManager.getSourceDocument(version.getRef());

            props.setProperty(IMPORT_PROXY_TARGET_ID,
                    version.getId() == null ? "" : version.getId());
            props.setProperty(IMPORT_PROXY_VERSIONABLE_ID,
                    sourceDocument.getId() == null ? ""
                            : sourceDocument.getId());
        } else if (document.isVersion()) {
            DocumentModel sourceDocument = documentManager.getSourceDocument(ref);

            props.setProperty(IMPORT_VERSION_VERSIONABLE_ID,
                    sourceDocument.getId() == null ? ""
                            : sourceDocument.getId());
            props.setProperty(IMPORT_VERSION_LABEL,
                    document.getVersionLabel() == null ? ""
                            : document.getVersionLabel());

            List<VersionModel> versions = documentManager.getVersionsForDocument(sourceDocument.getRef());
            for (VersionModel version : versions) {
                // add version description
                props.setProperty(IMPORT_VERSION_DESCRIPTION,
                        version.getDescription() == null ? ""
                                : version.getDescription());
                // add version creation date
                props.setProperty(
                        IMPORT_VERSION_CREATED,
                        new DateType().encode(version.getCreated()) == null ? ""
                                : new DateType().encode(version.getCreated()));
            }

            VersioningDocument docVer = document.getAdapter(VersioningDocument.class);
            String minorVer = docVer.getMinorVersion().toString();
            String majorVer = docVer.getMajorVersion().toString();

            props.setProperty(IMPORT_VERSION_MAJOR,
                    majorVer == null ? "" : majorVer);

            props.setProperty(IMPORT_VERSION_MINOR,
                    minorVer == null ? "" : minorVer);
        } else {

            props.setProperty(IMPORT_LOCK,
                    document.getLock() == null ? "" : document.getLock());
            if (document.isVersionable()) {
                props.setProperty(IMPORT_CHECKED_IN,
                        Boolean.FALSE.toString());
                // add the id of the last version, which represents the base for
                // the current state of the document
                DocumentModel version = documentManager.getLastDocumentVersion(ref);
                if ((version != null)
                        && version.getId().equals(document.getId())) {
                    props.setProperty(IMPORT_BASE_VERSION_ID,
                            version.getId() == null ? "" : version.getId());
                }
                VersioningDocument docVer = document.getAdapter(VersioningDocument.class);
                if (docVer != null) {
                    String minorVer = docVer.getMinorVersion().toString();
                    String majorVer = docVer.getMajorVersion().toString();
                    // add major version
                    props.setProperty(IMPORT_VERSION_MAJOR,
                            majorVer == null ? "" : majorVer);
                    // add minor version
                    props.setProperty(IMPORT_VERSION_MINOR,
                            minorVer == null ? "" : minorVer);
                }
            }

        }

        props.setProperty(IMPORT_LIFECYCLE_STATE,
                document.getCurrentLifeCycleState() == null ? ""
                        : document.getCurrentLifeCycleState());
        props.setProperty(IMPORT_LIFECYCLE_POLICY,
                document.getLifeCyclePolicy() == null ? ""
                        : document.getLifeCyclePolicy());

        return props;
    }
}
