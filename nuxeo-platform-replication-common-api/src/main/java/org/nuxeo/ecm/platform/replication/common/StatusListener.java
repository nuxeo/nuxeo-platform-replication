/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.replication.common;

/**
 * Listener used to provide information about the process that is
 * running(import/export)
 *
 * @author cpriceputu
 *
 */
public interface StatusListener {

    int OK = 0;

    int ERROR = 1;

    int DOC_PROCESS_SUCCESS = 2;

    int PROCESS_STOPPED = 3;

    int STARTED = 4;

    int DONE = 5;

    void onUpdateStatus(Object... params);

}
