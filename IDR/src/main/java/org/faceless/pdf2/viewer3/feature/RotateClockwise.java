// $Id: RotateClockwise.java 19846 2014-07-30 11:19:13Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;

import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerWidget;

/**
 * Create a button that will rotate the page 90 degrees clockwise.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">RotateClockwise</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8.3
 */
public class RotateClockwise extends ViewerWidget implements ThumbnailPanel.ThumbnailSelectionAction {

    private final RotateAction action;
    private final int diff;

    public RotateClockwise() {
        this("RotateClockwise", 90);
    }

    public RotateClockwise(String name, int diff) {
        super(name);
        this.diff = diff;
        setButton("Edit", "resources/icons/"+name+".png", "PDFViewer.tt."+name);
        setMenu("Edit\t"+name);
        this.action = new RotateAction(false) {
            List<PDFPage> getPages() {
                return Collections.singletonList(getViewer().getActiveDocumentPanel().getPage());
            }
        };
    }

    protected ActionListener createActionListener() {
        return action;
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(action);
    }

    public boolean isButtonEnabledByDefault() {
        return false;
    }

    public Action getAction(final ThumbnailPanel.View view) {
        if (!view.isFactoryEditable()) {
            return null;
        }

        RotateAction action = new RotateAction(true) {
            List<PDFPage> getPages() {
                List<PDFPage> l = new ArrayList<PDFPage>();
                for (int i=0;i<view.getComponentCount();i++) {
                    ThumbnailPanel.SinglePagePanel panel = (ThumbnailPanel.SinglePagePanel)view.getComponent(i);
                    if (panel.isSelected()) {
                        l.add(panel.getPage());
                    }
                }
                return l;
            }
        };
        view.getDocumentPanel().addDocumentPanelListener(action);
        return action;
    }

    private abstract class RotateAction extends AbstractAction implements DocumentPanelListener {
        protected DocumentPanel docpanel;
        private boolean documentpanelspecific;

        RotateAction(boolean documentpanelspecific) {
            if (diff == 90) {
                putValue(Action.NAME, UIManager.getString("PDFViewer.RotateClockwise"));
            } else if (diff == -90) {
                putValue(Action.NAME, UIManager.getString("PDFViewer.RotateAntiClockwise"));
            } else {
                throw new IllegalArgumentException();
            }
            this.documentpanelspecific = documentpanelspecific;
        }

        abstract List<PDFPage> getPages();

        public void actionPerformed(ActionEvent event) {
            final List<PDFPage> pages = new ArrayList<PDFPage>(getPages());
            for (Iterator<PDFPage> i = pages.iterator();i.hasNext();) {
                PDFPage page = i.next();
                page.setPageOrientation(page.getPageOrientation() + diff);
            }

            docpanel.fireUndoableEditEvent(new UndoableEditEvent(docpanel, new AbstractUndoableEdit() {
                public String getPresentationName() {
                    return (String)getValue(Action.NAME);
                }
                public void undo() {
                    super.undo();
                    undoredo(-diff);
                }
                public void redo() {
                    super.redo();
                    undoredo(diff);
                }
                private void undoredo(int diff) {
                    for (Iterator<PDFPage> i = pages.iterator();i.hasNext();) {
                        PDFPage page = i.next();
                        page.setPageOrientation(page.getPageOrientation() + diff);
                    }
                }
            }));
        }

        public void documentUpdated(DocumentPanelEvent event) {
            String type = event.getType();
            DocumentPanel eventdocpanel = event.getDocumentPanel();
            if (type.equals("activated") || (type.equals("permissionChanged") && eventdocpanel == getViewer().getActiveDocumentPanel())) {
                docpanel = eventdocpanel;
                setEnabled(docpanel.getPDF() != null && docpanel.hasPermission("Assemble"));
            } else if (type.equals("deactivated")) {
                docpanel = null;
                setEnabled(false);
            } else if (type.equals("closing") && documentpanelspecific) {
                event.getDocumentPanel().removeDocumentPanelListener(this);
                docpanel = null;
                setEnabled(false);
            }
        }
    }

}
