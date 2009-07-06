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

package org.nuxeo.ecm.platform.replication.exporter;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * The service which exports the documentary base. The system replication is
 * made inside a single directory named “Replication Root”. Under it,
 * “Documentary Base” contains the documents. Under it, the “Usual documents”
 * contains the repository exported muck likely the export utility. The file
 * names are the names of the documents. The path of Nuxeo documents is unique,
 * so it can be used without worrying of duplicates. We can find the usual
 * documents exported, with blobs, with workflow state (inside the document
 * folder a file named “workflow.export”). The JBPM workflow state of the
 * document is stored (lifecycle state is already saved in the document export)
 * although for the moment it is not of much use. But if required it can be
 * imagined a procedure to update the workflow state when importing based on
 * customer's particularities. And also a third new file named “import.export”
 * containing the contextual metadata required for core import.
 * <p>
 * Under “Documentary Base” the “Versions” folder contains the checked in
 * versions. The versions are exported as the usual documents with single
 * difference: without ACL and workflows.
 * <p>
 * Under “Documentary Base” the “Proxies” folder contains the proxies. They are
 * exported as the usual documents with single difference: without workflows.
 * 
 * 
 * @author cpriceputu
 * 
 */
public interface DocumentaryBaseExporterService {

    /**
     * Exports the documentary base.
     * 
     * @param session
     * @param parameter
     * @param path
     * @param resume
     * @param exportVersions
     * @param exportProxies
     * @throws ClientException
     */
    public void export(String domain, Map<String, Serializable> parameter,
            File path, boolean resume, boolean exportVersions,
            boolean exportProxies) throws ClientException;

    /**
     * Stops the export
     */
    public void stop();

    /**
     * Sets a listener that receives misc events
     * 
     * @param listener
     */
    public void setListener(StatusListener listener);
}
