// $Id: PageNumber.java 20861 2015-02-11 10:58:38Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.PDFViewer;

/**
 * Create a widget that displays the current page number, and allows the user
 * to enter a new pagenumber for display.
 *
 * <div class="initparams">
 * The following <a href="../doc-files/initparams.html">initialization parameters</a> can be specified to configure this feature.
 * <table summary="">
 * <tr><th>usePageLabels</th><td>If true (the default), display the {@link PDF#getPageLabel page label} for
 * the selected page if specified. If false, display the physical page number (the default behaviour
 * prior to 2.11.19).</td></tr>
 * <tr><th>alignment</th><td>Can be set to "right", "center" or "left" (the default) to position the page
 * number in the box</td></tr>
 * </table>
 * </div>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PageNumber</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class PageNumber extends NavigationWidget {

    private JTextField field;
    private String[] pagelabels;
    private boolean uselabels = true;

    public PageNumber() {
        super("PageNumber");
        setDocumentRequired(false); // manages its own enabled state

        field = new JTextField();
        field.setColumns(3);
        field.setFont(null);
        field.setEditable(true);
        field.setEnabled(false);
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = field.getText().trim();
                DocumentPanel docpanel = getViewer().getActiveDocumentPanel();
                if (pagelabels != null) {
                    int pn = -1;
                    for (int i=0;pn < 0 && i < pagelabels.length;i++) {
                        if (pagelabels[i].equals(text)) {
                            pn = i;
                        }
                    }
                    if (pn < 0) {               // Fallback to physical page number
                        try {
                            pn = Integer.parseInt(text) - 1;
                            if (pn < 0 || pn >= pagelabels.length) {
                                pn = -1;
                            }
                        } catch (NumberFormatException e2) { }
                    }
                    if (docpanel != null) {
                        if (pn >= 0) {
                            docpanel.setPageNumber(pn);
                        } else {
                            pn = docpanel.getPageNumber();
                            if (pn >= 0 && pn < pagelabels.length) {
                                field.setText(pagelabels[pn]);
                            }
                        }
                    }
                }
                if (field.hasFocus() && docpanel != null) {
                    docpanel.getViewport().requestFocusInWindow();
                }
            }
        });
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.selectAll();
            }
        });
        setComponent("Navigation.ltr", field);
        field.setEnabled(false);
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
        String val = getFeatureProperty(viewer, "usePageLabels");
        if (val != null) {
            setUsePageLabels("true".equalsIgnoreCase(val));
        }
        String alignment = getFeatureProperty(viewer, "alignment");
        if ("right".equals(alignment)) {
            field.setHorizontalAlignment(JTextField.RIGHT);
        } else if ("center".equals(alignment)) {
            field.setHorizontalAlignment(JTextField.CENTER);
        } else {
            field.setHorizontalAlignment(JTextField.LEFT);
        }
    }

    /**
     * Set whether to display the "page labels" if defined on this PDF, or
     * whether to always display the physical page number.
     * @param uselabels if true, use the page labels if defined, otherwise use the physical page number
     * @see PDF#getPageLabel
     * @see ThumbnailPanel#setUsePageLabels
     * @since 2.11.19
     */
    public void setUsePageLabels(boolean uselabels) {
        this.uselabels = uselabels;
        if (pdf != null) {
            updatePageLabels();
        }
    }

    public void documentUpdated(DocumentPanelEvent event) {
        super.documentUpdated(event);
        String type = event.getType();
        if (type == "activated") {
            updatePageLabels();
            field.setEnabled(true);
        } else if (type == "deactivated") {
            pagelabels = null;
            field.setText("");
            field.setEnabled(false);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (event.getSource() == pdf) {
            String name = event.getPropertyName();
            if (name.equals("pagelabels") || name.equals("pages")) {
                updatePageLabels();
            }
        }
    }

    protected void pageChanged() {
        if (pdf == null) {
            field.setText("");
        } else {
            int pagenumber = docpanel.getPageNumber();
            if (pagenumber >= 0 && pagelabels != null && pagenumber < pagelabels.length) {
                String label = pagelabels[pagenumber];
                if (!label.equals(field.getText())) {
                    field.setText(label);
                }
            }
        }
    }

    private void updatePageLabels() {
        List<String> l = new ArrayList<String>();
        int numpages = pdf.getNumberOfPages();
        for (int i=0;i<numpages;i++) {
            String label = uselabels ? pdf.getPageLabel(i) : null;
            if (label == null) {
                label = Integer.toString(i + 1);
            }
            l.add(label);
        }
        pagelabels = l.toArray(new String[numpages]);
        pageChanged();
    }

}
