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

package org.nuxeo.ecm.platform;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.common.Reporter;
import org.nuxeo.ecm.platform.replication.common.ReporterFactory;
import org.nuxeo.ecm.platform.replication.common.ReporterFactoryService;

public class ReporterTest {

    private static final Log log = LogFactory.getLog(ReporterTest.class);
    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws Exception {
        new ReporterTest().testFileChannel();
    }

    @Test
    public void testSingleFileChannel() throws Exception {
        ReporterFactoryService rfs = new ReporterFactory();
        Reporter rp = rfs.getReporter(new File(System.getProperty("user.home"),
                "teste.synchro"));

        rp.writeEntry(UUID.randomUUID().toString(), 3000);

        log.info(rp.getLastEntry());
        log.info("Size of documents: " + rp.getTotalSize());
    }

    @Test
    public void testFileChannel() throws Exception {
        Executor exex = Executors.newCachedThreadPool();
        for (int i = 0; i < 130; i++) {
            exex.execute(new Runner());
        }
    }

}
class Runner implements Runnable {

    private static final ReporterFactoryService rfs = new ReporterFactory();
    private static final Log log = LogFactory.getLog(Runner.class);

    public Runner() {}

    public void run() {
        try {
            Reporter rp = rfs.getReporter(new File(
                    System.getProperty("user.home"), "teste.synchro"));
            rp.writeEntry(UUID.randomUUID().toString(),
                    (int) System.currentTimeMillis());
        } catch (ClientException e) {
            log.error(e);
        }
    }

}
