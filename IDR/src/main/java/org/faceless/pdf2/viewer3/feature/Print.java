// $Id: Print.java 18841 2014-01-13 13:29:36Z chris $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import java.awt.event.*;
import javax.swing.*;
import javax.print.*;

/**
 * Create a button that opens a print dialog.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Print</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class Print extends ViewerWidget implements DocumentPanelListener {

    private final Action action;

    public Print() {
        super("Print");
        setButton("Document", "resources/icons/print.png", "PDFViewer.tt.Print");
        setMenu("File\tPrint...", 'p');
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                action(new ViewerEvent(event, getViewer()));
            }
        };
    }

    public ActionListener createActionListener() {
        return action;
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        DocumentPanel docpanel = event.getDocumentPanel();
        if (type.equals("activated") || (type.equals("permissionChanged") && docpanel == getViewer().getActiveDocumentPanel())) {
            action.setEnabled(docpanel.getPDF() != null && docpanel.hasPermission("Print"));
        } else if (type.equals("deactivated")) {
            action.setEnabled(false);
        }
    }

    public void action(ViewerEvent event) {
        try {
            event.getDocumentPanel().imprimirPaginaAtual();
        } catch (Exception e) {
            Util.displayThrowable(e, event.getViewer());
        }
    }
}
