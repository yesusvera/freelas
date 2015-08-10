// $Id: PDFExporter.java 19623 2014-07-11 15:17:50Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.Exporter;
import org.faceless.pdf2.viewer3.PDFBackgroundTask;

/**
 * A subclass of Exporter that handles exporting a PDF as a PDF file.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PDFExporter</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.10.2
 */
public class PDFExporter extends Exporter
{
    private FileFilter filefilter;

    /**
     * Create a new PDFExporter
     */
    public PDFExporter() {
        super("PDFExporter");
        filefilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".pdf");
            }
            public String getDescription() {
                return UIManager.getString("PDFViewer.FilesPDF");
            }
        };
    }

    public FileFilter getFileFilter() {
        return filefilter;
    }

    public String getFileSuffix() {
        return "pdf";
    }

    public boolean isEnabled(DocumentPanel docpanel) {
        return docpanel.hasPermission("Save");
    }

    public ExporterTask getExporter(final DocumentPanel docpanel, final PDF pdf, final JComponent c, final OutputStream out) {
        return new ExporterTask() {
            public final boolean isCancellable() {
                return false;
            }
            public float getProgress() {
                return pdf.getRenderProgress();
            }
            public void savePDF() throws IOException {
                Collection<Object> c = null;
                if (docpanel != null) {
                    c = new HashSet<Object>(docpanel.getSidePanels());
                    if (docpanel.getViewer()!=null) {
                        c.addAll(Arrays.asList(docpanel.getViewer().getFeatures()));
                    }
                    for (Iterator<Object> i = c.iterator();i.hasNext();) {
                        Object o = i.next();
                        if (o instanceof PDFBackgroundTask) {
                            try {
                                ((PDFBackgroundTask)o).pause();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            i.remove();
                        }
                    }
                    docpanel.getJSManager().runEventDocWillSave(docpanel);
                }
                preProcessPDF(pdf);
                pdf.render(out);
                postProcessPDF(pdf);
                out.close();
                if (docpanel != null) {
                    docpanel.setDirty(false);
                    docpanel.getJSManager().runEventDocDidSave(docpanel);
                    for (Iterator i = c.iterator();i.hasNext();) {
                        ((PDFBackgroundTask)i.next()).unpause();
                    }
                }
            }
        };
    }
}
