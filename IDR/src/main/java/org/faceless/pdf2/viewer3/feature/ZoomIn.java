// $Id: ZoomIn.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerEvent;
import org.faceless.pdf2.viewer3.ViewerWidget;

/**
 * Creates a button whcih will zoom the document in to the next level.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ZoomIn</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class ZoomIn extends ViewerWidget
{
    public ZoomIn() {
        super("ZoomIn");
        setButton("Navigation", "resources/icons/ZoomIn.png", "PDFViewer.tt.ZoomIn");
        setMenu("View\tZoom\tZoomIn");
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        JButton b = (JButton)getComponent();
        Insets i = b.getInsets();
        b.setMargin(new Insets(0, 0, 0, i.right));
    }

    public void action(ViewerEvent event) {
        PDFViewer viewer = event.getViewer();
        JComboBox zoomlevel = (JComboBox)viewer.getNamedComponent("ZoomLevel");
        float currentzoom = event.getDocumentPanel().getZoom();
        for (int i=0;i<zoomlevel.getItemCount();i++) {
            String s = (String)zoomlevel.getItemAt(i);
            float thiszoom = Float.parseFloat(s.substring(0, s.length()-1))/100;
            if (thiszoom>currentzoom) {
                zoomlevel.setSelectedItem(s);
                break;
            }
        }
    }
}
