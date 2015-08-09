// $Id: ZoomFitWidth.java 20435 2014-12-03 16:21:26Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Creates a button/menu button which zooms the Document to fit the width to the current viewport.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">FitWidth</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class ZoomFitWidth extends ToggleViewerWidget implements DocumentPanelListener
{
    public ZoomFitWidth() {
        super("FitWidth", "Zoom");
        setButton("Navigation", "resources/icons/ZoomFitWidth.png", "PDFViewer.tt.ZoomFitWidth");
        setMenu("+View\tZoom\tZoomFitWidth", '2');
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        if (type=="redrawn" || type=="activated" || type=="viewportChanged") {
            int mode = event.getDocumentPanel().getViewport().getZoomMode();
            setSelected(mode==DocumentViewport.ZOOM_FITWIDTH);
        }
    }

    public void action(ViewerEvent event) {
        DocumentPanel docpanel = event.getDocumentPanel();
        PDFPage page = null;
        if (docpanel!=null && (page=docpanel.getPage())!=null) {
            super.action(event);
            if (isSelected()) {
                docpanel.getViewport().setZoomMode(DocumentViewport.ZOOM_FITWIDTH);
                docpanel.runAction(PDFAction.goToFitWidth(page, Float.NaN));
            } else {
                docpanel.getViewport().setZoomMode(DocumentViewport.ZOOM_NONE);
            }
        }
    }
}
