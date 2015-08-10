// $Id: TIFFExporter.java 20659 2015-01-21 14:24:01Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.Exporter;

/**
 * A subclass of {@link Exporter} that handles saving a PDF as
 * a TIFF file.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">TIFFExporter</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.10.2
 */
public class TIFFExporter extends Exporter
{
    private FileFilter filefilter;

    public TIFFExporter() {
        super("TIFFExporter");
        filefilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".tif") || file.getName().toLowerCase().endsWith(".tiff");
            }
            public String getDescription() {
                return UIManager.getString("PDFViewer.FilesTIFF");
            }
        };
    }

    public FileFilter getFileFilter() {
        return filefilter;
    }

    public String getFileSuffix() {
        return "tif";
    }

    public JComponent getComponent(DocumentPanel panel, File file) {
        return new TIFFComponent();
    }

    public ExporterTask getExporter(final DocumentPanel panel, final PDF pdf, final JComponent c, final OutputStream out) {
        return new ExporterTask() {
            private PDFParser parser;
            public final boolean isCancellable() {
                return true;
            }
            public float getProgress() {
                return parser == null ? 0 : parser.getWriteAsTIFFProgress();
            }
            public void savePDF() throws IOException {
                TIFFComponent tc = (TIFFComponent)c;
                preProcessPDF(pdf);
                parser = new PDFParser(pdf);
                postProcessPDF(pdf);
                parser.writeAsTIFF(out, tc.getDPI(), tc.getSelectedColorModel());
                out.close();
            }
        };
    }


    private static class TIFFComponent extends JPanel {
        private JComboBox<String> dpi;
        private JComboBox colormodel;

        TIFFComponent() {
            super(new FlowLayout());
            dpi = new JComboBox<String>(new String[] { "72", "100", "150", "200", "300", "400", "600" });
            colormodel = new JComboBox<String>(new String[] { UIManager.getString("PDFViewer.export.RGB"), UIManager.getString("PDFViewer.export.Gray"), UIManager.getString("PDFViewer.export.BW") });

            add(new JLabel(UIManager.getString("PDFViewer.export.DPI")));
            add(dpi);
            add(new Box.Filler(new Dimension(20, 0), null, null));
            add(new JLabel(UIManager.getString("PDFViewer.export.ColorModel")));
            add(colormodel);
        }

        int getDPI() {
            return Integer.parseInt(dpi.getSelectedItem().toString());
        }

        ColorModel getSelectedColorModel() {
            Object val = colormodel.getSelectedItem();
            if (val.equals(UIManager.getString("PDFViewer.export.RGB"))) {
                return PDFParser.RGB;
            } else if (val.equals(UIManager.getString("PDFViewer.export.Gray"))) {
                return PDFParser.GRAYSCALE;
            } else {
                return PDFParser.BLACKANDWHITE;
            }
        }
    }
}
