// $Id: ThumbnailDeleteAction.java 20438 2014-12-03 19:08:51Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * This feature will allow pages to be deleted via the {@link ThumbnailPanel}.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ThumbnailDelete</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.12
 */
public class ThumbnailDeleteAction extends ViewerFeature implements ThumbnailPanel.ThumbnailSelectionAction {

    public ThumbnailDeleteAction() {
        super("ThumbnailDelete");
    }


    private class DeleteAction extends AbstractAction implements PropertyChangeListener, DocumentPanelListener {
        private final ThumbnailPanel.View view;
        private final DocumentPanel docpanel;

        DeleteAction(String name, ThumbnailPanel.View view) {
            super(name);
            this.view = view;
            this.docpanel = view.getDocumentPanel();
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
            // Note: magic in ThumbnailPanel.View will also use BACKSPACE for this
            // event if it's not otherwise defined. Macs don't have Delete key.
            setEnabled(isActionEnabled());
            view.addPropertyChangeListener(this);
            docpanel.addDocumentPanelListener(this);
            docpanel.getPDF().addPropertyChangeListener(this);
        }

        public void actionPerformed(ActionEvent event) {
            final PDF pdf = docpanel.getPDF();

            if (JOptionPane.showConfirmDialog(docpanel, Util.getUIString("PDFViewer.ConfirmDeletePages", view.getSelectedPagesDescription()), UIManager.getString("PDFViewer.Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                List<PDFPage> l = new ArrayList<PDFPage>();
                for (int i=0;i<view.getComponentCount();i++) {
                    ThumbnailPanel.SinglePagePanel panel = (ThumbnailPanel.SinglePagePanel)view.getComponent(i);
                    if (panel.isSelected()) {
                        l.add(panel.getPage());
                        panel.setSelected(false);
                    }
                }
                int pagenumber = docpanel.getPageNumber();
                final List<PDFPage> oldpages = new ArrayList<PDFPage>(pdf.getPages());
                pdf.getPages().removeAll(l);
                final List<PDFPage> newpages = new ArrayList<PDFPage>(pdf.getPages());
                final PDFPage beforevisiblepage;
                if (l.contains(docpanel.getPage())) {
                    pagenumber = Math.min(pdf.getPages().size() - 1, pagenumber);
                    docpanel.setPage(beforevisiblepage = pdf.getPages().get(pagenumber));
                } else {
                    beforevisiblepage = docpanel.getViewport().getRenderingPage();
                }
                docpanel.fireUndoableEditEvent(new UndoableEditEvent(docpanel, new AbstractUndoableEdit() {
                    public String getPresentationName() {
                        return UIManager.getString("PDFViewer.Pages");
                    }
                    public boolean canUndo() {
                        return docpanel != null;
                    }
                    public boolean canRedo() {
                        return docpanel != null;
                    }
                    public void undo() {
                        super.undo();
                        // Makes list longer
                        List<PDFPage> pages = pdf.getPages();
                        for (int i=0;i<oldpages.size();i++) {
                            PDFPage page = oldpages.get(i);
                            if (i == pages.size() || pages.get(i) != page) {
                                pages.add(i, page);
                            }
                        }
                    }
                    public void redo() {
                        super.redo();
                        // Makes list shorter
                        List<PDFPage> pages = pdf.getPages();
                        for (int i=0;i<pages.size();i++) {
                            PDFPage page = pages.get(i);
                            if (i >= newpages.size() || newpages.get(i) != page) {
                                pages.remove(i--);
                            }
                        }
                        // We may have deleted invisible page
                        if (beforevisiblepage != null) {
                            docpanel.setPage(beforevisiblepage);
                        }
                    }
                }));
            }
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

        private boolean isActionEnabled() {
            DocumentPanel docpanel = view.getDocumentPanel();
            if (docpanel == null) {
                return false;
            }
            PDF pdf = docpanel.getPDF();
            if (pdf == null) {
                return false;
            }
            // We're only available if deletion won't leave an empty document.
            int select = 0;
            int total = pdf.getNumberOfPages();
            for (int i=0;i<view.getComponentCount();i++) {
                ThumbnailPanel.SinglePagePanel panel = (ThumbnailPanel.SinglePagePanel)view.getComponent(i);
                if (panel.isSelected()) {
                    select++;
                }
            }
            return select > 0 && select < total && view.isEditable() && view.isDraggable();
        }
    }

    public Action getAction(final ThumbnailPanel.View view) {
        if (!view.isFactoryEditable()) {
            return null;
        }

        return new DeleteAction(UIManager.getString("PDFViewer.Delete")+"...", view);
    }

}
