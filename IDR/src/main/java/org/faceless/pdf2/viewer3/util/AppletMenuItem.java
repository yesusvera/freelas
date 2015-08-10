package org.faceless.pdf2.viewer3.util;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.faceless.pdf2.viewer3.Util;

/**
 * A subclass of JMenuItem that disables menus and/or accelerator keys when
 * the menu is applied in an Applet
 */
public class AppletMenuItem extends JMenuItem {

    private KeyStroke accelerator;
    private boolean appletenabled;

    public AppletMenuItem(Action action) {
        super(action);
        setAppletEnabled(true);
    }

    public AppletMenuItem(String text) {
        super(text);
        setAppletEnabled(true);
    }

    /**
     * Whether to display this menu in the browser when it's running as an applet
     * or not
     */
    public void setAppletEnabled(boolean enabled) {
        this.appletenabled = enabled;
    }

    public boolean isVisible() {
        return super.isVisible() && (appletenabled || !Util.isBrowserApplet(this));
    }

    /**
     * Set whether this menu is in a browser-based applet or not
     */
    public void setBrowserApplet(boolean browserapplet)  {
        if (browserapplet) {
            Action action = getAction();
            if (action != null) {
                accelerator = (KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
                action.putValue(Action.ACCELERATOR_KEY, null);
            } else {
                accelerator = getAccelerator();
                setAccelerator(null);
            }
        } else if (accelerator != null) {
            Action action = getAction();
            if (action != null) {
                action.putValue(Action.ACCELERATOR_KEY, accelerator);
            } else {
                setAccelerator(accelerator);
            }
        }
    }

}
