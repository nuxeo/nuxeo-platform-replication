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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * The MBean implementation.
 * 
 * @author btatar
 * 
 */
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
