// $Id: PDFViewer.java 21055 2015-03-04 12:38:10Z mike $

package org.faceless.pdf2.viewer3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;

import org.faceless.pdf2.EncryptionHandler;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.PropertyManager;
import org.faceless.pdf2.viewer3.feature.ActiveWindowMenu;
import org.faceless.pdf2.viewer3.feature.Menus;
import org.faceless.pdf2.viewer3.feature.MultiWindow;
import org.faceless.pdf2.viewer3.feature.OpenRecent;
import org.faceless.pdf2.viewer3.feature.PDFImporter;
import org.faceless.pdf2.viewer3.feature.ToolbarDisabling;
import org.faceless.pdf2.viewer3.feature.ToolbarFloating;
import org.faceless.pdf2.viewer3.feature.Toolbars;
import org.faceless.pdf2.viewer3.util.AppletCheckboxMenuItem;
import org.faceless.pdf2.viewer3.util.AppletMenuItem;
import org.faceless.pdf2.viewer3.util.BoundedDesktopManager;
import org.faceless.pdf2.viewer3.util.KeyStoreTrustManager;
import org.faceless.pdf2.viewer3.util.PromptingAuthenticator;
import org.faceless.util.ToolBarLayout;

import br.com.ibracon.idr.form.model.LivroIDR;

/**
 * The <code>PDFViewer</code> class is a simple Swing PDF viewer application.
 * It demonstrates the {@link DocumentPanel} class, and can be run directly
 * from the JAR (via the {@link PDFTool} class) like so.
 * <pre>
 * java -jar bfopdf.jar <i>filename</i>
 * </pre>
 * The <i>filename</i> argument is optional, but if supplied will load the
 * specified PDF.
 * See the <a href="doc-files/tutorial.html">viewer tutorial</a> for more detail on how to use this class and the "viewer" package.
 *
 * <div class="initparams">
 * The following <a href="doc-files/initparams.html">initialization parameters</a> may be specified
 * <table summary="">
 * <tr><th>currentUser</th><td>The name of the current user - optional, but if set this will be set as the author field on any annotations created by the user</td></tr>
 * <tr><th>unpromptedDirtyClose</th><td>Whether to prompt the user for confirmation when closing a window containng a PDF that has been {@link DocumentPanel#setDirty modified}</td></tr>
 * <tr><th>dpi</th><td>The resolution of the screen. This can be set to override {@link Toolkit#getScreenResolution} if required</td></tr>
 * </table>
 * </div>
 *
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 *
 * @since 2.5, with major rewrites in 2.8
 */
public class PDFViewer extends JPanel implements Scrollable {

    static final ImageIcon BFO16 = new ImageIcon(PDFViewer.class.getResource("resources/BFO16.png"));
    private static final int MINIMIZEDWIDTH = 600;
    private static final int MINIMIZEDHEIGHT = 400;

    // Components
    private JDesktopPane desktop;     // Used when MultiWindow is set
    private JMenuBar menubar;
    private Map<String,JComponent> components;   // Map containing all components
    private Map<String,JComponent> toolbars;       // Map containing all the Toolbars that can be activated
    private Map<String,JMenu> menus;                // Map containing all the Menus
    private Collection<DocumentPanelListener> listeners;     // Collection of DocumentPanelListeners

    // Other bits
    private final Collection<ViewerFeature> features;
    private int internalFrameCounter = 0;
    private DocumentPanel activedocument;
    private KeyStoreManager keystoremanager;
    private PropertyManager propertymanager;
    private JSManager jsmanager;
    private Preferences preferences;
    private volatile boolean createdEventPending; // For lazy create event on non MultiWindow viewers
    private boolean initialized, unprompteddirtyclose;
    private String user;
    private LivroIDR livroIDR;

    /**
     * Creates new PDFViewer with the specified features
     * @param features a Collection of {@link ViewerFeature} objects that are enabled
     * @see ViewerFeature
     * @since 2.7.9
     */
    public PDFViewer(Collection<ViewerFeature> features) {
        Util.initialize();
        this.features = Collections.unmodifiableCollection(features);
        setPropertyManager(new PropertyManager() {
            public String getProperty(final String key) {
                try {
                    return AccessController.doPrivileged(new PrivilegedAction<String>() {
                        public String run() {
                        	System.out.println(key);
                            String x = System.getProperty("org.faceless.pdf2.viewer3."+key);
                            if (x == null) {
                                x = System.getProperty(key);
                            }
                            return x;
                        }
                    });
                } catch (SecurityException e) {
                    return null;
                }
            }
            public URL getURLProperty(String key) throws java.net.MalformedURLException {
                String val = getProperty(key);
                return val==null ? null : new URL(val);
            }
        });
        try {
            preferences = Preferences.userNodeForPackage(PDFViewer.class);
        } catch (Exception e) {
            // Ignore
        }
        components = new LinkedHashMap<String,JComponent>();
        toolbars = new LinkedHashMap<String,JComponent>();
        menus = new LinkedHashMap<String,JMenu>();
        listeners = new LinkedHashSet<DocumentPanelListener>();

        setLayout(new BorderLayout());
        setOpaque(true);

        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent event) {
                removeHierarchyListener(this);
                initialize();
            }
        });
        try {
            user = System.getProperty("user.name");
        } catch (Throwable e) { }
    }

    private synchronized void initialize() {
        if (initialized) return;
        initialized = true;

        getMenu("File");           // Initialize the order
        getMenu("Edit");
        getMenu("View");
        getMenu("Document");

        for (Iterator<ViewerFeature> i=features.iterator();i.hasNext();) {
            ViewerFeature feature = i.next();
            feature.initialize(this);
        }

        if (hasFeature(Toolbars.getInstance())) {
            // Ensure items added to this toolbarPanel are added in the order defined in
            // the "toolbars" map. Why? So if we float a toolbar then unfloat it, it drops
            // back into the correct position. Why not use setComponentZOrder() on a
            // ancestor listener? Because those events don't seem to fire in a predictable
            // order. This is more predictable, and anyway it works under Java 1.4.
            JPanel toolbarPanel = new JPanel(new ToolBarLayout()) {
                protected void addImpl(Component comp, Object constraints, int index) {
                    int pos = 0;
                    for (Iterator<JComponent> i = toolbars.values().iterator();i.hasNext();pos++) {
                        if (i.next() == comp) {
                            index = pos >= getComponentCount() ? -1 : pos;
                            break;
                        }
                    }
                    super.addImpl(comp, constraints, index);
                }
            };
            toolbarPanel.setFont(toolbarPanel.getFont().deriveFont(10f));
            for (Iterator<Map.Entry<String,JComponent>> i = toolbars.entrySet().iterator();i.hasNext();) {
                Map.Entry<String,JComponent> e = i.next();
                String key = e.getKey();
                JComponent toolbar = e.getValue();
                toolbar.setFont(null);
                if (toolbar instanceof JToolBar) {
                    // If we only have one "ViewMode", no point in displaying toolbar
                    int minbuttons = "ViewMode".equals(key) ? 1 : 0;
                    if (toolbar.getComponentCount()>minbuttons) {
                        toolbarPanel.add(toolbar);
                    }
                } else {
                    add(toolbar);
                }
            }
            if (toolbarPanel.getComponentCount() > 0) {
                int bw = Util.isLAFWindows() ? 1 : 0;
                toolbarPanel.setBorder(BorderFactory.createEmptyBorder(bw, bw, bw, bw));
                toolbarPanel.setOpaque(false);
                add(toolbarPanel, BorderLayout.NORTH);
            }
        }

        if (hasFeature(Menus.getInstance())) {
            menubar = new JMenuBar();
            menubar.setDoubleBuffered(true);

            if (hasFeature(Toolbars.getInstance()) && hasFeature(ToolbarDisabling.getInstance())) {
                JMenuItem mToolbars = new JMenu(UIManager.getString("PDFViewer.Toolbars"));
                for (Iterator<Map.Entry<String,JComponent>> i = toolbars.entrySet().iterator();i.hasNext();) {
                    Map.Entry<String,JComponent> e = i.next();
                    final String key = e.getKey();
                    final JComponent toolbar = e.getValue();
                    if (!key.endsWith(" ") && toolbar.getComponentCount() > 0) {
                        final JCheckBoxMenuItem toolbaritem = new JCheckBoxMenuItem();
                        mToolbars.add(toolbaritem);
                        String name = UIManager.getString("PDFViewer."+key);
                        if (name==null) {
                            if (getPropertyManager().getProperty("debug.L10N")!=null) {
                                System.err.println("No localization for menu: PDFViewer."+key);
                            }
                            name = key;
                        }
                        toolbaritem.setText(name);
                        toolbaritem.setSelected(toolbar.isVisible());
                        toolbaritem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                toolbar.setVisible(toolbaritem.getState());
                            }
                        });
                    }
                }
                getMenu("View").add(mToolbars, 0);
            }

            if (hasFeature(MultiWindow.getInstance())) {
                JMenuItem item = setMenu("Window\tCascade", (char)0, false, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cascadeFrames();
                    }
                });
                components.put("MenuCascade", item);
            }

            for (Iterator<Map.Entry<String,JMenu>> i = menus.entrySet().iterator();i.hasNext();) {
                Map.Entry<String,JMenu> e = i.next();
                String key = e.getKey();
                JMenu menu = e.getValue();
                if (menu.getItemCount()>0) {
                    menubar.add(menu);
                }
            }

            if (menubar.getComponentCount() > 0) {
                // Add the menubar to the parenet frame when the frame is set
                addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if ("ancestor".equals(event.getPropertyName())) {
                            boolean browserapplet = Util.isBrowserApplet(PDFViewer.this);
                            JApplet applet = (JApplet)SwingUtilities.getAncestorOfClass(JApplet.class, PDFViewer.this);
                            JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, PDFViewer.this);
                            if (applet != null) {
                                applet.setJMenuBar(menubar);
                                List<JMenuItem> l = new ArrayList<JMenuItem>();           // remove all mnemonics
                                for (int i=0;i<menubar.getMenuCount();i++) {
                                    l.add(menubar.getMenu(i));
                                }
                                for (int i=0;i<l.size();i++) {
                                    JMenuItem item = (JMenuItem)l.get(i);
                                    if (item instanceof AppletMenuItem) {
                                        ((AppletMenuItem)item).setBrowserApplet(browserapplet);
                                    } else if (item instanceof AppletCheckboxMenuItem) {
                                        ((AppletCheckboxMenuItem)item).setBrowserApplet(browserapplet);
                                    } else if (item instanceof JMenu) {
                                        for (int j=0;j<((JMenu)item).getItemCount();j++) {
                                            l.add(((JMenu)item).getItem(j));
                                        }
                                    }
                                }
                            } else if (frame != null) {
                                frame.setJMenuBar(menubar);
                            }
                        }
                    }
                });
            }
        }

        if (hasFeature(MultiWindow.getInstance())) {
            desktop = new JDesktopPane();
            desktop.setDesktopManager(new BoundedDesktopManager());
            desktop.setPreferredSize(new Dimension(600, 400));
            desktop.setOpaque(true);
            desktop.setBackground(Color.gray);
            add(desktop, BorderLayout.CENTER);

        } else {
            activedocument = new DocumentPanel();
            createdEventPending = true;
            activedocument.setViewer(this);
            activedocument.setFocusable(true);
            for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
                ViewerFeature feature = i.next();
                if (feature instanceof SidePanelFactory) {
                    activedocument.addSidePanelFactory((SidePanelFactory)feature);
                } else if (feature instanceof AnnotationComponentFactory) {
                    activedocument.addAnnotationComponentFactory((AnnotationComponentFactory)feature);
                } else if (feature instanceof ActionHandler) {
                    activedocument.addActionHandler((ActionHandler)feature);
                }
            }
            activedocument.setPreferredSize(new Dimension(600, 400));
            add(activedocument, BorderLayout.CENTER);
            for (Iterator<DocumentPanelListener> i = listeners.iterator();i.hasNext();) {
                activedocument.addDocumentPanelListener(i.next());
            }
        }
        updatePropertyManager();
        firePropertyChange("bfo.initialized", false, true);
    }

    /**
     * Return the {@link Preferences} object that should be used to store user preferences
     * about the viewer, or <code>null</code> if preferences cannot be saved.
     * @since 2.11
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * Return the JSManager object for this PDFViewer.
     * @since 2.9
     */
    public synchronized JSManager getJSManager() {
        initialize();
        if (jsmanager==null) {
            // Create a JSManager and initialize it as soon as possible, whether application is displayed or not
            jsmanager = new JSManager(this);
            jsmanager.runEventAppInit();
        }
        return jsmanager;
    }

    /**
     * Set the {@link PropertyManager} in use by this PDFViewer
     * @since 2.8.5
     */
    public final void setPropertyManager(PropertyManager manager) {
        if (manager == null) {
            throw new NullPointerException();
        }
        this.propertymanager = manager;
        updatePropertyManager();
    }

    private void updatePropertyManager() {
        String val = propertymanager.getProperty("currentUser");
        if (user != null) {
            setCurrentUser(val);
        }
        val = propertymanager.getProperty("unpromptedDirtyClose");
        unprompteddirtyclose = val != null && !"false".equals(val);
    }

    /**
     * Get the {@link PropertyManager} in use by this PDFViewer
     * @since 2.8.5
     */
    public final PropertyManager getPropertyManager() {
        return this.propertymanager;
    }

    /**
     * Get the {@link KeyStoreManager} in use by this PDFViewer
     * @since 2.8.3
     */
    public synchronized KeyStoreManager getKeyStoreManager() {
        initialize();
        if (keystoremanager==null) {
            keystoremanager = new KeyStoreManager(this);
            try {
                // Check legacy properties first
                String[] s = new String[] { "type", "provider", "file", "password" };
                for (int i=0;i<s.length;i++) {
                    String val = getPropertyManager().getProperty("KeyStoreManager."+s[i]);
                    if (val != null) {
                        keystoremanager.setParameter(s[i], val);
                    }
                }
                String val = getPropertyManager().getProperty("KeyStoreManager.params");
                if (val != null) {
                    keystoremanager.setParameters(val);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return keystoremanager;
    }

    /**
     * Set the {@link KeyStoreManager} used by this PDFViewer
     * @since 2.8.3
     */
    public synchronized void setKeyStoreManager(KeyStoreManager manager) {
        this.keystoremanager = manager;
    }

    /**
     * Return true if the Viewer has a certain feature enabled.
     * @param feature the feature to look for
     */
    public boolean hasFeature(ViewerFeature feature) {
        initialize();
        return features.contains(feature);
    }

    /**
     * Return true if the Viewer has a certain feature enabled.
     * @param feature the name of the feature to look for
     */
    public boolean hasFeature(String feature) {
        initialize();
        for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
            if (i.next().getName().equals(feature)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the list of all features set in the viewer
     */
    public ViewerFeature[] getFeatures() {
        initialize();
        return new ArrayList<ViewerFeature>(features).toArray(new ViewerFeature[features.size()]);
    }

    /**
     * Return the {@link ViewerFeature} with the specified {@link ViewerFeature#getName name}, or
     * <code>null</code> if it doesn't exist for this viewer.
     * @param feature the name of the feature
     * @since 2.8.5
     */
    public ViewerFeature getFeature(String feature) {
        initialize();
        for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
            ViewerFeature f = i.next();
            if (f.getName().equals(feature)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Return the first {@link ViewerFeature} of the specified class, or
     * <code>null</code> if it doesn't exist for this viewer. For example
     * <pre class="example">
     * PDFImporter importer = viewer.getFeature(PDFImporter.class);
     * </pre>
     * @param clazz the Class of the ViewerFeature.
     * @since 2.10.2
     */
    @SuppressWarnings("unchecked") public <T extends ViewerFeature> T getFeature(Class<T> clazz) {
        initialize();
        for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
            ViewerFeature f = i.next();
            if (clazz.isInstance(f)) {
                return (T)f;
            }
        }
        return null;
    }

    //-------------------------------------------------------------------------------
    // Methods to be called by Features when they're adding themselves to this Viewer
    //-------------------------------------------------------------------------------

    /**
     * Return the specifeid ToolBar. If the toolbar doesn't exist, it's created.
     * @param name the name of the ToolBar
     * @param enabled whether the toolbar is enabled or not
     * @param always whether the toolbar is always in it's enabled state
     */
    final JComponent getToolBar(String name, boolean enabled, boolean enabledalways, boolean floatable, boolean floating) {
        name = name.trim();
        if (enabledalways) {
            name = name + " ";
        }
        if (floating) {
            final String nname = name;
            final JInternalFrame toolbar = new JInternalFrame(name, false, true);
            if (hasFeature(ToolbarDisabling.getInstance())) {
                toolbar.addInternalFrameListener(new InternalFrameListener() {
                    public void internalFrameClosing(InternalFrameEvent e) {
                        JMenu menu = (JMenu)getMenu("View").getItem(0);
                        for (int i=0;i<menu.getItemCount();i++) {
                            if (nname.equals(menu.getItem(i).getText())) {
                                ((JCheckBoxMenuItem)menu.getItem(i)).setState(false);
                            }
                        }
                        toolbar.dispose();
                    }
                    public void internalFrameActivated(InternalFrameEvent e) { }
                    public void internalFrameDeactivated(InternalFrameEvent e) { }
                    public void internalFrameDeiconified(InternalFrameEvent e) { }
                    public void internalFrameIconified(InternalFrameEvent e) { }
                    public void internalFrameOpened(InternalFrameEvent e) { }
                    public void internalFrameClosed(InternalFrameEvent e) { }
                });
            }
            toolbar.setLocation(100+(toolbars.size()*20), 60+(toolbars.size()*20));
            toolbar.setVisible(enabled);
            toolbars.put(name, toolbar);
            return toolbar;
        } else {
            if (!toolbars.containsKey(name)) {
                JToolBar toolbar = new JToolBar();
                toolbar.setLayout(new GridBagLayout() {
                    { defaultConstraints.fill = GridBagConstraints.BOTH; defaultConstraints.weighty = 1; }
                });
                toolbar.setBorder(BorderFactory.createEtchedBorder());
                toolbar.setFloatable(floatable && !Util.isLAFNimbus() && !Util.isLAFGTK() && hasFeature(ToolbarFloating.getInstance()));
                toolbar.setVisible(enabled);
                toolbar.setOpaque(false);
                toolbars.put(name, toolbar);
            }
            return toolbars.get(name);
        }
    }

    /**
     * Add or replace a menu item in the viewer.
     * Since 2.13.1, if the supplied listener is an {@link Action} then the {@link Action#NAME}
     * and {@link Action#ACCELERATOR_KEY} (if set) will override the <code>name</code> and
     * <code>mnemonic</code> supplied here, and the <code>documentrequired</code> parameter will
     * be ignored, as the Action is expected to manage it's own {@link Action#isEnabled enabled}
     * state.
     *
     * @param name the name of the menu, if the form "File\tOpen" or "File\tQuit(999)". The tab
     * character is used to separate the items in the menu hierarchy, and the optional bracketed
     * value is used to control ordering in the menu.
     * @param mnemonic the mnemonic to assign to this menu - lowercase or uppercase letter, or
     * <code>(char)0</code> for no mnenonic
     * @param documentrequired whether this menu item should only be enabled if a document is loaded
     * @param listener the {@link ActionListener} to run when this menu item is activated
     * @since 2.10.2
     */
    public JMenuItem setMenu(String name, char mnemonic, boolean documentrequired, final ActionListener listener) {
        Action action;
        if (listener instanceof Action) {
            action = (Action)listener;
            if (action.getValue(Action.SMALL_ICON) != null) {
                // We don't do icons in menus, but we target Java 1.5 which doesn't
                // have Action.LARGE_ICON_KEY. So wrap action in icon-removing version
                final Action origaction = action;
                action = new Action() {
                    public void addPropertyChangeListener(PropertyChangeListener listener) {
                        origaction.addPropertyChangeListener(listener);
                    }
                    public Object getValue(String key) {
                        return Action.SMALL_ICON.equals(key) ? null : origaction.getValue(key);
                    }
                    public boolean isEnabled() {
                        return origaction.isEnabled();
                    }
                    public void putValue(String key, Object value) {
                        origaction.putValue(key, value);
                    }
                    public void removePropertyChangeListener(PropertyChangeListener listener) {
                        origaction.removePropertyChangeListener(listener);
                    }
                    public void setEnabled(boolean b) {
                        origaction.setEnabled(b);
                    }
                    public void actionPerformed(ActionEvent event) {
                        origaction.actionPerformed(event);
                    }
                };
            }
        } else {
            action = new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    listener.actionPerformed(event);
                }
            };
            if (documentrequired) {
                final Action faction = action;
                action.setEnabled(false);
                addDocumentPanelListener(new DocumentPanelListener() {
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

        boolean checkbox = name.startsWith("+");
        if (checkbox) {
            name = name.substring(1);
        }
        boolean ellipsis = name.endsWith("...");
        if (ellipsis) {
            name = name.substring(0, name.length() - 3);
        }
        String[] sections = name.split("\t");
        int[] sorts = new int[sections.length];
        for (int i=1;i<sections.length;i++) {
            String section = sections[i];
            sorts[i] = 10;
            int pos = section.lastIndexOf("(");
            if (pos >= 0 && section.endsWith(")")) {
                try {
                    sorts[i] = Integer.parseInt(section.substring(pos+1, section.length()-1));
                    sections[i] = section = section.substring(0, pos);
                } catch (RuntimeException e) { }
            }
            String sectiontext = UIManager.getString("PDFViewer."+section);
            if (sectiontext == null) {
                if (getPropertyManager().getProperty("debug.L10N")!=null) {
                    System.err.println("No localization for menu: PDFViewer."+section);
                }
            } else {
                if (ellipsis && i == sections.length - 1) {
                    sectiontext += "\u2026";
                }
                sections[i] = sectiontext;
            }
        }

        JMenu menu = getMenu(sections[0]);
        JMenuItem item = null;
        for (int i=1;i<sections.length;i++) {
            item = null;
            final String section = sections[i];
            final int sort = sorts[i];
            int pos = -1;
            for (int j=0;item==null && j<menu.getItemCount();j++) {
                item = menu.getItem(j);
                if (item != null && item.getText() != null && !item.getText().equals(section)) {
                    Integer oldsort = (Integer)item.getClientProperty("bfo.sort");
                    if (oldsort != null && oldsort.intValue() > sort) {
                        pos = j;
                    }
                    item = null;
                }
            }
            if (item == null) {
                if (i == sections.length-1) {
                    if (mnemonic != 0 && action.getValue(Action.ACCELERATOR_KEY) == null) {
                        // Although mnemonics don't work in applets, we're not
                        // in a hierarchy yet so can't test. Add now, remove
                        // it later if needed.
                        int mask = getToolkit().getMenuShortcutKeyMask();
                        if (Character.isUpperCase(mnemonic)) {
                            mask |= java.awt.Event.SHIFT_MASK;
                        }
                        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(Character.toUpperCase(mnemonic), mask, false));
                    }
                    if (action.getValue(Action.NAME) == null) {
                        action.putValue(Action.NAME, section);
                    }
                    if (checkbox) {
                        AppletCheckboxMenuItem cb = new AppletCheckboxMenuItem(action);
                        cb.setSelected(false);
                        item = cb;
                    } else {
                        item = new AppletMenuItem(action);
                    }
                } else {
                    item = new JMenu(section);
                }
                item.putClientProperty("bfo.sort", Integer.valueOf(sort));
                menu.add(item, pos);
            }
            if (i < sections.length-1) {
                menu = (JMenu)item;
            }
        }
        return item;
    }

    /**
     * Return the specified Menu. If the menu doesn't exist, it's created
     * @param name the name of the Menu, before it's been localized
     * @since 2.10.4
     */
    public JMenu getMenu(String name) {
        String text = UIManager.getString("PDFViewer."+name);
        if (text==null) {
            if (getPropertyManager().getProperty("debug.L10N")!=null) {
                System.err.println("No localization for menu: PDFViewer."+name);
            }
            text = name;
        }
        JMenu menu = menus.get(name);
        if (menu == null) {
            menu = new JMenu(text);
            menus.put(name, menu);
        }
        return menu;
    }

    /**
     * Add a named component to the viewers list
     * @since 2.10.2
     */
    public void putNamedComponent(String name, JComponent value) {
        initialize();
        components.put(name, value);
    }

    /**
     * Return a Component created by a {@link ViewerFeature}
     */
    public JComponent getNamedComponent(String name) {
        return components.get(name);
    }

    /**
     * Get the Frame this Viewer is inside. Used for dialogs
     */
    Frame getFrame() {
        return JOptionPane.getFrameForComponent(this);
    }

    // -- Scrollable interface --
    // The PDFViewer contains DocumentViewports which have scrollable
    // contents, so disable a scrollpane parent here.

    /**
     * Returns {@link #getPreferredSize}; for the {@link Scrollable} interface.
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Returns 0; for the {@link Scrollable} interface.
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 0;
    }

    /**
     * Returns 0; for the {@link Scrollable} interface.
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 0;
    }

    /**
     * Returns true; for the {@link Scrollable} interface.
     */
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Returns true; for the {@link Scrollable} interface.
     */
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }


    //-------------------------------------------------------------------------------
    // Methods for loading a PDF or changing the active PDF
    //-------------------------------------------------------------------------------

    private File chooseFile() {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<File>() {
                public File run() {
                    JFileChooser filechooser = Util.fixFileChooser(new JFileChooser((File)null));
                    int filtercount = 0;
                    for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
                        ViewerFeature feature = i.next();
                        if (feature instanceof Importer) {
                            FileFilter filter = ((Importer)feature).getFileFilter();
                            filechooser.addChoosableFileFilter(filter);
                            if (feature instanceof PDFImporter) {
                                filechooser.setFileFilter(filter);
                            }
                            filtercount++;
                        }
                    }
                    if (filtercount == 0) {
                        return null;
                    }
                    if (preferences != null) {
                        String dir = preferences.get("lastDirectory", null);
                        if (dir == null) {
                            dir = preferences.get("File/dir", null);    // Legacy value
                            if (dir != null) {
                                preferences.remove("File/dir");
                            }
                        }
                        if (dir != null) {
                            filechooser.setCurrentDirectory(new File(dir));
                        }
                    }
                    if (filechooser.showOpenDialog(PDFViewer.this) == JFileChooser.APPROVE_OPTION) {
                        repaint();
                        File file = filechooser.getSelectedFile();
                        if (file != null && preferences != null && file.getParent() != null) {
                            preferences.put("lastDirectory", file.getParent());
                        }
                        if (!file.exists()) {
                            Util.displayThrowable(new IOException("\""+file+"\": No such File"), getFrame());
                            file = null;
                        }
                        return file;
                    } else {
                        return null;
                    }
                }
            });
        } catch (SecurityException e) { 
            return null;
        }
    }

    /**
     * Load a PDF into the viewer from a file. This method will search the
     * {@link Importer} objects in order until it finds one that matches the file,
     * then will use that to load the PDF into the viewer. Prompting for passwords
     * etc. is left to the appropriate {@link Importer} - see the {@link PDFImporter}
     * class for details on the default behaviour.
     * @param ffile the PDF file to load, or <code>null</code> to select it with a chooser
     * @since 2.7.1
     */
    public void loadPDF(final File ffile) {
        try {
            loadPDF(ffile, 0);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Load a PDF into the viewer from a file. This method will search the
     * {@link Importer} objects in order until it finds one that matches the file,
     * then will use that to load the PDF into the viewer. Prompting for passwords
     * etc. is left to the appropriate {@link Importer} - see the {@link PDFImporter}
     * class for details on the default behaviour.
     * @param file the PDF file to load, or <code>null</code> to select it with a chooser
     * @param pagenumber the initial page number to request. May be overridden
     * @since 2.11.10
     */
    public void loadPDF(File file, final int pagenumber) {
        initialize();
        if (file == null) {
            file = chooseFile();
        }
        if (file == null) {
            return;
        } else {
            IOException exception = null;
            for (Iterator<ViewerFeature> i=features.iterator();i.hasNext();) {
                ViewerFeature feature = i.next();
                if (feature instanceof Importer) {
                    Importer importer = (Importer)feature;
                    try {
                        if (importer.matches(file)) {
                            Importer.ImporterTask task = importer.getImporter(this, file);
                            task.setPageNumber(pagenumber);
                            task.start(this, UIManager.getString("PDFViewer.Loading"));
                            return;
                        }
                    } catch (IOException e) {
                        exception = e;
                    }
                }
            }
            if (exception == null) {
                exception = new IOException("Cannot load this file as a PDF");
            }
            Util.displayThrowable(exception, getFrame());
        }
    }

    /**
     * Load a PDF into the viewer from a file using the specified EncryptionHandler.
     * This method is now deprecated - the correct way to load a PDF with custom
     * EncryptionHandlers is to modify the {@link PDFImporter} passed in to the
     * PDF Constructor.
     * @param file the PDF File
     * @param handler the EncryptionHandler to use
     * @since 2.8
     * @deprecated this method has been superceded by the {@link PDFImporter} class
     */
    public void loadPDF(File file, EncryptionHandler handler) {
        initialize();
        loadPDF(file, new EncryptionHandler[] { handler });
    }

    /**
     * Load a PDF into the viewer from a file using the specified list of EncryptionHandler.
     * This method is now deprecated - the correct way to load a PDF with custom
     * EncryptionHandlers is to modify the {@link PDFImporter} passed in to the
     * PDF Constructor.
     * @param file the PDF File
     * @param handlers the list of EncryptionHandlers
     * @since 2.8
     * @deprecated this method has been superceded by the {@link PDFImporter} class
     */
    public void loadPDF(File file, EncryptionHandler[] handlers) {
        initialize();
        if (file == null) {
            file = chooseFile();
        }
        if (file == null) {
            return;
        } else {
            PDFImporter importer = getFeature(PDFImporter.class);
            if (importer == null) {
                importer = new PDFImporter();
            }
            if (handlers != null) {
                Set<EncryptionHandler> storeset = importer.getEncryptionHandlers();
                Set<EncryptionHandler> newset = new HashSet<EncryptionHandler>(Arrays.asList(handlers));
                if (!newset.equals(storeset)) {
                    importer = new PDFImporter();
                    importer.getEncryptionHandlers().clear();
                    importer.getEncryptionHandlers().addAll(newset);
                }
            }
            importer.getImporter(this, file).start(this, UIManager.getString("PDFViewer.Loading"));
        }
    }

    /**
     * <p>
     * Load a PDF into the viewer from an InputStream. Note that this method will
     * return immediately, which means the calling thread <i>must not close</i>
     * the InputStream. Doing so will result in an Exception. The stream will be
     * closed by the reading thread when it is completed.
     * </p><p>
     * This method is now deprecated - the correct way to load a PDF from an
     * InputStream is to call the {@link Importer#getImporter(PDFViewer, InputStream, String, File)}
     * method. For example:
     * </p>
     * <pre class="example">
     * PDFImporter importer = viewer.getFeature(PDFImporter.class);
     * importer.getImporter(viewer, inputstream, title, file).start(viewer, "Loading");
     * </pre>
     * @param in the InputStream to load the PDF from
     * @param handlers the EncryptionHandlers to use to try to decrypt the PDF
     * @param title The name of the Window. May be null.
     * @param file If using a save dialog, the initial value to set the dialog to. May be null.
     * @deprecated this method has been superceded by the {@link PDFImporter} class
     * @since 2.8.3
     */
    public void loadPDF(LivroIDR livroIDR, InputStream in, EncryptionHandler[] handlers, String title, File file) {
        initialize();
        this.livroIDR = livroIDR;
        PDFImporter importer = getFeature(PDFImporter.class);
        if (importer==null) {
            importer = new PDFImporter();
        }
        if (handlers!=null) {
            Set<EncryptionHandler> storeset = importer.getEncryptionHandlers();
            Set<EncryptionHandler> newset = new HashSet<EncryptionHandler>(Arrays.asList(handlers));
            if (!newset.equals(storeset)) {
                importer = new PDFImporter();
                importer.getEncryptionHandlers().clear();
                importer.getEncryptionHandlers().addAll(newset);
            }
        }
        importer.getImporter(this, in, title, file).start(this, UIManager.getString("PDFViewer.Loading"));
    }

    /**
     * Load a PDF into the viewer from a URL. If the URL contains a fragment which
     * is a valid page number, that page will be selected on load.
     * @param url the URL to load
     * @see PDFImporter
     * @since 2.14
     */
    public void loadPDF(URL url) throws IOException {
        initialize();
        PDFImporter importer = getFeature(PDFImporter.class);
        if (importer == null) {
            importer = new PDFImporter();
        }
        Importer.ImporterTask task = importer.getImporter(this, url);
        String frag = url.getRef();
        if (frag != null) {
            try {
                int pagenumber = Integer.parseInt(frag);
                if (pagenumber >= 0) {
                    task.setPageNumber(pagenumber);
                }
            } catch (NumberFormatException e) {}
        }
        task.start(PDFViewer.this, UIManager.getString("PDFViewer.Loading"));
    }

    /**
     * Load a pre-loaded PDF into the viewer. Simply calls
     * <code>loadPDF(new PDFParser(pdf), name)</code>.
     * @param pdf the PDF to load
     * @param name the name of the PDF, to display in the title bar.
     * @since 2.7.1
     */
    public void loadPDF(PDF pdf, String name) {
        if (pdf!=null) {
            loadPDF(new PDFParser(pdf), name);
        }
    }

    /**
     * Load a pre-loaded PDF into the viewer.
     * @param parser the PDFParser referencing the PDF to load
     * @param name the name of the PDF, to display in the title bar.
     * @since 2.11.3
     */
    public void loadPDF(PDFParser parser, String name) {
        loadPDF(parser, name, 0);
    }

    /**
     * Load a pre-loaded PDF into the viewer.
     * @param parser the PDFParser referencing the PDF to load
     * @param name the name of the PDF, to display in the title bar.
     * @param pagenumber the initial page to open the PDF at
     * @since 2.11.3
     */
    public void loadPDF(PDFParser parser, String name, int pagenumber) {
        initialize();
        File file = name == null ? null : new File(name);
        addDocumentPanel(parser, name==null ? parser.getPDF().getInfo("Title") : name, file, pagenumber, true);
    }

    /**
     * Add a new Document Frame containing the specified PDF. If we're using the
     * MultiWindow feature then this is done in a JInternalFrame inserted into
     * the desktop. If not, we just update the activedocument with the new
     * PDF.
     */
    void addDocumentPanel(final PDFParser parser, final String name, final File file, final int pagenumber, final boolean addtomostrecent) {
        initialize();
        Runnable r = new Runnable() {
            public void run() {
                if (hasFeature(MultiWindow.getInstance())) {
                    final DocumentPanel documentpanel = new DocumentPanel();
                    documentpanel.putClientProperty("file", file);
                    for (Iterator<DocumentPanelListener> i = listeners.iterator();i.hasNext();) {
                        documentpanel.addDocumentPanelListener(i.next());
                    }
                    documentpanel.setViewer(PDFViewer.this);
                    documentpanel.raiseDocumentPanelEvent(DocumentPanelEvent.createCreated(documentpanel));
                    for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
                        ViewerFeature feature = i.next();
                        if (feature instanceof SidePanelFactory) {
                            documentpanel.addSidePanelFactory((SidePanelFactory)feature);
                        } else if (feature instanceof AnnotationComponentFactory) {
                            documentpanel.addAnnotationComponentFactory((AnnotationComponentFactory)feature);
                        } else if (feature instanceof ActionHandler) {
                            documentpanel.addActionHandler((ActionHandler)feature);
                        }
                    }
                    documentpanel.setWindowTitle(name);

                    final JInternalFrame frame = new JInternalFrame(name, true, true, true, true);
                    frame.addVetoableChangeListener(new VetoableChangeListener() {
                        public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
                            if (e.getPropertyName().equals(JInternalFrame.IS_CLOSED_PROPERTY) && ((Boolean)e.getNewValue()).booleanValue()) {
                                if (!getUnpromptedDirtyClose() && documentpanel.isDirty()) {
                                    frame.toFront();
                                    if (JOptionPane.showConfirmDialog(PDFViewer.this, UIManager.getString("PDFViewer.ConfirmCloseText"), UIManager.getString("PDFViewer.Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                                        throw new PropertyVetoException("Close cancelled", e);
                                    }
                                }
                                frame.removeVetoableChangeListener(this);
                                documentpanel.setPDF(null);
                                documentpanel.requestFocusInWindow();
                            }
                        }
                    });

                    frame.setContentPane(documentpanel);
                    frame.setFrameIcon(BFO16);
                    frame.setFocusable(false);
                    frame.addInternalFrameListener(new InternalFrameListener() {
                        private void activeWindowChanged() {
                            JInternalFrame activeWindow = desktop.getSelectedFrame();
                            if (activeWindow != null && activeWindow.isIcon()) {
                                activeWindow = null;
                            }
                            DocumentPanel newactivedocument = activeWindow!=null ? (DocumentPanel)activeWindow.getContentPane() : null;
                            if (activedocument != newactivedocument) {
                                if (activedocument != null && activedocument.getPDF() != null) {
                                    activedocument.raiseDocumentPanelEvent(DocumentPanelEvent.createDeactivated(activedocument));
                                }
                                activedocument = newactivedocument;
                                if (activedocument != null && activedocument.getPDF() != null) {
                                    activedocument.raiseDocumentPanelEvent(DocumentPanelEvent.createActivated(activedocument));
                                }
                                updateWindowMenu(false);
                            }
                        }

                        public void internalFrameClosed(InternalFrameEvent e) {
                            frame.removeInternalFrameListener(this);
                            frame.removeAll();
                            frame.dispose();
                            desktop.remove(frame);
                            activeWindowChanged();
                            if (desktop.getComponentCount() == 0) {
                                internalFrameCounter = 0;
                                // Bug 4759312
                                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                            }
                            try {
                                // Stupid Aqua LAF bug as of at least 1.7u45
                                desktop.getDesktopManager().activateFrame(null);
                            } catch (Throwable e2) { }
                            updateWindowMenu(true);
                        }

                        public void internalFrameActivated(InternalFrameEvent e) { activeWindowChanged(); }
                        public void internalFrameDeactivated(InternalFrameEvent e) { activeWindowChanged(); }
                        public void internalFrameDeiconified(InternalFrameEvent e) { activeWindowChanged(); }
                        public void internalFrameIconified(InternalFrameEvent e) { activeWindowChanged(); }
                        public void internalFrameOpened(InternalFrameEvent e) { }
                        public void internalFrameClosing(InternalFrameEvent e) {
                        }
                    });

                    frame.setBounds(internalFrameCounter*30, internalFrameCounter*20, MINIMIZEDWIDTH, MINIMIZEDHEIGHT);
                    internalFrameCounter = (internalFrameCounter+1) % 10;

                    try {   // Minimise other frames
                        JInternalFrame[] fs = desktop.getAllFrames();
                        for (int i=0;i<fs.length;i++) {
                            fs[i].setMaximum(false);
                        }
                        desktop.add(frame);
                        frame.setVisible(true);
                        frame.setMaximum(fs.length==0);
                    } catch (PropertyVetoException e) { }
                    PDF pdf = parser.getPDF();
                    PDFPage initialPage = pdf!=null && pagenumber >=0 && pagenumber < pdf.getNumberOfPages() ? pdf.getPage(pagenumber) : null;
                    documentpanel.setPDF(parser, initialPage);
                    updateWindowMenu(true);
                } else {
                    if (createdEventPending) {
                        for (Iterator<DocumentPanelListener> i = listeners.iterator();i.hasNext();) {
                            activedocument.addDocumentPanelListener(i.next());
                        }
                        activedocument.raiseDocumentPanelEvent(DocumentPanelEvent.createCreated(activedocument));
                        createdEventPending = false;
                    }
                    activedocument.putClientProperty("file", file);
                    PDF pdf = parser.getPDF();
                    PDFPage initialPage = pdf!=null && pagenumber >=0 && pagenumber < pdf.getNumberOfPages() ? pdf.getPage(pagenumber) : null;
                    activedocument.setPDF(parser, initialPage);
                }
                if (file != null && addtomostrecent) {
                    OpenRecent feature = getFeature(OpenRecent.class);
                    if (feature != null) {
                        try {
                            feature.opened(PDFViewer.this, file.getCanonicalFile());
                        } catch (IOException e) {}
                    }
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Update the "Window" menu with the list of currently displayed documents
     * @param rebuild whether a rebuild is required due to a window being opened or closed
     */
    private void updateWindowMenu(boolean rebuild) {
        if (hasFeature(Menus.getInstance()) && hasFeature(ActiveWindowMenu.getInstance())) {
            JMenu menu = getMenu("Window");
            if (menu != null) {
                if (rebuild) {
                    for (int i=0;i<menu.getMenuComponentCount();i++) {
                        if (menu.getMenuComponent(i) instanceof JPopupMenu.Separator) {
                            while (i<menu.getMenuComponentCount()) {
                                menu.remove(i);
                            }
                        }
                    }
                    menu.addSeparator();
                    DocumentPanel[] panels = getDocumentPanels();
                    Collections.reverse(Arrays.asList(panels));
                    for (int i=0;i<panels.length;i++) {
                        final JInternalFrame frame = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, panels[i]);
                        if (frame != null) {
                            String name = panels[i].getWindowTitle();
                            for (int j=0;j<panels.length;j++) {
                                if (i != j && name.equals(panels[j].getWindowTitle())) {
                                    int count = 0;
                                    for (j=0;j<=i;j++) {
                                        if (name.equals(panels[j].getWindowTitle())) {
                                            count++;
                                        }
                                    }
                                    name = name + "("+count+")";
                                }
                            }
                            JMenuItem item = new JCheckBoxMenuItem(name, false);
                            item.putClientProperty("bfo.documentpanel", panels[i]);
                            item.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent event) {
                                    try {
                                        frame.setSelected(true);
                                    } catch (PropertyVetoException e) {
                                        // Ignore
                                    }
                                }
                            });
                            menu.add(item);
                        }
                    }
                }
                DocumentPanel active = getActiveDocumentPanel();
                for (int i=0;i<menu.getMenuComponentCount();i++) {
                    if (menu.getMenuComponent(i) instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem item = (JCheckBoxMenuItem)menu.getMenuComponent(i);
                        item.setSelected(item.getClientProperty("bfo.documentpanel") == active);
                    }
                }
            }
        }
    }

    /**
     * Return the active {@link DocumentPanel}
     */
    public DocumentPanel getActiveDocumentPanel() {
        initialize();
        return activedocument;
    }

    /**
     * Return all the {@link DocumentPanel}s in the viewer
     * @since 2.8.5
     */
    public DocumentPanel[] getDocumentPanels() {
        if (hasFeature(MultiWindow.getInstance())) {
            if (desktop == null) {
                return null;
            } else {
                JInternalFrame[] fs = desktop.getAllFrames();
                DocumentPanel[] panels = new DocumentPanel[fs.length];
                for (int i=0;i<fs.length;i++) {
                    try {
                        panels[i] = (DocumentPanel)(fs[i].getContentPane());
                    } catch (ClassCastException e) {
                        DocumentPanel[] t = new DocumentPanel[panels.length-1];
                        System.arraycopy(panels, 0, t, 0, i);
                        panels = t;
                    }
                }
                return panels;
            }
        } else {
            return new DocumentPanel[] { activedocument };
        }
    }

    /**
     * Close the PDFViewer. Should be called before this PDFViewer
     * is permanently removed from the Swing object hierarchy.
     * @since 2.11.18
     */
    public void close() {
        if (hasFeature(MultiWindow.getInstance())) {
            JInternalFrame[] fs = desktop.getAllFrames();
            for (int i=0;i<fs.length;i++) {
                fs[i].doDefaultCloseAction();
            }
        } else if (activedocument != null) {
            activedocument.setPDF((PDFParser)null, null);
        }
        for (Iterator<ViewerFeature> i = features.iterator();i.hasNext();) {
            ViewerFeature feature = i.next();
            feature.teardown();
        }
        if (menubar != null) {
            menubar.removeAll();
        }
    }

    /**
     * Close the specified {@link DocumentPanel}
     * @param panel the panel to close - usually the return value of {@link #getActiveDocumentPanel}
     * @since 2.10.2
     */
    public void closeDocumentPanel(final DocumentPanel panel) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                if (hasFeature(MultiWindow.getInstance())) {
                    if (panel != null) {
                        JInternalFrame window = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, panel);
                        if (window != null) {
                            if (getPropertyManager().getProperty("debug.Event") != null) {
                                System.err.println("[PDF] Closing DocumentPanel#"+panel.panelindex);
                            }
                            window.doDefaultCloseAction();
                        }
                    }
                } else if (panel == activedocument) {
                    if (!getUnpromptedDirtyClose() && panel.isDirty() && JOptionPane.showConfirmDialog(PDFViewer.this, UIManager.getString("PDFViewer.ConfirmCloseText"), UIManager.getString("PDFViewer.Confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return null;
                    }

                    activedocument.setPDF(null);
                }
                return null;
            }
        });
    }

    /**
     * Add a {@link DocumentPanelListener} to any {@link DocumentPanel} objects
     * created by this PDFViewer. The listener will be attached to any existing
     * {@link DocumentPanel} objects, and added to any new ones that are created later.
     */
    public void addDocumentPanelListener(DocumentPanelListener listener) {
        initialize();
        listeners.add(listener);
        if (desktop != null) {
            JInternalFrame[] fs = desktop.getAllFrames();
            for (int i=0;i<fs.length;i++) {
                try {
                    DocumentPanel panel = (DocumentPanel)(fs[i].getContentPane());
                    panel.addDocumentPanelListener(listener);
                } catch (ClassCastException e) {}
            }
        } else if (activedocument != null) {
            activedocument.addDocumentPanelListener(listener);
        }
    }

    /**
     * Remove a {@link DocumentPanelListener} previously added to the class
     * with the {@link #addDocumentPanelListener} method.
     */
    public void removeDocumentPanelListener(DocumentPanelListener listener) {
        initialize();
        listeners.remove(listener);
        if (desktop!=null) {
            JInternalFrame[] fs = desktop.getAllFrames();
            for (int i=0;i<fs.length;i++) {
                try {
                    DocumentPanel panel = (DocumentPanel)(fs[i].getContentPane());
                    panel.removeDocumentPanelListener(listener);
                } catch (ClassCastException e) {}
            }
        } else if (activedocument!=null) {
            activedocument.removeDocumentPanelListener(listener);
        }
    }

    /**
     * Return a list of {@link DocumentPanelListener} objects added to this class
     * with the {@link #addDocumentPanelListener} method
     * @since 2.13.1
     */
    public DocumentPanelListener[] getDocumentPanelListeners() {
        initialize();
        return (DocumentPanelListener[])listeners.toArray(new DocumentPanelListener[0]);
    }

    //----------------------------------------------------------------------------------

    private void cascadeFrames() {
        JInternalFrame[] frames = desktop.getAllFrames();
        if (frames.length != 0) {
            Rectangle dBounds = desktop.getBounds();
            int margin = 0;
            Insets ins = null;
            Dimension d, cd;
            for (int i=0; i<frames.length; i++) {
                d = frames[i].getSize();
                cd = frames[i].getContentPane().getSize();
                margin += Math.max(d.width - cd.width, d.height - cd.height);
            }

            int width = dBounds.width - margin;
            int height = dBounds.height - margin;
            int offset = 0;
            for (int i=0;i<frames.length;i++) {
                try {
                    frames[i].setMaximum(false);
                } catch (PropertyVetoException e) {}
                frames[i].setBounds(dBounds.x + offset, dBounds.y + offset, width, height);
                d = frames[i].getSize();
                cd = frames[i].getContentPane().getSize();
                offset += Math.max(d.width - cd.width, d.height - cd.height);
            }
        }
    }

    //----------------------------------------------------------------------------------

    /**
     * Create a new PDFViewer object in a frame of it's own. Top level routine
     * to be called by main()
     * @since 2.7..1
     */
    public static PDFViewer newPDFViewer() {
        ArrayList<ViewerFeature> f = new ArrayList<ViewerFeature>(ViewerFeature.getAllEnabledFeatures());
//        f.remove(MultiWindow.getInstance());
        return newPDFViewer(f);
    }
    
    
    /***
     * @author yesus
     * @return o frame principal adicionado ao chamar a API.
     */
    public Frame getParentFrame(){
    	return (Frame)getParent().getParent().getParent().getParent();
    }
    
    /***
     * @author yesus
     * @return
     */
    public String getTitle(){
    	 Frame parentViewer = getParentFrame();
    	 return parentViewer.getTitle();
    }

    /**
     * Create a new PDFViewer object in a frame of its own with the specified features.
     * @param features a Collection of {@link ViewerFeature} objects that are to be supported
     * @see ViewerFeature
     * @since 2.7.9
     */
    public static PDFViewer newPDFViewer(Collection<ViewerFeature> features) {
        final JFrame frame = new JFrame("BFO");
        frame.setIconImage(BFO16.getImage());
        PDFViewer viewer = new PDFViewer(features);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setContentPane(viewer);
        boolean located = false, sized = false;
        final Preferences preferences = viewer.getPreferences();
        if (preferences != null) {
            int x = preferences.getInt("window.x", -1);
            int y = preferences.getInt("window.y", -1);
            int width = preferences.getInt("window.width", -1);
            int height = preferences.getInt("window.height", -1);
            if (x >= 0 && y >= 0) {
                frame.setLocation(x, y);
                located = true;
            }
            if (width > 0 && height > 0) {
                frame.setSize(width, height);
                sized = true;
            }
            frame.addComponentListener(new ComponentAdapter() {
                public void componentMoved(ComponentEvent e) {
                    preferences.putInt("window.x", frame.getX());
                    preferences.putInt("window.y", frame.getY());
                }
                public void componentResized(ComponentEvent e) {
                    preferences.putInt("window.width", frame.getWidth());
                    preferences.putInt("window.height", frame.getHeight());
                }
            });
        }
        if (!sized) {
            frame.pack();
        }
        if (!located) {
            frame.setLocationByPlatform(true); // Note: works in OSX Java 6 but not 7, fix defered to Java 9! But it's not a regression, apparently. https://bugs.openjdk.java.net/browse/JDK-8025130
        }
        frame.applyComponentOrientation(ComponentOrientation.getOrientation(frame.getLocale()));
        frame.setVisible(true);
        return viewer;
    }

    public LivroIDR getLivroIDR() {
		return livroIDR;
	}

	public void setLivroIDR(LivroIDR livroIDR) {
		this.livroIDR = livroIDR;
	}

	/**
     * The main() method can be invoked to run this class from the command line.
     * A single argument specifying the name of the file to open is optional
     */
    public static void main(final String args[]) {
        File file = null;
        URL url = null;
        if (args.length > 0) {
            try {
                if (args.length != 1 || args[0].equals("--help") || args[0].equals("-h")) {
                    throw new Exception();
                } else {
                    if (args[0].startsWith("http://") || args[0].startsWith("https://")) {
                        url = new URL(args[0]);
                    } else {
                        file = new File(args[0]);
                        if (!file.isFile() || !file.canRead()) {
                            System.err.println("PDFViewer: Unable to read \""+args[0]+"\"");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Usage: java org.faceless.pdf2.viewer3.PDFViewer [<file | url | - >]");
                return;
            }
        }

        try {
            if (System.getProperty("swing.defaultlaf") == null) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) { }


        final File ffile = file;
        final URL furl = url;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PDFViewer viewer = newPDFViewer();
                try {
                    Authenticator.setDefault(new PromptingAuthenticator(viewer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    KeyStoreTrustManager.install(viewer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ffile != null) {
                    viewer.loadPDF(ffile);
                } else if (furl != null) {
                    try {
                        viewer.loadPDF(furl);
                    } catch (IOException e) {
                        Util.displayThrowable(e, viewer);
                    }
                }
            }
        });
    }

    /**
     * Set the name of the current user (for Annotations). The
     * default is null.
     * Can also be set by the <code>currentUser</code> initialization parameter
     * @since 2.11.18
     */
    public void setCurrentUser(String user) {
        this.user = user;
    }

    /**
     * Get the name of the current user, as set by {@link #setCurrentUser}
     * @since 2.11.18
     */
    public String getCurrentUser() {
        return this.user;
    }

    /**
     * Set whether to allow a window containing a modified PDF to be closed
     * without prompting.
     * Can also be set by the <code>unpromptedDirtyClose</code> initialization parameter
     * @since 2.11.19
     */
    public void setUnpromptedDirtyClose(boolean ignore) {
        this.unprompteddirtyclose = ignore;
    }

    /**
     * Return the value set by {@link #setUnpromptedDirtyClose}
     * @since 2.11.19
     */
    public boolean getUnpromptedDirtyClose() {
        return unprompteddirtyclose;
    }

    static {
        PDF.useAWTEventModel(true);
    }

}
