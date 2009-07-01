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
package org.nuxeo.ecm.platform.replication.common;

/**
 * Utility class used for constants.
 * 
 * @author btatar
 * 
 */
public class ReplicationConstants {

    private ReplicationConstants() {

    }

    // used to pass properties to importDocument
    public static final String IMPORT_VERSION_VERSIONABLE_ID = "ecm:versionableId";

    public static final String IMPORT_VERSION_CREATED = "ecm:versionCreated";

    public static final String IMPORT_VERSION_LABEL = "ecm:versionLabel";

    public static final String IMPORT_VERSION_DESCRIPTION = "ecm:versionDescription";

    public static final String IMPORT_VERSION_MAJOR = "ecm:majorVersion";

    public static final String IMPORT_VERSION_MINOR = "ecm:minorVersion";

    public static final String IMPORT_PROXY_TARGET_ID = "ecm:proxyTargetId";

    public static final String IMPORT_PROXY_VERSIONABLE_ID = "ecm:proxyVersionableId";

    public static final String IMPORT_LIFECYCLE_POLICY = "ecm:lifeCyclePolicy";

    public static final String IMPORT_LIFECYCLE_STATE = "ecm:lifeCycleState";

    public static final String IMPORT_LOCK = "ecm:lock";

    public static final String IMPORT_DIRTY = "ecm:dirty";

    public static final String IMPORT_CHECKED_IN = "ecm:isCheckedIn";

    public static final String IMPORT_BASE_VERSION_ID = "ecm:baseVersion";

    /** The document type to use to create a proxy by import. */
    public static final String IMPORT_PROXY_TYPE = "ecm:proxy";
}
