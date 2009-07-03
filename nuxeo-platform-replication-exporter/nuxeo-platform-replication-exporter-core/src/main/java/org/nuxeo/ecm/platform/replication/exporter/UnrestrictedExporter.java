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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.io.DocumentReader;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * Used to run as system.
 * 
 * @author cpriceputu@nuxeo.com
 * 
 */
public class UnrestrictedExporter extends UnrestrictedSessionRunner {
	private ReplicationPipe pipe = null;

	private StatusListener listener = null;

	private File path = null;

	public UnrestrictedExporter(String repositoryName, File path) {
		super(repositoryName);
		setPath(path);
	}

	@Override
	public void run() throws ClientException {
		try {

			DocumentReader reader = new ReplicationReader(session);
			DocumentWriter writer = new ReplicationWriter(path, session);

			pipe = new ReplicationPipe(10);
			pipe.setListener(listener);
			pipe.setReader(reader);
			pipe.setWriter(writer);
			pipe.run();

			if (getListener() != null) {
				getListener().onUpdateStatus(StatusListener.DONE);
			}

		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	public void stop() {
		if (pipe != null)
			pipe.stop();
	}

	public void setListener(StatusListener listener) {
		this.listener = listener;
	}

	public StatusListener getListener() {
		return listener;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public File getPath() {
		return path;
	}

}
