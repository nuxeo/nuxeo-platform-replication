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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;

/**
 * Implementation for reporter.
 *
 * @author cpriceputu
 */
public class NXReporter implements Reporter {

    private File location;

    private RandomAccessFile rndFile;

    private static final Logger LOG = Logger.getLogger(NXReporter.class);

    public NXReporter(File location) throws ClientException {
        try {
            setLocation(location);
            LOG.info(rndFile.getChannel().size());
        } catch (IOException e) {
            throw new ClientException("Failed to create logger.", e);
        }
    }

    public String getHeader() throws ClientException {
        StringBuilder sb = new StringBuilder();

        try {
            synchronized (this) {
                int c = 0;
                rndFile.seek(0);

                while ((c = rndFile.read()) != -1) {
                    if (c != '\n') {
                        sb.append((char) c);
                    } else {
                        break;
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public String getLastEntry() throws ClientException {
        String lastEntry;
        synchronized (this) {
            lastEntry = getLastEntryNoSync();
        }
        return lastEntry;
    }

    private String getLastEntryNoSync() throws ClientException {
        try {
            int c = 0;
            goBeginLastValidValue();
            StringBuilder sb = new StringBuilder();
            while ((c = rndFile.read()) != -1) {
                if (c != '\n') {
                    sb.append((char) c);
                } else {
                    break;
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public int getNumberOfDocuments() throws ClientException {
        try {
            int newLineCount = 0;
            synchronized (this) {
                int count = 0;
                int c = 0;
                goToEndLastValidValue();
                long end = rndFile.getFilePointer();
                goEndHeader();
                while ((c = rndFile.read()) != -1) {
                    count++;
                    if (c == '\n') {
                        newLineCount++;
                    }

                    if (count == end) {
                        break;
                    }
                }
            }
            return newLineCount;
        } catch (Exception e) {
            throw new ClientException(e);
        }

    }

    private int getSize(String entry) {
        String[] entryParts = entry.trim().split("\\s");
        return Integer.parseInt(entryParts[entryParts.length - 1].trim());
    }

    public long getTotalSize() throws ClientException {
        try {
            long size = 0;
            synchronized (this) {
                int count = 0;
                int c = 0;
                goToEndLastValidValue();
                long end = rndFile.getFilePointer();
                goEndHeader();
                StringBuilder sb = new StringBuilder();
                while ((c = rndFile.read()) != -1) {
                    count++;
                    sb.append((char) c);
                    if (c == '\n') {
                        size += getSize(sb.toString());
                        sb.setLength(0);
                    }
                    if (count == end) {
                        break;
                    }
                }
            }
            return size;
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public boolean hasHeader() throws IOException {
        rndFile.getFD().sync();
        if (rndFile.length() == 0) {
            return false;
        }
        int c = 0;
        long ptr = rndFile.getFilePointer();
        StringBuilder sb = new StringBuilder();
        rndFile.seek(0);
        while (true) {
            c = rndFile.read();
            if (c != -1) {
                sb.append((char) c);
                if (sb.length() >= 2) {
                    if ((sb.charAt(sb.length() - 1) == '-')
                            && (sb.charAt(sb.length() - 2) == '\n')) {

                        rndFile.seek(ptr);
                        return true;
                    }
                }

            } else {
                break;
            }
        }
        rndFile.seek(ptr);
        return false;
    }

    public void writeEntry(String documentId, int blobsSize)
            throws ClientException {
        try {
            synchronized (this) {
                rndFile.getFD().sync();
                if (!hasHeader()) {
                    writeHeaderNoSync("test", true);
                }
                goToEndLastValidValue();
                StringBuilder sb = new StringBuilder();
                sb.setLength(0);
                sb.append(documentId);
                sb.append("   ");
                sb.append(blobsSize);
                sb.append('\n');
                rndFile.write(sb.toString().getBytes());
            }
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public void writeHeader(String documentaryScope, boolean resume)
            throws ClientException {
        synchronized (this) {
            writeHeaderNoSync(documentaryScope, resume);
        }
    }

    private void goToEndLastValidValue() throws IOException {
        long count = 0;
        long index = -1;
        rndFile.seek(0);
        int c = 0;
        while ((c = rndFile.read()) != -1) {
            count++;
            if (c == '\n') {
                index = count;
            }
        }
        rndFile.seek(index);
    }

    private void goEndHeader() throws IOException {
        long count = 0;
        long index = -1;
        rndFile.seek(0);
        int c = 0, c1 = 0;
        while ((c = rndFile.read()) != -1) {
            count++;
            if ((c == '\n') && (c1 == '-')) {
                index = count;
            } else {
                c1 = c;
            }
        }
        rndFile.seek(index);
    }

    private void goBeginLastValidValue() throws IOException {
        long count = 0;
        long oldIndex = -1;
        long newIndex = -1;
        rndFile.seek(0);
        int c = 0;
        while ((c = rndFile.read()) != -1) {
            count++;
            if (c == '\n') {
                oldIndex = newIndex;
                newIndex = count;
            }
        }
        rndFile.seek(oldIndex == newIndex ? 0 : oldIndex);
    }

    public void writeHeaderNoSync(String documentaryScope, boolean resume)
            throws ClientException {
        long ptr = 0;
        try {
            ptr = rndFile.getFilePointer();
            rndFile.seek(0);
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            sb.append(documentaryScope);
            sb.append("   ");
            sb.append(resume);
            sb.append("   ");
            sb.append('-');
            sb.append('\n');
            rndFile.write(sb.toString().getBytes());
            rndFile.seek(ptr);
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public void setLocation(File location) throws IOException {
        this.location = location;
        if (rndFile != null) {
            rndFile.close();
            rndFile = null;
        }
        rndFile = new RandomAccessFile(this.location, "rw");
    }

    public File getLocation() {
        return location;
    }

}
