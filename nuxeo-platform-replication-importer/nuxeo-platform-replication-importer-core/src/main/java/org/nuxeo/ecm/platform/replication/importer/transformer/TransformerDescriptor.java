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

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.replication.importer.DocumentXmlTransformer;

/**
 * Contribution for a importer transformer to modify the xml before performing
 * the import. Maybe useful if the structure has been changed.
 *
 * @author Sun Seng David TAN <stan@nuxeo.com>
 *
 */
@XObject("transformer")
public class TransformerDescriptor {
    @XNode("@class")
    public Class<DocumentXmlTransformer> clazz;

}
