// $Id: PDFImporter.java 20861 2015-02-11 10:58:38Z mike $

package org.faceless.pdf2.viewer3.feature;

import javax.swing.filechooser.FileFilter;
import javax.swing.UIManager;
import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;

/**
 * <p>
 * A subclass of {@link Importer} that allows PDF files to be loaded
 * into the viewer. This feature is essential for regular operation
 * of the Viewer - if not included in the list of features passed
 * into the {@link PDFViewer} constructor, it will be added automatically.
 * </p><p>
 * To customize the list of {@link EncryptionHandler}s used when loading a
 * PDF, this class can be altered. For example, to always use a specific
 * password when loading a PDF you could do the following.
 * <pre class="example">
 * <i>// First, get rid of the default PDFImporter from the list</i>
 * Collection features = new ArrayList(ViewerFeature.getDefaultFeatures());
 * for (Iterator i = features.iterator();i.hasNext();) {
 *     if (i.next() instanceof PDFImporter) {
 *         i.remove();
 *     }
 * }
 *
 * <i>// Next, create a new PDFImporter and set its EncryptionHandler</i>
 * PDFImporter importer = new PDFImporter();
 * Set handlers = importer.getEncryptionHandlers();
 * handlers.clear();
 * StandardEncryptionHandler pwhandler = new StandardEncryptionHandler();
 * pwhandler.setUserPassword("secret");
 * handlers.add(pwhandler);
 *
 * <i>// Add that feature to the list and pass it into the PDFViewer</i>
 * features.add(importer);
 * PDFViewer viewer = new PDFViewer(features);
 * </pre>
 * <div class="initparams">
 * The following <a href="../doc-files/initparams.html">initialization parameters</a> can be specified to configure this feature.
 * <table summary="">
 * <tr><th>useInputStream</th><td>If set to 'true' the {@link #setUseInputStream} method will be called with true as an argument.</td></tr>
 * </table>
 * </div>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PDFImporter</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.10.2
 */
public class PDFImporter extends Importer {

    private Set<EncryptionHandler> encryptionhandlers;
    private FileFilter filefilter;
    private boolean useinputstream;

    /**
     * Create a new PDFImporter
     */
    public PDFImporter() {
        super("PDFImporter");
        encryptionhandlers = new LinkedHashSet<EncryptionHandler>();
        filefilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".pdf");
            }
            public String getDescription() {
                return UIManager.getString("PDFViewer.FilesPDF");
            }
        };
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        encryptionhandlers.add(new PasswordPromptEncryptionHandler(viewer));
        encryptionhandlers.add(new PublicKeyPromptEncryptionHandler(viewer, viewer.getKeyStoreManager()));
        String val = getFeatureProperty(viewer, "useInputStream");
        if (val != null) {
            setUseInputStream("true".equals(val));
        }

    }

    public FileFilter getFileFilter() {
        return filefilter;
    }

    public boolean matches(final File file) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                public Boolean run() throws IOException {
                    boolean found = false;
                    if (file.length() > 200) {
                        org.faceless.util.SemiSeekableInputStream in = new org.faceless.util.SemiSeekableInputStream(new FileInputStream(file));
                        found = in.search(new byte[] { '%', 'P', 'D', 'F', '-', '1' }, 1032);
                        in.close();
                    }
                    return Boolean.valueOf(found);
                }
            }).booleanValue();
        } catch (PrivilegedActionException e) {
            Exception e2 = e.getException();
            if (e2 instanceof IOException) {
                throw (IOException)e2;
            } else {
                throw (RuntimeException)e2;
            }
        }
    }

    /**
     * Return the set of {@link EncryptionHandler} objects used to possibly decrypt
     * PDF files loaded with this Importer. This set may be altered.
     */
    public Set<EncryptionHandler> getEncryptionHandlers() {
        return encryptionhandlers;
    }

    /**
     * When opening a PDF from a File, this flag determines whether
     * to open the PDF using the {@link PDFReader#PDFReader(File) File}
     * constructor or {@link PDFReader#PDFReader(InputStream) InputStream}
     * PDFReader constructor.
     * @param stream whether to load PDF files from a FileInputStream (true) or a File (false)
     * @since 2.11.3
     */
    public void setUseInputStream(boolean stream) {
        this.useinputstream = stream;
    }

    public ImporterTask getImporter(PDFViewer viewer, File file) {
        return getImporter(viewer, null, null, file);
    }

    public ImporterTask getImporter(final PDFViewer viewer, final InputStream in, final String title, final File file) {
        return new ImporterTask(viewer, in, title, file) {
            private PDFReader reader = new PDFReader();
            public float getProgress() {
                return reader.getProgress();
            }
            public PDF loadPDF() throws IOException {
                try {
                    return AccessController.doPrivileged(new PrivilegedExceptionAction<PDF>() {
                        public PDF run() throws IOException {
                            if (in != null) {
                                reader.setSource(in);
                            } else if (useinputstream) {
                                reader.setSource(new FileInputStream(file));
                            } else {
                                reader.setSource(file);
                            }
                            for (EncryptionHandler handler : getEncryptionHandlers()) {
                                if (handler != null) {
                                    reader.addEncryptionHandler(handler);
                                }
                            }
                            reader.load();
                            if (!isCancelled()) {
                                return new PDF(reader);
                            } else {
                                return null;
                            }
                        }
                    });
                } catch (PrivilegedActionException e) {
                    throw (IOException)e.getException();
                }
            }
        };
    }

    public ImporterTask getImporter(final PDFViewer viewer, final URL url) throws IOException {
        String protocol = url.getProtocol();
        if (!"http".equals(protocol) && !"https".equals(protocol)) {
            return super.getImporter(viewer, url);
        }
        String urlstring = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile()).toString();      // Strip password
        return new ImporterTask(viewer, null, urlstring, null) {
            private PDFReader reader = new PDFReader();
            public float getProgress() {
                return reader.getProgress();
            }
            public PDF loadPDF() throws IOException {
                try {
                    return AccessController.doPrivileged(new PrivilegedExceptionAction<PDF>() {
                        public PDF run() throws IOException {
                            reader.setSource(url);
                            for (EncryptionHandler handler : getEncryptionHandlers()) {
                                if (handler != null) {
                                    reader.addEncryptionHandler(handler);
                                }
                            }
                            reader.load();
                            if (!isCancelled()) {
                                return new PDF(reader);
                            } else {
                                return null;
                            }
                        }
                    });
                } catch (PrivilegedActionException e) {
                    throw (IOException)e.getException();
                }
            }
        };
    }
}


