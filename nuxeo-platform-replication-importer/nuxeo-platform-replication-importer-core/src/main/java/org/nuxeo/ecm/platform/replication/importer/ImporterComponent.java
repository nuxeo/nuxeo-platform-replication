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
package org.nuxeo.ecm.platform.replication.importer;

import org.nuxeo.ecm.platform.replication.importer.transformer.TransformerDescriptor;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Importer component based class. Provides DocumentaryBaseService and manage
 * transformer extension point.
 * 
 * @author Sun Seng David TAN <stan@nuxeo.com>
 * 
 */
public class ImporterComponent extends DefaultComponent {

    DocumentaryBaseImpServiceImpl documentaryBaseService;

    @Override
    public void activate(ComponentContext context) throws Exception {
        documentaryBaseService = new DocumentaryBaseImpServiceImpl();
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        documentaryBaseService = null;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (!"transformer".equals(extensionPoint)) {
            return;
        }

        TransformerDescriptor descriptor = (TransformerDescriptor) contribution;
        
        documentaryBaseService.setXmlTransformer(descriptor.clazz.newInstance());

    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.isAssignableFrom(DocumentaryBaseImporterService.class)) {
            return adapter.cast(documentaryBaseService);
        }
        return super.getAdapter(adapter);
    }

}
