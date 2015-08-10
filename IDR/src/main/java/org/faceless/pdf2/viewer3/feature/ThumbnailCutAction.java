// $Id: ThumbnailCutAction.java 18673 2013-12-05 13:59:07Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * Store the currently selected pages in the thumbnail panel for a
 * subsequent move operation.
 * <span class="featurename">The name of this feature is <span class="featureactualname">ThumbnailCut</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.15
 */
public class ThumbnailCutAction extends ViewerFeature implements ThumbnailPanel.ThumbnailSelectionAction {

    private PDFViewer viewer;

    public ThumbnailCutAction() {
        super("ThumbnailCut");
    }

    public void initialize(final PDFViewer viewer) {
        this.viewer = viewer;
    }

    public Action getAction(final ThumbnailPanel.View view) {
        if (!view.isFactoryEditable()) {
            return null;
        }
        return this.new CutAction(UIManager.getString("PDFViewer.Cut"), view);
    }

    class CutAction extends AbstractAction implements PropertyChangeListener, DocumentPanelListener {

        private final ThumbnailPanel.View view;
        private final DocumentPanel docpanel;

        CutAction(String name, ThumbnailPanel.View view) {
            super(name);
            this.view = view;
            docpanel = view.getDocumentPanel();
            int mask = docpanel.getToolkit().getMenuShortcutKeyMask();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('X', mask, false));
            setEnabled(isActionEnabled());
            view.addPropertyChangeListener(this);
            docpanel.addDocumentPanelListener(this);
            docpanel.getPDF().addPropertyChangeListener(this);
        }

        private boolean isActionEnabled() {
            int selected = 0;
            int len = view.getComponentCount();
            for (int i = 0; i < len; i++) {
                ThumbnailPanel.SinglePagePanel thumbnail = (ThumbnailPanel.SinglePagePanel) view.getComponent(i);
                if (thumbnail.isSelected()) {
                    selected++;
                }
            }
            return selected > 0 && selected < len && view.isEditable() && view.isDraggable();
        }

        public void propertyChange(PropertyChangeEvent event) {
            String name = event.getPropertyName();
            if (event.getSource() == docpanel.getPDF()) {
                if (name.equals("pages")) {
                    setEnabled(isActionEnabled());
                }
            } else {
                if (name.equals("selection") || name.equals("selected")) {
                    setEnabled(isActionEnabled());
                }
            }
        }

        public void documentUpdated(DocumentPanelEvent event) {
            if ("permissionChanged".equals(event.getType())) {
                setEnabled(isActionEnabled());
            } else if ("closing".equals(event.getType())) {
                view.removePropertyChangeListener(this);
                docpanel.removeDocumentPanelListener(this);
            }
        }

        public void actionPerformed(ActionEvent event) {
            // Set the source view for a paste action
            viewer.putClientProperty("org.faceless.pdf2.viewer3.pasteView", view);
            int len = view.getComponentCount();
            for (int i = 0; i < len; i++) {
                ThumbnailPanel.SinglePagePanel thumbnail = (ThumbnailPanel.SinglePagePanel) view.getComponent(i);
                thumbnail.setFlags(thumbnail.isSelected() ?
                        ThumbnailPanel.SinglePagePanel.FLAG_CUT :
                        ThumbnailPanel.SinglePagePanel.FLAG_NONE);
            }
            view.firePropertyChange("cut");
        }

    }

}
