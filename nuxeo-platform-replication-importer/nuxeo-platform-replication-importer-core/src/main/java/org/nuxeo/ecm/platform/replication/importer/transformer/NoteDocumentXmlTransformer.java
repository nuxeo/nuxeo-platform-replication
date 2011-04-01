/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 * Contributors:
 *     Sun Seng David TAN <stan@nuxeo.com>
 */
package org.nuxeo.ecm.platform.replication.importer.transformer;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.importer.DocumentXmlTransformer;

/**
 * Note schema has changed since NXP-5837 (5.4) and is causing issue while
 * document are imported. This transformer alter the xml dom before importing
 * the the new repository, removing the unused fields that would fail the import
 * (fields doesn't exist).
 *
 * @author Sun Seng David TAN <stan@nuxeo.com>
 *
 */
public class NoteDocumentXmlTransformer implements DocumentXmlTransformer {

    @Override
    public Document transform(Document xmlDocument) throws ClientException {
        Map<String, String> noteNamespacesUri = new HashMap<String, String>();
        noteNamespacesUri.put("note", "http://www.nuxeo.org/ecm/schemas/note/");
        XPath xPath = DocumentHelper.createXPath("//note:schema[@name=\"note\"]");
        xPath.setNamespaceURIs(noteNamespacesUri);
        Node node = xPath.selectSingleNode(xmlDocument);
        if (node == null) {
            return xmlDocument;
        }

        xPath = DocumentHelper.createXPath("//note:transform_result");
        xPath.setNamespaceURIs(noteNamespacesUri);
        node = xPath.selectSingleNode(xmlDocument);
        if (node != null) {
            node.detach();
        }

        xPath = DocumentHelper.createXPath("//note:stylesheet_filename");
        xPath.setNamespaceURIs(noteNamespacesUri);
        node = xPath.selectSingleNode(xmlDocument);
        if (node != null) {
            node.detach();
        }

        xPath = DocumentHelper.createXPath("//note:stylesheet_content");
        xPath.setNamespaceURIs(noteNamespacesUri);
        node = xPath.selectSingleNode(xmlDocument);
        if (node != null) {
            node.detach();
        }
        return xmlDocument;
    }

}
