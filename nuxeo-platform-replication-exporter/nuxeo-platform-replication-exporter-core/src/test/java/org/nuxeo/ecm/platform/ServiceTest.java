/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package org.nuxeo.ecm.platform;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.exporter.DocumentaryBaseExpServiceImpl;
import org.nuxeo.ecm.platform.replication.exporter.DocumentaryBaseExporterService;


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

    @Before
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

    @Test
    public void testService() throws Exception {
        createDataWarehouse();
        DocumentaryBaseExporterService srv = new DocumentaryBaseExpServiceImpl();
        srv.setListener(new StatusListener() {
            public void onUpdateStatus(Object... params) {
                LOG.info(params[0]);
            }
        });
        srv.export("test", null, new File(System.getProperty("user.home"),
                "test.folder"), true, true, true);
        //simple avoid closing session before test completes
        Thread.sleep(10000);
    }

    @Test
    public void testDocumentOrder() throws Exception {
    	 // create the ordered folder
    	 DocumentModel folder = session.createDocumentModel("/", "0001", "OrderedFolder");
    	 folder.setPropertyValue("dc:title", "Ordered Folder");
    	 folder = session.createDocument(folder);
    	 
    	 // create files
    	 List<String> ids =  new ArrayList<String>();
    	 for ( int i = 0 ; i < 5 ; i++ ){
    		 String name = "file" + i;
    		 DocumentModel file = session.createDocumentModel( folder.getPathAsString(), name, "File");
    	     file.setPropertyValue("dc:title", name);
    	     file = session.createDocument(file);
    	     ids.add(file.getId());
    	 }
    	 session.save();
    	 
    	 // change the creation order to 1 2 0 3 4 
    	 session.orderBefore(folder.getRef(), "file0", "file3");
    	 session.save();
    	 // update the list of ids 
    	 String id = ids.remove(0);
    	 ids.add(2, id);
    	 
    	 DocumentaryBaseExporterService srv = new DocumentaryBaseExpServiceImpl();
    	 File exportDirectory = new File ( System.getProperty("java.io.tmpdir"), "testExportOrderedFolder");
    	 exportDirectory.deleteOnExit();
    	 
    	 srv.export("test", null, exportDirectory, true, true, true);
    	 //simple avoid closing session before test completes
    	 Thread.sleep(3000);
    	 
    	 Properties properties = new Properties();
    	 File propFile = new File( exportDirectory, "Documentary Base/Usual documents/0001/metadata.properties");
    	 FileInputStream fis = new FileInputStream(propFile);
    	 properties.load(fis);
    	 
    	 
    	 String p = properties.getProperty("ecm:childrenOrder");
    	 assertEquals(StringUtils.join(ids, ";"), p);
    }

}
