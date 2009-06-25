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

package org.nuxeo.ecm.platform.replication.importer;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.importer.base.GenericMultiThreadedImporter;
import org.nuxeo.ecm.platform.importer.executor.AbstractImporterExecutor;
import org.nuxeo.ecm.platform.replication.importer.DocumentaryBaseImporterService;

/**
 * Implementation for import documentary base service.
 * 
 * @author rux
 * 
 */
public class DocumentaryBaseImpServiceImpl extends AbstractImporterExecutor
        implements DocumentaryBaseImporterService {
    private static final Log log = LogFactory.getLog(DocumentaryBaseImpServiceImpl.class);

    protected CoreSession session;

    public void importDocuments(CoreSession session,
            Map<String, Serializable> parameter, File path, boolean resume,
            boolean exportVersions, boolean exportProxies)
            throws ClientException {

//        GenericMultiThreadedImporter importer = new GenericMultiThreadedImporter(source, targetPath, batchSize, nbTheards, getLogger());
//        importer.setFactory(getFactory());
//        importer.setThreadPolicy(getThreadPolicy());
//        doRun(importer, null);
    }

    @Override
    protected CoreSession getCoreSession() {
        return session;
    }

    @Override
    protected Log getJavaLogger() {
        return log;
    }

}
