// $Id: ImageImporter.java 21183 2015-03-31 15:00:54Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFImage;
import org.faceless.pdf2.PDFImageSet;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.Importer;
import org.faceless.pdf2.viewer3.PDFViewer;

/**
 * A subclass of {@link Importer} that allows bitmap images to be converted
 * to PDF documents and loaded directly into the {@link PDFViewer}. This
 * class handles all the formats supported by the {@link PDFImage} class,
 * namely TIFF, PNG, GIF, JPEG, PNM and JPEG-2000.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ImageImporter</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.10.2.
 */
public class ImageImporter extends Importer
{
    private final FileFilter filefilter;
    private static final byte[][] PREFIXES = {
        { (byte)0xFF, (byte)0xD8 },
        { (byte)0x89, 'P', 'N', 'G', '\r' },
        { 'I', 'I', '*', 0 },
        { 'M', 'M', 0, '*' },
        { 'G', 'I', 'F', '8' },
        { 'P', '4' },
        { 'P', '5' },
        { 'P', '6' },
        { 'B', 'M' },
        { 0, 0, 0, 0x0C, 'j', 'P', ' ', ' ', '\r' }
    };

    public ImageImporter() {
        super("ImageImporter");
        setAddToMostRecent(false);
        filefilter = new FileFilter() {
            public boolean accept(File file) {
                String fname = file.getName().toLowerCase();
                return file.isDirectory() || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpg") || fname.endsWith(".jp2") || fname.endsWith(".tif") || fname.endsWith(".bmp") || fname.endsWith(".pnm") || fname.endsWith(".ppm") || fname.endsWith(".pbm") || fname.endsWith(".pgm");
            }
            public String getDescription() {
                return UIManager.getString("PDFViewer.FilesBitmap");
            }
        };
    }

    public FileFilter getFileFilter() {
        return filefilter;
    }

    public boolean matches(final File file) throws IOException {
        try {
            return (AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                public Boolean run() throws IOException {
                    if (file.length() > 10) {
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), 10);
                        byte[] buf = new byte[10];
                        in.read(buf);
                        in.close();
                        for (int i=0;i<PREFIXES.length;i++) {
                            if (buf.length > PREFIXES[i].length) {
                                int j = 0;
                                while (j<PREFIXES[i].length) {
                                    if (buf[j] != PREFIXES[i][j]) {
                                        break;
                                    }
                                    j++;
                                }
                                if (j == PREFIXES[i].length) {
                                    return Boolean.TRUE;
                                }
                            }
                        }
                    }
                    return Boolean.FALSE;
                }
            })).booleanValue();
        } catch (PrivilegedActionException e) {
            Exception e2 = e.getException();
            if (e2 instanceof IOException) {
                throw (IOException)e2;
            } else {
                throw (RuntimeException)e2;
            }
        }
    }

    public ImporterTask getImporter(PDFViewer viewer, File file) {
        return getImporter(viewer, null, file.getName(), file);
    }

    public ImporterTask getImporter(final PDFViewer viewer, final InputStream in, final String title, final File file) {
        return new ImporterTask(viewer, in, title, file) {
            private int i, num;
            public float getProgress() {
                return num == 0 ? 0 : (float)i / num;
            }
            public PDF loadPDF() throws IOException {
                String name = file.getName();
                int ix = name.lastIndexOf(".");
                if (ix > 0) {
                    setFile(new File(file.getParentFile(), name.substring(0, ix) + ".pdf"));
                }
                final InputStream fin = this.in;
                try {
                    return AccessController.doPrivileged(new PrivilegedExceptionAction<PDF>() {
                        public PDF run() throws IOException {
                            InputStream in = fin;
                            if (in == null) {
                                in = new FileInputStream(file);
                            }
                            try {
                                PDF pdf = new PDF();
                                PDFImageSet imageset = new PDFImageSet(in);
                                num = imageset.getNumImages();
                                Reader xmp = null;
                                for (i=0;i<num && !isCancelled();i++) {
                                    PDFImage image = imageset.getImage(i);
                                    if (xmp == null) {
                                        xmp = image.getMetaData();
                                    }
                                    PDFPage page = pdf.newPage((int)image.getWidth(), (int)image.getHeight());
                                    page.drawImage(image, 0, 0, page.getWidth(), page.getHeight());
                                }
                                if (isCancelled()) {
                                    return null;
                                } else if (xmp != null) {
                                    StringWriter w = new StringWriter();
                                    int c;
                                    while ((c=xmp.read())>=0) {
                                        w.write((char)c);
                                    }
                                    xmp.close();
                                    if (w.toString().length() == 0) {
                                        xmp = null;
                                    } else {
                                        pdf.setMetaData(w.toString());
                                    }
                                }
                                if (xmp == null) {
                                    pdf.setInfo("Title", "Converted from "+title);
                                }
                                return pdf;
                            } finally {
                                in.close();
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
