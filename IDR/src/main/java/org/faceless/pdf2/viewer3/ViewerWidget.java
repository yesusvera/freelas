// $Id: ViewerWidget.java 20522 2014-12-16 14:12:39Z mike $

package org.faceless.pdf2.viewer3;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.faceless.pdf2.PropertyManager;
import org.faceless.pdf2.viewer3.feature.Quit;
import org.faceless.pdf2.viewer3.feature.ToolbarDisabling;

/**
 * <p>
 * A type of ViewerFeature that adds a "widget" to a {@link PDFViewer}. Widgets are typically
 * buttons on the toolbar, menu items and so on.
 * </p>
 * <h3>Internals (the old way)</h3>
 * <p>
 * Prior to 2.13.1, Widgets would need to specify details for a button (by calling
 * {@link #setButton setButton()} and/or a menu item (by calling {@link #setMenu setMenu()}).
 * This would create a {@link JButton} that would run the
 * {@link ActionListener} returned by {@link #createActionListener} when clicked -
 * for most widgets this would simply call {@link #action action}.
 * Likewise a {@link JMenuItem} would also be created the same way.
 * </p><p>
 * The two component were independent, and had to be enabled or disabled
 * independently. Retrieving the components could be done by calling
 * {@link PDFViewer#getNamedComponent getViewer.getNamedCompoment(name)}, where <code>name</code> was
 * <code>Button<i>NNN</i></code> or <code>Menu<i>NNN</i></code> (<i>NNN</i> being the {@link #getName name}
 * of the feature).
 * </p><p>
 * If the widget left the {@link #setDocumentRequired documentRequired} field to the default value of true,
 * then both the button and menu components would be added to a list of JComponents which would have their
 * {@link JComponent#setEnabled enabled} state updated depending on whether the PDFViewer had an active
 * document panel. Here's a typical example.
 * </p>
 * <pre class="example">
 * public class MyWidget extends ViewerWidget {
 *    public MyWidget() {
 *        super("MyWidgetName");
 *        setButton("ToolbarName", "resources/icons/myicon.png", "Tooltip text");
 *        setMenu("MenuName\tMenuItemName", 'z');
 *    }
 *    public void action(ViewerEvent event) {
 *        DocumentPanel panel = event.getViewer().getActiveDocumntPanel();
 *        // Run action here
 *    }
 * }
 * </pre>
 * <p>
 * This approach has proved inflexible as the viewer has grown more capable, in particular since the
 * arrival of the {@link DocumentPanel#hasPermission permission} framework, which implies that features
 * are no longer enabled based solely on whether a PDF is available. It is no long recommended
 * for new Widgets, although it will continue to work as described above. For new Widgets and those with
 * more complex requirements for enablement, we recommend the approach described below.
 * </p>
 * <h3>Internals (the new way)</h3>
 * <p>
 * The recommended approach for more complex Widgets is to override {@link #createActionListener} to
 * return an {@link Action} instead of a simple {@link ActionListener}. One of both of {@link #setButton setButton()}
 * or {@link #setMenu setMenu()} must still be called to add the action to the toolbar or menu (but any Icon, Name,
 * Tooltip or Accelerator key set on the Action will override values specified in these calls).
 * </p><p>
 * This is a much more Swing-like approach: there is one Action shared by the button, menu (or any other
 * components you may create that trigger it), and you manage the enabled status of the Action, rather
 * than the components. The Action will be disabled by default if the
 * {@link #setDocumentRequired documentRequired} field is left at the default value of true, but other
 * than that you must manage when the Action is enabled by reacting to
 * {@link DocumentPanelEvent DocumentPanelEvents}.
 * Here's an example which is functionally identical to the above example.
 * </p>
 * <pre class="example">
 * public class MyWidget extends ViewerWidget {
 *    private AbstractAction action;
 *
 *    public MyWidget() {
 *        super("MyWidgetName");
 *        setButton("ToolbarName", "resources/icons/myicon.png", "Tooltip text");
 *        setMenu("MenuName\tMenuItemName", 'z');
 *        action = new AbstractAction() {
 *            public void actionPerformed(ActionEvent event) {
 *                action(new ViewerEvent(event, getViewer()));
 *            }
 *        };
 *    }
 *    public ActionListener createActionListener() {
 *        return action;
 *    }
 *    public void action(ViewerEvent event) {
 *        DocumentPanel panel = event.getViewer().getActiveDocumentPanel();
 *        // Run action here
 *    }
 * }
 * </pre>
 * <p>
 * If the action is only to be enabled when a PDF is available and (for example) the <code>Assemble</code>
 * {@link DocumentPanel#hasPermission permission} is set, then you could augment the above code with the
 * following.
 * </p>
 * <pre class="example">
 * public void initialize(final PDFViewer viewer) {
 *     super.initialize(viewer);
 *     viewer.addDocumentPanelListener(new DocumentPanelListener() {
 *         public void documentUpdated(DocumentPanelEvent event) {
 *             String type = event.getType();
 *             if (type.equals("activated") || (type.equals("permissionChanged") &amp;&amp; event.getDocumentPanel() == viewer.getActiveDocumentPanel())) {
 *                 action.setEnabled(event.getDocumentPanel().hasPermission("Assemble"));
 *             } else if (type.equals("deactivated")) {
 *                 action.setEnabled(false);
 *             }
 *         }
 *     });
 * }
 * </pre>
 * <p>
 * Of course other designs for this widget are possible, including skipping {@link #action action()}
 * and running the code directly from
 * {@link ActionListener#actionPerformed Action.actionPerformed()}, or having the widget itself implement
 * {@link Action} and {@link DocumentPanelListener} to remove the anonymous inner classes.
 * </p>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @see ToggleViewerWidget
 * @since 2.8
 */
public class ViewerWidget extends ViewerFeature {

    private String toolbarname, iconpath, tooltip, menu;
    private char mnemonic;
    private boolean documentrequired, toolbarenabled, toolbarenabledalways, toolbarfloatable, floating;
    private JComponent component;
    private PDFViewer viewer;
    private static final String LARGE_ICON_KEY = "SwingLargeIconKey";   // From Action but 1.6

    /**
     * Create a new Widget
     */
    public ViewerWidget(String name) {
        super(name);
        setDocumentRequired(true);
        setToolBarEnabled(true);
        setToolBarFloatable(true);
    }

    public String toString() {
        return "Widget:"+super.toString();
    }

    /**
     * Return an ActionListener that will be called when this Widget is activated.
     * Subclasses will typically not need to override this method except in special cases.
     * @see Quit#createActionListener
     * @return ActionListener the ActionListener to be notified when an event fires
     */
    protected ActionListener createActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                action(new ViewerEvent(evt, viewer));
            }
        };
    }

    /**
     * Return the icon specified by the supplied path, or null if no such icon exists
     */
    protected ImageIcon getIcon(String path) {
        URL url;
        if (path.length() > 0 && path.charAt(0) == '/') {
            url = getClass().getClassLoader().getResource(path.substring(1));
        } else {
            // Scan through each superclass for relative path icon: makes specifying
            // these a little easier.
            Class clazz = getClass();
            do {
                url = clazz.getResource(path);
                clazz = clazz.getSuperclass();
            } while (url == null && clazz != ViewerFeature.class);
        }
        return url == null ? null : new ImageIcon(url);
    }

    public void initialize(final PDFViewer viewer) {
        if (getViewer() != null) {
            throw new IllegalStateException("Feature already added to another viewer");
        }
        this.viewer = viewer;
        PropertyManager propertymanager = viewer.getPropertyManager();
        String property = getClass().getName();
        if (property.startsWith("org.faceless.pdf2.viewer3.")) {
            property = property.substring(26);
        }

        final ActionListener listener = createActionListener();
        Action action;
        if (listener instanceof Action) {
            action = (Action)listener;
            if (documentrequired) {
                action.setEnabled(false);
            }
        } else {
            action = new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    listener.actionPerformed(event);
                }
            };
            if (documentrequired) {
                action.setEnabled(false);
                final Action faction = action;
                viewer.addDocumentPanelListener(new DocumentPanelListener() {
                    public void documentUpdated(DocumentPanelEvent event) {
                        String type = event.getType();
                        if ("activated".equals(type)) {
                            faction.setEnabled(true);
                        } else if ("deactivated".equals(type)) {
                            faction.setEnabled(false);
                        }
                    }
                });
            }
        }

        // Set icon, toolip and name on Action
        if (tooltip != null && action.getValue(Action.SHORT_DESCRIPTION) == null) {
            String localizedtooltip = UIManager.getString(tooltip);
            if (localizedtooltip == null) {
                if (viewer.getPropertyManager().getProperty("debug.L10N") != null) {
                    System.out.println("No localization for \""+tooltip+"\"");
                }
                localizedtooltip = tooltip;
            }
            action.putValue(Action.SHORT_DESCRIPTION, localizedtooltip);
        }
        if (iconpath != null && action.getValue(Action.SMALL_ICON) == null && action.getValue(LARGE_ICON_KEY) == null) {
            ImageIcon icon = getIcon(iconpath);
            if (icon != null) {
                try {
                    // Action.LARGE_ICON_KEY is Java16, but following line doesn't
                    // throw an exception in Java15. Presumably constant is being
                    // expanded at compile time. Downside of using SMALL_ICON is it's
                    // used in the menus as well, but we already have a wrapper to
                    // prevent this for Java15 case, so run it for both.
                    action.putValue(Action.LARGE_ICON_KEY, icon);       // Java16
                    action.putValue(Action.SMALL_ICON, icon);
                } catch (Throwable e) {
                    action.putValue(Action.SMALL_ICON, icon);
                }
            }
        }

        boolean buttonenabled = isButtonEnabledByDefault();
        if (propertymanager.getProperty(property+".button") != null) {
            buttonenabled = !propertymanager.getProperty(property+".button").equals("false");
        }
        buttonenabled &= toolbarname != null;
        if (buttonenabled) {
            boolean ltrsubpanel = false;
            if (toolbarname.endsWith(".ltr")) {
                // Hack to ensure page first/prev/number/right/last are in LTR order:
                // we put them into a subpanel which forces its ComponentOrientation
                toolbarname = toolbarname.substring(0, toolbarname.length() - 4);
                ltrsubpanel = true;
            }
            JComponent toolbar = viewer.getToolBar(toolbarname, toolbarenabled, toolbarenabledalways, toolbarfloatable, floating);
            String compname = getName();
            if (component == null) {
                if (action.getValue(LARGE_ICON_KEY) == null && action.getValue(Action.SMALL_ICON) == null) {
                    if (iconpath == null) {
                        throw new NullPointerException("No icon specified for button");
                    } else {
                        throw new NullPointerException("Can't find icon \""+iconpath+"\" for button");
                    }
                }

                JButton button = new JButton() {
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (isSelected()) {
                            BorderFactory.createLoweredBevelBorder().paintBorder(this, g, 0, 0, getWidth()-1, getHeight()-1);
                            g.setColor(new Color(0x20000000, true));
                            g.fillRect(0, 0, getWidth(), getHeight());
                        }
                    }
                };
                // Tweaks for Look and Feel
                if (Util.isLAFAqua()) {
                    if (System.getProperty("java.version").compareTo("1.5.0_16") >= 0) {
                        button.setMargin(new Insets(0, 0, 0, 0));
                    } else {
                        button.setMargin(new Insets(4, 5, 5, 5));
                    }
                } else if (Util.isLAFGTK()) {
                    button.setMargin(new Insets(0, 0, 0, 0));
                } else if (Util.isLAFNimbus()) {
                    button.setMargin(new Insets(1, 1, 1, 1));
                } else {
                    button.setMargin(new Insets(2, 2, 2, 2));
                }
                button.setBorderPainted(false);
                button.putClientProperty("hideActionText", Boolean.TRUE); // setHideActionText() is Java16
                button.setAction(action);
                component = button;
                compname = "Button"+compname;
            }
            viewer.putNamedComponent(compname, component);

            JComponent parent = toolbar;
            if (toolbar instanceof JInternalFrame) {
                parent = (JComponent)((JInternalFrame)parent).getContentPane();
            }
            if (ltrsubpanel) {
                if (parent.getComponentCount() == 0 || !(parent.getComponent(0) instanceof JToolBar)) {
                    final JToolBar ltr = new JToolBar();
                    ltr.setLayout(new GridBagLayout() {
                        { defaultConstraints.fill = GridBagConstraints.BOTH; defaultConstraints.weighty = 1; }
                    });
                    ltr.setOpaque(false);
                    ltr.setBorder(BorderFactory.createEmptyBorder());
                    ltr.setFloatable(false);
                    ltr.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName().equals("componentOrientation")) {
                                ComponentOrientation orient = (ComponentOrientation)event.getNewValue();
                                if (orient != null && !orient.isLeftToRight()) {
                                    ltr.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                                }
                            }
                        }
                    });
                    parent.add(ltr);
                    parent = ltr;
                } else {
                    parent = (JComponent)parent.getComponent(0);
                }
            }
            parent.add(component);
            if (toolbar instanceof JInternalFrame) {
                ((JInternalFrame)toolbar).pack();
            }
        }

        boolean menuenabled = isMenuEnabledByDefault();
        if (propertymanager.getProperty(property+".menu") != null) {
            menuenabled = !propertymanager.getProperty(property+".menu").equals("false");
        }
        menuenabled &= menu != null;
        if (menuenabled) {
            JMenuItem item = viewer.setMenu(menu, mnemonic, documentrequired, action);
            if (component == null) {
                component = item;
            }
            viewer.putNamedComponent("Menu"+getName(), item);
        }
    }

    /**
     * Get the Viewer this Feature has been added to.
     */
    public final PDFViewer getViewer() {
        return viewer;
    }

    /**
     * Set whether this feature requires a PDF to be loaded. Most
     * features except for the "Open" widget do, so the default is "true"
     */
    protected final void setDocumentRequired(boolean required) {
        this.documentrequired = required;
    }

    /**
     * Return whether this widget should be inactive if no Document is
     * selected.
     * @see #setDocumentRequired
     * @since 2.11.7
     */
    public boolean isDocumentRequired() {
        return this.documentrequired;
    }

    /**
     * Set a custom component to be displayed in the ToolBar for this feature.
     * @param toolbar the name of the toolbar to put the component in
     * @param component the component
     */
    protected final void setComponent(String toolbar, JComponent component) {
        this.toolbarname = toolbar;
        this.component = component;
    }

    /**
     * Return the component representing this Widget.
     * @since 2.8.5
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Return true if the button component for this widget is enabled by default.
     * The default is "true"
     * @since 2.10.3
     */
    public boolean isButtonEnabledByDefault() {
        return true;
    }

    /**
     * Return true if the menu component for this widget is enabled by default.
     * The default is "true"
     * @since 2.10.3
     */
    public boolean isMenuEnabledByDefault() {
        return true;
    }

    /**
     * Set this feature to use a regular button in the toolbar. The button will be
     * created using the specified icon and with the specified tooltip.
     * @param toolbar the name of the toolbar to put the component in
     * @param icon the URL of the icon to use. If the return value of {@link #createActionListener}
     * is an {@link Action} that specifies an icon already, this will be ignored.
     * @param tooltip the tooltip to display for this button. If the return value of
     * {@link #createActionListener} is an {@link Action} that specifies a tooltip value already,
     * this will be ignored.
     */
    protected final void setButton(String toolbar, String icon, String tooltip) {
        this.toolbarname = toolbar;
        this.iconpath = icon;
        this.tooltip = tooltip;
    }

    /**
     * Set whether the toolbar this feature is stored in is enabled by default
     */
    protected final void setToolBarEnabled(boolean enabled) {
        this.toolbarenabled = enabled;
    }

    /**
     * Set whether the toolbar this feature is stored in can be enabled or disabled
     * @see ToolbarDisabling
     */
    protected final void setToolBarEnabledAlways(boolean always) {
        this.toolbarenabledalways = always;
    }

    /**
     * Set whether the toolbar this feature is stored in can be floated
     */
    protected final void setToolBarFloatable(boolean floatable) {
        this.toolbarfloatable = floatable;
    }

    /**
     * Set whether this toolbar is always floating or not. Toolbars with this set
     * are implemented as JInternalFrame objects, and are never attached to the
     * regular tool bar
     * @since 2.8.3
     */
    protected final void setToolBarFloating(boolean floating) {
        this.floating = floating;
    }

    /**
     * Set a menu item for this feature. Activating the menu item is the
     * same as pressing the button.
     * @param menu the menu hierarchy to use, separated with tabs - eg "File\tOpen".
     * If the return value of {@link #createActionListener} is an {@link Action} that
     * specifies a Name already, the last part of this menu will be ignored and that name used instead.
     */
    protected final void setMenu(String menu) {
        setMenu(menu, (char)0);
    }

    /**
     * Set a menu item for this feature, with an optional keyboard shortcut.
     * @param menu the menu hierarchy to use, separated with tabs - eg "File\tOpen".
     * If the return value of {@link #createActionListener} is an {@link Action} that
     * specifies a Name already, the last part of this menu will be ignored and that name used instead.
     * @param mnemonic the keyboard shortcut to activate the menu - a lowercase or uppercase
     * character to activate the menu.
     * If the return value of {@link #createActionListener} is an {@link Action} that
     * specifies an Accelerator key already, this will be ignored.
     * @since 2.10.2
     */
    protected final void setMenu(String menu, char mnemonic) {
        this.menu = menu;
        this.mnemonic = mnemonic;
    }

    /**
     * The method that's run when this feature is activated. This method is called by the
     * {@link ActionListener} returned by the default implementation of
     * {@link #createActionListener}, and by default is a no-op.
     */
    public void action(ViewerEvent event) { }

}
