package org.nuxeo.ecm.platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.replication.importer.DocumentaryBaseImporterService;
import org.nuxeo.runtime.api.Framework;

public class TestImport extends SQLRepositoryTestCase {

    public TestImport(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
        deployBundle("org.nuxeo.ecm.core");
        deployBundle("org.nuxeo.ecm.core.schema");
        deployBundle("org.nuxeo.ecm.platform.replication.importer.api");
        deployContrib("org.nuxeo.ecm.platform.replication.importer.core","OSGI-INF/ImporterService.xml");
        openSession();
    }

    private File getArchiveFile() throws ZipException, IOException {
        File zip = new File(FileUtils.getResourcePathFromContext("DocumentaryBase.zip"));
        ZipFile arch = new ZipFile(zip);

        Path basePath = new Path(System.getProperty("java.io.tmpdir")).append("TestImport").append(System.currentTimeMillis() + "");
        new File(basePath.toString()).mkdirs();
        Enumeration entries = arch.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            InputStream in = arch.getInputStream(entry);
            File out = new File(basePath.append(entry.getName()).toString());
            if (entry.isDirectory()) {
                out.mkdirs();
            }
            else {
                out.createNewFile();
                FileUtils.copyToFile(in, out);
            }
            in.close();
        }
        return new File(basePath.toString());
    }

    public void testImport() throws Exception {

        DocumentModel root = session.getRootDocument();
        assertNotNull(root);
        String rootUUID = root.getRef().toString();

        DocumentModelList children = session.getChildren(root.getRef());
        assertEquals(0,children.size());

        DocumentaryBaseImporterService importer = Framework.getLocalService(DocumentaryBaseImporterService.class);
        assertNotNull(importer);

        File archiveDir = getArchiveFile();
        assertTrue(archiveDir.exists());
        assertTrue(archiveDir.list().length>0);
        importer.importDocuments(session, null, archiveDir, false, true, true, false);

        root = session.getRootDocument();
        assertNotNull(root);

        IdRef ref = new IdRef("db6646ea-dd30-42a3-aec5-2e40a7a79511");
        assertTrue(session.exists(ref));
        DocumentModel doc = session.getDocument(ref);
        assertEquals("Workspace", doc.getType());
        assertEquals("ws", doc.getTitle());

        ref = new IdRef("c114da8f-cb7c-424d-9419-3f483db2390a");
        assertTrue(session.exists(ref));
        doc = session.getDocument(ref);
        assertEquals("Domain", doc.getType());
        //assertEquals("Default domain", doc.getTitle());

        children = session.getChildren(root.getRef());
        assertEquals(1,children.size());
        session.hasChildren(new IdRef("c114da8f-cb7c-424d-9419-3f483db2390a"));

    }

}
