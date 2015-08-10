// $Id: DualPageView.java 17253 2013-04-02 15:26:36Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.DocumentViewport;
import org.faceless.pdf2.viewer3.DualPageDocumentViewport;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ToggleViewerWidget;
import org.faceless.pdf2.viewer3.ViewerEvent;

/**
 * This Widget changes the {@link DocumentViewport} of the current {@link DocumentPanel}
 * to a {@link DualPageDocumentViewport}.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">TwoPages</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.13.1
 */
public class DualPageView extends ToggleViewerWidget implements DocumentPanelListener
{
    /**
     * Create a new DualPageView object
     */
    public DualPageView() {
        super("TwoPages", "ViewMode");
        setButton("ViewMode", "resources/icons/dualpage.png", "PDFViewer.tt.ViewDualPage");
    }

    protected void updateViewport(final DocumentViewport vp, boolean selected) {
        if (selected) {
            DocumentPanel panel = vp.getDocumentPanel();
            if (!(panel.getViewport() instanceof DualPageDocumentViewport)) {
                panel.setViewport(new DualPageDocumentViewport());
            }
        }
    }

    public void action(ViewerEvent event) {
        if (!isSelected()) {
            setSelected(!isSelected());
        }
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        if (event.getType()=="activated" || event.getType()=="viewportChanged") {
            setSelected(event.getDocumentPanel().getViewport() instanceof DualPageDocumentViewport);
        }
    }
}
