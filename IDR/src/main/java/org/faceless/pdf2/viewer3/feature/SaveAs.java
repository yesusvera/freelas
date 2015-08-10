// $Id: SaveAs.java 21183 2015-03-31 15:00:54Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.AbstractAction;

import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.Exporter;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.ViewerEvent;
import org.faceless.pdf2.viewer3.ViewerWidget;

/**
 * Create a button that will open a dialog allowing the PDF to be saved to disk.
 * Any {@link Exporter} formats included as features in the Viewer will be presented
 * as options.
 * <div class="initparams">
 * The following <a href="../doc-files/initparams.html">initialization parameters</a> can be specified to configure this feature.
 * <table summary="">
 * <tr><th>promptOnOverwrite</th><td>true to prompt before overwriting files, false otherwise (the default)</td></tr>
 * <tr><th>disableUnlessDirty</th><td>true to disable this feature until the PDF has been marked as "dirty" (ie it has been altered), false to always enable this feature (the defualt)</td></tr>
 * </table>
 * </div>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Save</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.17
 */
public class SaveAs extends ViewerWidget implements DocumentPanelListener {

    private AbstractAction action;
    private boolean overwriteprompt = false;

    public SaveAs() {
        super("SaveAs");
        setMenu("File\tSaveAs...");
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                ViewerEvent ve = new ViewerEvent(event, getViewer());
                action(ve);
            }
        };
    }

    public void action(ViewerEvent event) {
        saveAs(event, null, null, true, overwriteprompt);
    }

    public ActionListener createActionListener() {
        return action;
    }

    public boolean isEnabledByDefault() {
        return Util.hasFilePermission();
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);

        String val = getFeatureProperty(viewer, "promptOnOverwrite");
        if (val != null) {
            setPromptOnOverwrite("true".equals(val));
        }

        viewer.addDocumentPanelListener(this);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        DocumentPanel docpanel = event.getDocumentPanel();
        if (type.equals("activated") && docpanel != null) {
            action.setEnabled(docpanel.hasPermission("Save"));
        } else if (type.equals("permissionChanged") && docpanel != null && docpanel == getViewer().getActiveDocumentPanel()) {
            action.setEnabled(docpanel.hasPermission("Save"));
        } else if (type.equals("deactivated")) {
            action.setEnabled(false);
        }
    }

    /**
     * Set whether this feature should prompt before overwriting a file
     * @param prompt whether to prompt before overwriting a file (detault is false)
     * @since 2.11.25
     */
    public void setPromptOnOverwrite(boolean prompt) {
        this.overwriteprompt = prompt;
    }

    /**

    /**
     * Save the Document
     * @param event the ViewerEvent that launched this action
     * @param initialexporter the {@link Exporter} to choose by default, or null to default
     * to a {@link PDFExporter}
     * @param initialpath the Path to display by default, or <code>null</code> to use the same
     * path as the source file
     * @param displayprompt whether to prompt the user for a filename. Currently ignored.
     * @param overwriteprompt whether to prompt the user if we are about to overwrite a filename.
     * @since 2.11.10
     */
    public static void saveAs(final ViewerEvent event, final Exporter initialexporter, final String initialpath, final boolean displayprompt, final boolean overwriteprompt) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                File file = null;
                if (initialpath != null) {
                    file = new File(initialpath);
                } else {
                    file = (File) event.getDocumentPanel().getClientProperty("file");
                }
                Save.doSave(file, initialexporter, event, true, displayprompt, overwriteprompt);
                return null;
            }
        });
    }

}
