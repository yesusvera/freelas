// $Id: ZoomOut.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Insets;

/**
 * Create a button which zooms the document out to the next level.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ZoomOut</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class ZoomOut extends ViewerWidget
{
    public ZoomOut() {
        super("ZoomOut");
        setButton("Navigation", "resources/icons/ZoomOut.png", "PDFViewer.tt.ZoomOut");
        setMenu("View\tZoom\tZoomOut");
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        JButton b = (JButton)getComponent();
        Insets i = b.getInsets();
        b.setMargin(new Insets(0, i.left, 0, 0));
    }

    public void action(ViewerEvent event) {
        PDFViewer viewer = event.getViewer();
        JComboBox zoomlevel = (JComboBox)viewer.getNamedComponent("ZoomLevel");
        float currentzoom = event.getDocumentPanel().getZoom();
        for (int i=zoomlevel.getItemCount()-1;i>=0;i--) {
            String s = (String)zoomlevel.getItemAt(i);
            float thiszoom = Float.parseFloat(s.substring(0, s.length()-1))/100;
            if (thiszoom<currentzoom) {
                zoomlevel.setSelectedItem(s);
                break;
            }
        }
    }
}
