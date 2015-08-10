// $Id: ZoomLevel.java 20435 2014-12-03 16:21:26Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.DocumentViewport;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ToggleViewerWidget;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.ViewerWidget;

/**
 * Create a widget which displays the current zoom level, and allows the user to
 *
 * edit it to set the zoom level.
 * <span class="featurename">The name of this feature is <span class="featureactualname">ZoomLevel</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class ZoomLevel extends ViewerWidget implements DocumentPanelListener {

    private JComboBox<String> field;
    private boolean fromupdate;

    public ZoomLevel() {
        super("ZoomLevel");

        field = new JComboBox<String>(new String[] { "15%", "25%", "50%", "75%", "100%", "125%", "150%", "200%", "400%", "800%", "1600%", "2400%", "3200%", "6400%"});
        field.setSelectedItem("100%");
        field.putClientProperty("JComponent.sizeVariant", "small");         // Improve OS X/Nimbus appearance
        field.setEditable(true);
        field.setFont(null);
        field.setEnabled(false);
        field.setPrototypeDisplayValue("6400%");    // Why does this give such bad results?
        if (field.getEditor().getEditorComponent() instanceof JTextField) {
            int cols = Util.isLAFAqua() ? 3 : 4;
            ((JTextField)field.getEditor().getEditorComponent()).setColumns(cols);
        }
        field.setToolTipText(UIManager.getString("PDFViewer.tt.ZoomLevel"));
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DocumentPanel docpanel = getViewer().getActiveDocumentPanel();
                if (docpanel != null && field.getSelectedItem() != null) {
                    float oldzoom = docpanel.getZoom();
                    String s = (String)field.getSelectedItem();
                    try {
                        if (s.endsWith("%")) {
                            s = s.substring(0, s.length() - 1);
                        }
                        float newzoom = Float.parseFloat(s)/100f;
                        if (Math.abs(newzoom-oldzoom) > 0.01) {
                            docpanel.getViewport().setZoomMode(DocumentViewport.ZOOM_NONE);
                            docpanel.setZoom(newzoom);
                            if (!fromupdate) {  // This is a result of a click! Turn off ZoomFitN
                                ToggleViewerWidget w = getViewer().getFeature(ZoomFit.class);
                                if (w != null && w.isSelected()) {
                                    w.setSelected(false);
                                }
                                w = getViewer().getFeature(ZoomFitWidth.class);
                                if (w != null && w.isSelected()) {
                                    w.setSelected(false);
                                }
                                w = getViewer().getFeature(ZoomFitHeight.class);
                                if (w != null && w.isSelected()) {
                                    w.setSelected(false);
                                }

                                docpanel.getViewport().setZoomMode(DocumentViewport.ZOOM_NONE);
                            }
                            fromupdate = false;
                        }
                    } catch (Exception e) {
                        field.setSelectedItem(Integer.toString((int)Math.round(oldzoom*100))+"%");
                    }

                    Component focusowner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (focusowner != null && (focusowner == field || field.isAncestorOf(focusowner))) {
                        docpanel.getViewport().requestFocusInWindow();
                    }
                }
            }
        });
        field.setRenderer(getWidthAddingCellRenderer(field.getRenderer()));
        setComponent("Navigation", field);
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        if (type=="redrawn" || type=="activated") {
            fromupdate = true;          // We're called from a DocumentPanel being updated....
            field.setSelectedItem(Integer.toString((int)Math.round(event.getDocumentPanel().getZoom()*100))+"%");
        }
        if (type == "activated") {
            field.setEnabled(true);
        } else if (type == "deactivated") {
            field.setEnabled(false);
        }
    }

    // Below is to fix issue whereby LAF cell renderer doesn't respect
    // JComponent.sizeVariant

    static <E> ListCellRenderer<E> getWidthAddingCellRenderer(final ListCellRenderer<E> proxy) {
        return new ListCellRenderer<E>() {
            public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = proxy.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                // Does not respect borders so have to do it this way
                return new WidthAddingComponent(comp);
            }
        };
    }

    static class WidthAddingComponent extends JComponent {

        Component comp;

        WidthAddingComponent(Component comp) {
            this.comp = comp;
            add(comp);
        }

        public Dimension getPreferredSize() {
            Dimension ps = comp.getPreferredSize();
            return new Dimension(ps.width + 5, ps.height);
        }

        public void doLayout() {
            Dimension ps = comp.getPreferredSize();
            comp.setBounds(0, 0, ps.width, ps.height);
        }

    }

}
