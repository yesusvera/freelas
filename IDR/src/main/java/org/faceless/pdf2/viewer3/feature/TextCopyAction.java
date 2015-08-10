// $Id: TextCopyAction.java 17586 2013-05-10 09:48:53Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import javax.swing.UIManager;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerFeature;
import org.faceless.pdf2.viewer3.util.RichTextTransferHandler;

/**
 * A {@link TextSelectionAction} that will copy the selected text to the
 * System clipboard.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">TextCopyAction</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 *
 * @since 2.11.19
 */
public class TextCopyAction extends ViewerFeature implements TextSelectionAction, DocumentPanelListener {

    private boolean enabled;

    public TextCopyAction() {
        super("TextCopyAction");
    }

    public String getDescription() {
        return UIManager.getString("PDFViewer.Copy");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        DocumentPanel docpanel = event.getDocumentPanel();
        if (type.equals("activated") || (type.equals("permissionChanged") && docpanel == docpanel.getViewer().getActiveDocumentPanel())) {
            enabled = docpanel.getPDF() != null && docpanel.hasPermission("Extract");
        } else if (type.equals("deactivated")) {
            enabled = false;
        }
    }

    public void selectAction(DocumentPanel docpanel, TextSelection.RangeList range) {
        Clipboard clipboard = docpanel.getToolkit().getSystemClipboard();
        clipboard.setContents(new RichTextTransferHandler.RichTransferable(range.getStyledText()), new ClipboardOwner() {
            public void lostOwnership(Clipboard clipboard, Transferable contents) {
            }
        });
    }

}
