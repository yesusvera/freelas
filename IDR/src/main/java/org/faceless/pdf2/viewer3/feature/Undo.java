// $Id: Undo.java 19623 2014-07-11 15:17:50Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * This features adds an "Undo" and "Redo" entry to the Edit menu, which interfaces
 * with the {@link DocumentPanel#fireUndoableEditEvent} method to provide undo/redo
 * across the Document.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Undo</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.19
 */
public final class Undo extends ViewerFeature implements ActionListener, UndoableEditListener, DocumentPanelListener {

    /**
     * An UndableEdit which can be passed into {@link DocumentPanel#fireUndoableEditEvent}
     * to clear the list. This should be done when the list needs to be cleared, due to an
     * action on the Document that permanently changes the state of the PDF.
     */
    public static final UndoableEdit DISCARD = new AbstractUndoableEdit() { };

    private PDFViewer viewer;
    private Map<DocumentPanel,UndoManager> undomanagers;
    private JMenuItem undomenu, redomenu;

    public Undo() {
        super("Undo");
        undomanagers = new WeakHashMap<DocumentPanel,UndoManager>();
    }

    public void actionPerformed(ActionEvent e) {
        UndoManager manager = undomanagers.get(viewer.getActiveDocumentPanel());
        if (e.getSource() == undomenu && manager.canUndo()) {
            manager.undo();
            update();
        } else if (e.getSource() == redomenu && manager.canRedo()) {
            manager.redo();
            update();
        }
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        this.viewer = viewer;
        undomenu = viewer.setMenu("Edit\tUndo", 'z', true, this);
        redomenu = viewer.setMenu("Edit\tRedo", 'y', true, this);
        viewer.getMenu("Edit").add(new JPopupMenu.Separator());
        viewer.addDocumentPanelListener(this);
        update();
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        if (type == "closing") {
            event.getDocumentPanel().removeUndoableEditListener(this);
            undomanagers.remove(event.getDocumentPanel());
            update();
        } else if (type == "loaded") {
            event.getDocumentPanel().addUndoableEditListener(this);
            UndoManager undomanager;
            undomanagers.put(event.getDocumentPanel(), undomanager = new UndoManager());
            undomanager.setLimit(10);
            update();
        } else if (type == "deactivated" || type == "activated") {
            update();
        }
    }

    public void undoableEditHappened(UndoableEditEvent event) {
        DocumentPanel panel = (DocumentPanel)event.getSource();
        UndoManager manager = (UndoManager)undomanagers.get(panel);
        if (event.getEdit() == DISCARD) {
            manager.discardAllEdits();
        } else {
            manager.undoableEditHappened(event);
        }
        update();
    }

    private void update() {
        DocumentPanel panel = viewer.getActiveDocumentPanel();
        UndoManager manager = panel == null ? null : (UndoManager)undomanagers.get(panel);
        if (manager == null || !manager.canUndo()) {
            undomenu.setText(UIManager.getString("AbstractUndoableEdit.undoText"));
            undomenu.setEnabled(false);
        } else {
            undomenu.setText(manager.getUndoPresentationName());
            undomenu.setEnabled(true);
        }

        if (manager == null || !manager.canRedo()) {
            redomenu.setText(UIManager.getString("AbstractUndoableEdit.redoText"));
            redomenu.setEnabled(false);
        } else {
            redomenu.setText(manager.getRedoPresentationName());
            redomenu.setEnabled(true);
        }
    }

}
