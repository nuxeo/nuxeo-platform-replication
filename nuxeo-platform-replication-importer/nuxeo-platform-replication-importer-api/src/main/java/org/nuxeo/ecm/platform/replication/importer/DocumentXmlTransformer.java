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

import org.dom4j.Document;
import org.nuxeo.ecm.core.api.ClientException;

/**
 * The transformer offering the chance to touch the document XML representation
 * (as in Core IO export form) before importing it. The transformation occurs in
 * memory directly in the DOM document. In order to use it just set the custom
 * implementation before running import {@link DocumentaryBaseImporterService}.
 * 
 * @author rux
 * 
 */
public interface DocumentXmlTransformer {

    /**
     * Transforms the XML document as it was exported before actual import. The
     * returned XML document is used in import instead the original one.
     * 
     * @param xmlDocument the XML representation of exported document
     * @return the XML document to be used instead. If null returned, the
     *         process is not impacted.
     * @throws ClientException
     */
    Document transform(Document xmlDocument) throws ClientException;
}
