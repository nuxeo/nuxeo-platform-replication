package org.nuxeo.ecm.platform;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.replication.exporter.api.DocumentaryBaseExporterService;
import org.nuxeo.ecm.platform.replication.exporter.api.StatusListener;
import org.nuxeo.ecm.platform.replication.exporter.core.DocumentaryBaseExpServiceImpl;

public class ServiceTest extends SQLRepositoryTestCase {
    public static final String SCHEMA_BUNDLE = "org.nuxeo.ecm.core.schema";

    public static final String CORE_BUNDLE = "org.nuxeo.ecm.core";

    public ServiceTest() {
        super("default");
    }

    private static final Logger LOG = Logger.getLogger(ServiceTest.class);
    static {
        BasicConfigurator.configure();
        LOG.setLevel(Level.INFO);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle(CORE_BUNDLE);
        deployBundle(SCHEMA_BUNDLE);
        deployBundle("org.nuxeo.ecm.platform.versioning.api");
        deployBundle("org.nuxeo.ecm.platform.replication.exporter.api");
        deployBundle("org.nuxeo.ecm.platform.replication.exporter.core");
        openSession();
    }

    protected void createDataWarehouse() throws Exception {
        DocumentModel folder = session.createDocumentModel("/", "0001",
                "Folder");
        folder.setPropertyValue("dc:title", "folder");
        folder = session.createDocument(folder);
        folder.setPropertyValue("dc:language", "en");
        folder = session.saveDocument(folder);

        DocumentModel tagRoot = session.createDocumentModel("/", "0002", "File");
        tagRoot.setPropertyValue("dc:title", "TAGROOT");
        tagRoot = session.createDocument(tagRoot);
        tagRoot = session.saveDocument(tagRoot);
        session.save();

        DocumentModel tag1 = session.createDocumentModel(
                folder.getPathAsString(), "0003", "Folder");
        tag1.setPropertyValue("dc:title", "Label1");
        tag1 = session.createDocument(tag1);
        tag1 = session.saveDocument(tag1);
        session.save();

        DocumentModel tag2 = session.createDocumentModel(
                tag1.getPathAsString(), "0004", "File");
        tag2.setPropertyValue("dc:title", "Label2");
        tag2 = session.createDocument(tag2);
        tag2 = session.saveDocument(tag2);

        DocumentModel tag3 = session.createDocumentModel(
                tag1.getPathAsString(), "0005", "File");
        tag3.setPropertyValue("dc:title", "Label3");
        tag3 = session.createDocument(tag3);
        tag3 = session.saveDocument(tag3);

        DocumentModel file1 = session.createDocumentModel(
                folder.getPathAsString(), "0006", "File");
        file1.setPropertyValue("dc:title", "File1");
        file1 = session.createDocument(file1);
        file1 = session.saveDocument(file1);
        DocumentModel file2 = session.createDocumentModel(
                folder.getPathAsString(), "0007", "File");
        file2.setPropertyValue("dc:title", "File2");
        file2 = session.createDocument(file2);
        file2 = session.saveDocument(file2);
        DocumentModel file3 = session.createDocumentModel("/", "0008", "File");
        file3.setPropertyValue("dc:title", "File3");
        file3 = session.createDocument(file3);
        file3 = session.saveDocument(file3);
        session.save();
    }

    public void testService() throws Exception {
        // Login Framework.getService(LoginComponent.class);
        // System.setSecurityManager(s)

        createDataWarehouse();

        DocumentaryBaseExporterService srv = new DocumentaryBaseExpServiceImpl();
        srv.setListener(new StatusListener() {
            public void onUpdateStatus(Object... params) {
                LOG.info(params[0]);
            }
        });

        srv.export("test", null, new File(System.getProperty("user.home"),
                "test.folder"), true, true, true);
        //System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        ServiceTest st = new ServiceTest();
        st.setUp();
        st.testService();
        st.tearDown();
    }
}
