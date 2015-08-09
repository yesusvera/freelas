// $Id: Quit.java 21107 2015-03-13 18:05:22Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import org.faceless.pdf2.viewer3.util.AppletMenuItem;

/**
 * Create a menu item that will quit the application - ie. it calls <code>System.exit(0)</code>.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Quit</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class Quit extends ViewerWidget implements ActionListener {

    public Quit() {
        super("Quit");
        setDocumentRequired(false);
    }

    public void initialize(PDFViewer viewer) {
        // On Windows this menu item is called "Exit" and has Alt-F4 as a shortcut. Everywhere
        // else it's called "Quit"
        if (Util.isLAFWindows()) {
            setMenu("File\tExit(999)");
        } else {
            setMenu("File\tQuit(999)", 'q');
        }
        super.initialize(viewer);
        AppletMenuItem menu = (AppletMenuItem)viewer.getNamedComponent("MenuQuit");
        if (menu != null) {
            menu.setAppletEnabled(false);
            if (Util.isLAFWindows()) {
                menu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
            }
        }
    }

    protected ActionListener createActionListener() {
        return this;
    }

    /**
     * Run the Quit action.
     */
    public void actionPerformed(ActionEvent evt) {
        super.createActionListener().actionPerformed(evt);
    }

    public void action(ViewerEvent event) {
        // Amazingly, System.exit works in an applet too! In Firefox or Safari it exits
        // the entire browser, and in IE it closes the applet but leaves it's appearance
        // in the browser window.
        //
        // This is almost certainly not what the user wants to happen, so display a prompt
        // if we're in an applet running inside a browser. Since 2.11.1 we try to prevent
        // this case from happening at all by disabling Quit entirely in Applets, but it
        // won't hurt to leave this code in
        //
        if (!Util.isBrowserApplet(event.getViewer()) || JOptionPane.showConfirmDialog(event.getViewer(), UIManager.getString("PDFViewer.ConfirmQuitText"), UIManager.getString("PDFViewer.Confirm"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            DocumentPanel[] p = event.getViewer().getDocumentPanels();
            for (int i=0;i<p.length;i++) {
                event.getViewer().closeDocumentPanel(p[i]);
            }
            if (event.getViewer().getDocumentPanels().length == 0) {
                event.getViewer().close();
                System.exit(0);
            }
        }
    }

}
