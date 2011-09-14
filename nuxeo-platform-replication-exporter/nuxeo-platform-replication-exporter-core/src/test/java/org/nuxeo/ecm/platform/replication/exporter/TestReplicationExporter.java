/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors: Sun Seng David TAN <stan@nuxeo.com>
 */
package org.nuxeo.ecm.platform.replication.exporter;

import java.io.File;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.repository.jcr.testing.RepositoryOSGITestCase;

public class TestReplicationExporter extends RepositoryOSGITestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.platform.replication.exporter.api");
        deployBundle("org.nuxeo.ecm.platform.replication.exporter.core");
        openRepository();
    }

    /**
     * Testing the replication exporter and make sure it doesn't remove the
     * cariage return and trim.
     *
     * @throws Exception
     */
    public void TestReplicationExporterWithoutTrim() throws Exception {
        // create a File doc with a
        CoreSession session = getCoreSession();
        DocumentModel doc = session.createDocumentModel("/", "test", "File");
        String dublincoreTitleValue = "this is a test with spaces  and cariage\nreturn";
        doc.setPropertyValue("dublincore:title", dublincoreTitleValue);
        doc = session.createDocument(doc);
        session.save();

        // process the export
        File tempExportDir = new File(System.getProperty("java.io.tmpdir")
                + File.separatorChar + "export"
                + TestReplicationExporter.class.getSimpleName());
        UnrestrictedExporter exporter = new UnrestrictedExporter(session,
                tempExportDir);
        exporter.run();

        // Read file from file
        String fileXml = FileUtils.readFile(new File(tempExportDir.getPath()
                + File.separator + "Documentary Base" + File.separator
                + "Usual documents" + File.separator + "test" + File.separator
                + "document.xml"));

        FileUtils.deleteTree(tempExportDir);
        // Making sure the xml file contains the same string (with CR and
        // spaces)
        assertTrue(fileXml.contains(dublincoreTitleValue));
    }

}
