// $Id: DocumentPanel.java 20861 2015-02-11 10:58:38Z mike $

package org.faceless.pdf2.viewer3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.StreamPrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MultipleDocumentHandling;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.faceless.pdf2.EncryptionHandler;
import org.faceless.pdf2.FormElement;
import org.faceless.pdf2.FormSignature;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFAction;
import org.faceless.pdf2.PDFAnnotation;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.PropertyManager;
import org.faceless.pdf2.viewer3.feature.ContinuousPageView;
import org.faceless.pdf2.viewer3.feature.DualPageView;
import org.faceless.pdf2.viewer3.feature.SinglePageView;

import br.com.ibracon.idr.form.bo.IndiceBO;
import br.com.ibracon.idr.form.bo.NotaBO;
import br.com.ibracon.idr.form.modal.JanelaNota;
import br.com.ibracon.idr.form.model.Nota;
import br.com.ibracon.idr.form.model.indice.Item;
import br.com.ibracon.idr.form.util.IdrUtil;
import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * <p>
 * A <code>DocumentPanel</code> is the basic component that displays a PDF, and may be
 * instantiated on it's own or as part of a {@link PDFViewer}. It contains a
 * {@link DocumentViewport} and optionally one or more {@link SidePanel} objects on the
 * left, and may process {@link PDFAction}s on the PDF.
 * See the <a href="doc-files/tutorial.html">viewer tutorial</a> for more detail on how to use this class and the "viewer" package.
 * </p>
 *
 * <a name="initParams"></a>
 * <div class="initparams">
 * The following <a href="doc-files/initparams.html">initialization parameters</a> may be specified
 * <table summary="">
 * <tr><th>defaultViewport</th><td>The class name of the default viewport to use if not specified in the PDF. May be <code>SinglePageDocumentViewport</code>, <code>MultiPageDocumentVieport</code> or a fully-qualified class name of another {@link DocumentViewport}</td></tr>
 * <tr><th>defaultPageMode</th><td>The default "page mode" of the PDF if not set. This may take one of the values for the "pagemode" {@link PDF#setOption PDF option}, and would typically be "UseThumbs" or "UseOutlines".</td></tr>
 * <tr><th>defaultZoom</th><td>The default zoom level of the PDF, if not set by a PDF open action. This may be the value "fit", "fitwidth", "fitheight" or a number between 12.5 and 6400 to set the zoom level.</td></tr>
 * <tr><th>useNamedSidePanels</th><td><code>true</code> or <code>false</code> (the default) - whether to show names on the side panel tabs rather than icons.</td></tr>
 * <tr><th>sidePanelSize</th><td>The default (and minimum) width of the side panels displayed in this DocumentPanel. The default is 120</td></tr>
 * <tr><th>mouseWheelUnit</th><td>The number of pixels to adjust the viewport's scrollbar by when using the mouse wheel. The default is 16.</td></tr>
 * <tr><th>smoothScrollTime</th><td>When smoothly scrolling a viewport's scrollbars, the number of ms to animate the scroll over. The default is 500, set to zero to disable.</td></tr>
 * <tr><th>smoothScrollDistance</th><td>When smoothly scrolling a viewport's scrollbars, the maximum number of pixels to try to animate. The default is 500, set to zero to disable.</td></tr>
 * <tr><th>earlyClose</th><td>When closing a DocumentPanel or changing the PDF it contains, the old PDF object remains open and will naturally have its {@link PDF#close} method called during garbage collection. This can lead to problems on Windows platforms; As the PDF may retain a reference to the file it was read from, this prevents the file being deleted until <code>close</code> is called. The <code>earlyClose</code> parameter can be set to close the PDF file immediately the PDF is removed from the DocumentPanel or the panel closed; this will free any resources held by the PDF, and so invalidate any reference to those resources (which may be held elsewhere - for example, if the PDF had its pages moved to another document). So use with caution - by default this value is not set, but set to any non-null value to enable.</td></tr>
 * <tr><th>noDirtyDocuments</th><td>Set this value to non-null to disable the {@link #setDirty dirty} flag on documents. If disabled, no prompt will appear when trying to close a document that has been modified.</td></tr>
 * <tr><th>noDirtyInTitle</th><td>Set this value to non-null to disable the "*" in the window title when the PDF is flagged as dirty.</td></tr>
 * <tr><th>noProgressInTitle</th><td>Set this value to non-null to disable the load-progress indicator in the window title when the PDF is linearized and only partially loaded.</td></tr>
 * <tr><th>respectSignatureCertification</th><td>If true, any restrictions found on a {@link FormSignature#getCertificationType certified} signature in the PDF will be honoured - for example, if the PDF being displayed has {@link FormSignature#CERTIFICATION_NOCHANGES nochanges} set then no changes will be allowed to the PDF through the viewer</td></tr>
 * <tr><th>viewportBorderColor</th><td>Can be set to the Color to draw the border around the pages in the viewport, specified as a hex value. The default value is "666666", which is a dark gray. If the specified value has 8 digits, the first two hex digits are used as the alpha value. A value of "none" or "transparent" will not draw the border.</td></tr>
 * <tr><th>viewportShadowColor</th><td>Can be set to the Color to draw the shadow below the pages in the viewport, specified as a hex value. The default value is "80666666", which is a translucent dark gray. If the specified value has 8 digits, the first two hex digits are used as the alpha value. A value of "none" or "transparent" will not draw the shadow.</td></tr>
 * <tr><th>viewportMargin</th><td>If set, this is the margin to place around the outside of all the pages displayed in the viewport, in pixels. The default value is 4.</td></tr>
 * <tr><th>viewportGap</th><td>If set, this is the gap to place between multiple pages displayed in the viewport, in pixels (if applicable to the type of viewport in use). The default value is 10.</td></tr>
 * </table>
 * </div>
 *
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class DocumentPanel extends JPanel {

    private PDF pdf;                            // The PDF
    private JSManager jsmanager;
    private PDFParser parser;                   // The parser for the PDF
    private PDFViewer viewer;                   // The parent viewer - may be null
    private DocumentViewport viewport;          // The viewport
    private JTabbedPane tabbedpane;             // If not null, contains the SidePanels
    private JSplitPane splitpane;               // If not null, contains tabbedpane and viewport
    private int initialsize, thresholdsize;   // The sizing values for splitpane
    private final Collection<ActionHandler> actionhandlers;    // A collection of ActionHandler
    private final Collection<AnnotationComponentFactory> annotfactories;    // A collection of AnnotationComponentFactory
    private final Collection<SidePanelFactory> panelfactories;    // A collection of ViewerFeaurte.SidePanelFactory
    private final Collection<DocumentPanelListener> listeners;         // A collection of DocumentPanelListeners
    private boolean initialpageset;             // False until a valid page has been set
    private boolean loadedfired;                // False until a valid page has been set
    private boolean splitpaneopen;              // It the splitpane open?
    private boolean dirty;                      // It the splitpane open?
    private SidePanel selectedsidepanel;        // The current sidepanel
    private int lastpagenumber;                 // Used only when the current page has been deleted
    private transient final DirtyListener dirtylistener;
    private final Collection<UndoableEditListener> undolisteners;
    private final Collection<String> permissiondenied;
    private int signaturePermissionDenied;
    private LinearizedSupport linearizedsupport;
    private String windowtitle;
    private JPanel notas = new JPanel();
    /** The scroll indice. */
	private JScrollPane scrollIndice;
    
    final int panelindex;               // For debugging
    private static int globalpanelindex;        // For debugging

    /**
     * Create a new DocumentPanel
     */
    public DocumentPanel() {
        super(new BorderLayout());
        Util.initialize();
        this.actionhandlers = new LinkedHashSet<ActionHandler>();
        this.annotfactories = new LinkedHashSet<AnnotationComponentFactory>();
        this.panelfactories = new LinkedHashSet<SidePanelFactory>();
        this.listeners = new LinkedHashSet<DocumentPanelListener>();
        this.undolisteners = new LinkedHashSet<UndoableEditListener>();
        this.permissiondenied = new LinkedHashSet<String>();
        setOpaque(true);
        setBackground(Color.gray);
        this.panelindex = globalpanelindex++;
        if (getProperty("debug.Event") != null) {
            System.err.println("[PDF] Created DocumentPanel#"+panelindex);
        }

        this.dirtylistener = new DirtyListener(this);
        PropertyChangeListener l = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                updateTitle();
            }
        };
        addPropertyChangeListener("dirty", l);
        addPropertyChangeListener("loadProgress", l);
    }

    /**
     * Create pageup/pagedown actions. Done this way so we can experiment with
     * different binding locations
     * @param comp the component on which to create the actions and edit the InputMap
     * @param level the level - WHEN_IN_FOCUSED_WINDOW or WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     * @param panel the DocumentPanel
     */
    static void createInputMapActions(JComponent comp, int level, final DocumentPanel docpanel) {
        Action actionScrollUp = new AbstractAction("pageUp") {
            public boolean isEnabled() {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                return page != null && vp.getPreviousSelectablePageIndex(page) >= 0;
            }
            public void actionPerformed(ActionEvent e)  {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                if (page != null) {
                    int ix = vp.getPreviousSelectablePageIndex(page);
                    if (ix >= 0) {
                        docpanel.setPageNumber(ix);
                    }
                }
            }
        };

        Action actionScrollDown = new AbstractAction("pageDown") {
            public boolean isEnabled() {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                return page != null && vp.getNextSelectablePageIndex(page) >= 0;
            }
            public void actionPerformed(ActionEvent e)  {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                if (page != null) {
                    int ix = vp.getNextSelectablePageIndex(page);
                    if (ix >= 0) {
                        docpanel.setPageNumber(ix);
                    }
                }
            }
        };

        Action actionScrollHome = new AbstractAction("pageFirst") {
            public void actionPerformed(ActionEvent e)  {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                if (page != null && page.getPageNumber() != 1) {
                    docpanel.setPageNumber(0);
                }
            }
        };

        Action actionScrollEnd = new AbstractAction("pageLast") {
            public void actionPerformed(ActionEvent e)  {
                DocumentViewport vp = docpanel.getViewport();
                PDFPage page = vp.getRenderingPage();
                if (page != null && page.getPageNumber() != docpanel.pdf.getNumberOfPages()) {
                    docpanel.setPageNumber(docpanel.pdf.getNumberOfPages() - 1);
                }
            }
        };

        Action actionDebugBindings = new AbstractAction("debugBindings") {
            public void actionPerformed(ActionEvent e)  {
                Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                while (c!=null) {
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent)c;
                        InputMap map1 = jc.getInputMap(jc.WHEN_FOCUSED);
                        InputMap map2 = jc.getInputMap(jc.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                        InputMap map3 = jc.getInputMap(jc.WHEN_IN_FOCUSED_WINDOW);
                        String a1 = map1.allKeys()==null ? "[]" : Arrays.asList(map1.allKeys()).toString();
                        String a2 = map2.allKeys()==null ? "[]" : Arrays.asList(map2.allKeys()).toString();
                        String a3 = map3.allKeys()==null ? "[]" : Arrays.asList(map3.allKeys()).toString();
                        System.out.println("InputMap: c="+jc.getClass().getName()+" f="+a1+" a="+a2+" w="+a3);
                    } else {
                        System.out.println("InputMap: c="+c.getClass().getName());
                    }
                    c = c.getParent();
                }
            }
        };

        InputMap inputmap = comp.getInputMap(level);
        ActionMap actionmap = comp.getActionMap();
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "pageUp");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "pageDown");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "pageFirst");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "pageLast");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), "pageUp");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), "pageDown");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "pageFirst");
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "pageLast");
        actionmap.put("pageUp", actionScrollUp);
        actionmap.put("pageDown", actionScrollDown);
        actionmap.put("pageFirst", actionScrollHome);
        actionmap.put("pageLast", actionScrollEnd);

        // For debugging - can remove
        inputmap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputmap.put(KeyStroke.getKeyStroke("ctrl alt shift K"), "debugBindings");
        actionmap.put("debugBindings", actionDebugBindings);
    }

    void setPreference(String key, String value) {
        if (viewer != null) {
            viewer.getPreferences().put(key, value);
        }
    }

    String getPreference(String key) {
        return viewer == null ? null : viewer.getPreferences().get(key, null);
    }

    private DocumentViewport createDefaultViewport() {
        DocumentViewport viewport = null;
        // If we have a viewer, look through it's features to see which Viewport
        // type comes first. Use that.
        if (getPDF() != null) {   // If the preferred viewport if specified in the PDF, use that
            String pagelayout = (String)getPDF().getOption("pagelayout");
            if ("OneColumn".equals(pagelayout)) {
                viewport = new MultiPageDocumentViewport();
            } else if ("SinglePage".equals(pagelayout)) {
                viewport = new SinglePageDocumentViewport();
            } else if ("DualPage".equals(pagelayout)) {
                viewport = new DualPageDocumentViewport();
            }
        }
        if (viewport==null) {
            String viewportname = getProperty("defaultViewport");
            if (viewportname == null && viewer != null) {
                viewportname = getPreference("defaultViewport");
            }
            if (viewportname == null && viewer != null) {
                ViewerFeature[] features = viewer.getFeatures();
                for (int i=0;viewportname==null && i<features.length;i++) {
                    if (features[i] instanceof SinglePageView) {
                        viewportname = "SinglePageDocumentViewport";
                    } else if (features[i] instanceof ContinuousPageView) {
                        viewportname = "MultiPageDocumentViewport";
                    } else if (features[i] instanceof DualPageView) {
                        viewportname = "DualPageDocumentViewport";
                    }
                }
            }
            if (viewportname != null) {
                if (viewportname.equals("MultiPageDocumentViewport")) {
                    viewport = new MultiPageDocumentViewport();
                } else if (viewportname.equals("SinglePageDocumentViewport")) {
                    viewport = new SinglePageDocumentViewport();
                } else if (viewportname.equals("DualPageDocumentViewport")) {
                    viewport = new DualPageDocumentViewport();
                } else {
                    try {
                        viewport = (DocumentViewport)Class.forName(viewportname, true, Thread.currentThread().getContextClassLoader()).newInstance();
                    } catch (Throwable e) { }
                }
            }
            if (viewport==null) {
                viewport = new MultiPageDocumentViewport();
            }
        }
        if (viewer != null) {
            Preferences preferences = viewer.getPreferences();
            if (preferences != null) {
                int zoomMode = preferences.getInt("zoomMode", DocumentViewport.ZOOM_NONE);
                viewport.setZoomMode(zoomMode);
            }
        }
        return viewport;
    }

    //----------------------------------------------------------------------------------
    // Viewer and Viewport

    /**
     * Set the {@link DocumentViewport} used by this DocumentPanel.
     * @param viewport the Viewport
     */
    public void setViewport(DocumentViewport viewport) {
        if (viewport == null) {
            throw new NullPointerException("Viewport is null");
        }
        if (viewer != null) {
            String viewportname = viewport.getClass().getName();
            if ("org.faceless.pdf2.viewer3.SinglePageDocumentViewport".equals(viewportname)) {
                viewportname = "SinglePageDocumentViewport";
            } else if ("org.faceless.pdf2.viewer3.MultiPageDocumentViewport".equals(viewportname)) {
                viewportname = "MultiPageDocumentViewport";
            }
            setPreference("defaultViewport", viewportname);
        }

        DocumentViewport oldviewport = this.viewport;
        PDFPage currentpage = null;
        double zoom = 0;
        if (oldviewport!=null) {
            currentpage = getPage();
            zoom = getZoom();
            oldviewport.setDocumentPanel(null);
            remove(oldviewport);
        }
        if (viewport.getDocumentPanel()!=null) {
            throw new IllegalArgumentException("Viewport associated with another DocumentPanel");
        }
        this.viewport = viewport;
        viewport.setDocumentPanel(this);
        createInputMapActions(viewport, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this);

        raiseDocumentPanelEvent(DocumentPanelEvent.createViewportChanged(DocumentPanel.this));
        // Add listeners to handle page open/close actions and to trigger "document redrawn"
        viewport.addPagePanelListener(new PagePanelListener() {
            private float lastdpi;
            private SoftReference<PDFPage> lastpageref;
            public void pageUpdated(PagePanelEvent event) {
                if (event.getType() == "redrawn") {
                    PDFPage lastpage = lastpageref == null ? null : lastpageref.get();
                    if (event.getPage()!=lastpage || event.getPagePanel().getDPI() != lastdpi) {
                        lastpageref = new SoftReference<PDFPage>(event.getPage());
                        lastdpi = event.getPagePanel().getDPI();
                        raiseDocumentPanelEvent(DocumentPanelEvent.createRedrawn(DocumentPanel.this));
                    }
                }
            }
        });
        viewport.applyComponentOrientation(ComponentOrientation.getOrientation(getLocale()));
        viewport.requestFocusInWindow();
        if (getPDF() != null) {
            if (viewport instanceof NullDocumentViewport) {
                removeAll();
                if (tabbedpane != null) {
                    add(tabbedpane, BorderLayout.CENTER);
                }
            } else if (splitpane != null && splitpane.getParent() == this) {
                // Splitpane exists, just change viewport
                int div = splitpane.getDividerLocation();
                splitpane.setBottomComponent(viewport);
                splitpane.setDividerLocation(div);
            } else if (tabbedpane != null && tabbedpane.getParent() == this) {
                // Only here if we're changing from a NullDocumentViewport back to normal
                remove(tabbedpane);
                splitpane.setTopComponent(tabbedpane);
                splitpane.setBottomComponent(viewport);
                add(splitpane, BorderLayout.CENTER);
            } else {
                add(viewport, BorderLayout.CENTER);
            }
            if (currentpage != null) {
                viewport.setPage(currentpage, 0, 0, zoom);
            }
        }
        revalidate();
        repaint();
        viewport.setFocusCycleRoot(true);
    }

    private String getProperty(String property) {
        PropertyManager manager = getViewer()==null ? PDF.getPropertyManager() : getViewer().getPropertyManager();
        return manager==null ? null : manager.getProperty(property);
    }

    /**
     * Return the {@link DocumentViewport} contained by this DocumentPanel
     */
    public DocumentViewport getViewport() {
        if (viewport==null) {
            setViewport(createDefaultViewport());
        }
        return viewport;
    }

    /**
     * Return the JSManager object for this DocumentPanel.
     * @since 2.9
     */
    public JSManager getJSManager() {
        if (jsmanager==null) {
            jsmanager = new JSManager(this);
        }
        return jsmanager;
    }

    /**
     * Set the JSManager object for this DocumentPanel.
     * This method should only be called if multiple DocumentPanel
     * object are used in the same non-PDFViewer container.
     * @since 2.9
     */
    public void setJSManager(JSManager jsmanager) {
        this.jsmanager = jsmanager;
    }

    /**
     * Return the {@link PDFViewer} that contains this DocumentPanel.
     * Note a DocumentPanel does <i>not</i> have to be contained inside
     * a PDFViewer, in which case this method will return <code>null</code>.
     */
    public PDFViewer getViewer() {
        return viewer;
    }

    void setViewer(PDFViewer viewer) {
        this.viewer = viewer;
        setLocale(viewer.getLocale());
        setJSManager(viewer.getJSManager());
    }

    //--------------------------------------------------------------------------------
    // Resources and settings

    /**
     * Control the size of the leftmost pane. The two values specify the threshold
     * below which the pane is considered to be closed, and the default size of the
     * pane when it's opened.
     *
     * @param threshold the minimum size, below which the panel is assumed to be closed
     * @param preferred the default size of the leftmost pane when opened
     */
    public void setSidePanelSize(int threshold, int preferred) {
        this.thresholdsize = Math.max(thresholdsize, splitpane.getMinimumDividerLocation());
        this.initialsize = preferred;
        splitpane.setLastDividerLocation(initialsize);
    }

    /**
     * Add a {@link SidePanelFactory} to this
     * <code>DocumentPanel</code>. When a PDF is set, the panels that are
     * appropriate for that PDF will be created from this list of factories.
     * @param panelfactory the factory
     */
    public void addSidePanelFactory(SidePanelFactory panelfactory) {
        if (panelfactory!=null) panelfactories.add(panelfactory);
    }

    /**
     * Add a {@link AnnotationComponentFactory} to this
     * <code>DocumentPanel</code>. Any PDF's displayed by this panel will have annotations
     * created by these factories.
     * @param annotationfactory the factory
     */
    public void addAnnotationComponentFactory(AnnotationComponentFactory annotationfactory) {
        if (annotationfactory!=null) annotfactories.add(annotationfactory);
    }

    /**
     * Return the set of AnnotationFactories - called by PagePanel
     */
    Collection<AnnotationComponentFactory> getAnnotationFactories() {
        return Collections.unmodifiableCollection(annotfactories);
    }

    /**
     * Add a {@link ActionHandler} to this <code>DocumentPanel</code>.
     * Any actions passed to {@link #runAction} will by handled by this list of handlers.
     * @param actionhandler the handler
     */
    public void addActionHandler(ActionHandler actionhandler) {
        if (actionhandler!=null) actionhandlers.add(actionhandler);
    }

    /**
     * Run the specified action on the PDF. Actions are handled by
     * {@link ActionHandler}s, which should be registered
     * with this class via the {@link #addActionHandler addActionHandler()} method.
     * @param action the PDFAction to run.
     * @return true if the action was recognised and run successfully, false otherwise.
     */
    public boolean runAction(PDFAction action) {
        boolean success = false;
        while (action != null) {
            for (Iterator<ActionHandler> i = actionhandlers.iterator();i.hasNext();) {
                ActionHandler handler = i.next();
                if (handler.matches(this, action)) {
                    handler.run(this, action);
                    success = true;
                    break;
                }
            }
            action = action.getNext();
        }
        return success;
    }

    /**
     * Add a {@link DocumentPanelListener} to this DocumentPanel
     * @param listener the listener
     */
    public void addDocumentPanelListener(DocumentPanelListener listener) {
        if (listener != null) {
            synchronized(listeners) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Remove a {@link DocumentPanelListener} from this DocumentPanel
     * @param listener the listener
     */
    public void removeDocumentPanelListener(DocumentPanelListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Return a list of all the {@link DocumentPanelListener} objects registered on
     * this DocumentPanel, or an empty array if there are none
     * @return the list of listeners
     * @since 2.13.1
     */
    public DocumentPanelListener[] getDocumentPanelListeners() {
        synchronized(listeners) {
             return (DocumentPanelListener[])listeners.toArray(new DocumentPanelListener[0]);
        }
    }

    /**
     * Raise a {@link DocumentPanelEvent} on the DocumentPanel. In general
     * this shouldn't be called unless you're extending one of the code
     * classes, i.e. by writing your own {@link DocumentViewport}.
     * @since 2.11.25
     */
    public void raiseDocumentPanelEvent(DocumentPanelEvent event) {
        if (getProperty("debug.Event")!=null) {
            if (SwingUtilities.isEventDispatchThread()) {
                System.err.println("[PDF] Raise DocumentPanel#"+panelindex+" "+event);
            } else {
                System.err.println("[PDF] Raise DocumentPanel#"+panelindex+" "+event+" (not in event thread)");
            }
        }
        if (event.getType() == "pageChanged") {
            if (!loadedfired) {
                loadedfired = true;
                raiseDocumentPanelEvent(DocumentPanelEvent.createLoaded(this));
                if (viewer != null) {
                    raiseDocumentPanelEvent(DocumentPanelEvent.createActivated(this));
                }
                postLoaded();
                File file = (File)getClientProperty("file");
                getJSManager().runEventDocOpen(this, file==null ? null : file.getName());
            }
            PDFPage oldPage = event.getPreviousPage();
            if (oldPage != null) {
                getJSManager().runEventPageClose(this, oldPage);
            }
            PDFPage newPage = getPage();
            if (newPage != null) {
                getJSManager().runEventPageOpen(this, newPage);
            }
        }
        DocumentPanelListener[] l = new DocumentPanelListener[0];
        synchronized(listeners) {
            l = (DocumentPanelListener[])listeners.toArray(l);
        }
        for (int i=0;i<l.length;i++) {
           l[i].documentUpdated(event);
        }
    }

    //-----------------------------------------------------------------------------
    // Panels

    /**
     * Return a read-only collection containing the {@link SidePanel} objects in use by this
     * <code>DocumentPanel</code>.
     * @since 2.10.3 (prior to this release a Map was returned instead)
     */
    public Collection<SidePanel> getSidePanels() {
        if (tabbedpane == null) {
            return Collections.<SidePanel>emptySet();
        }
        List<SidePanel> l = new ArrayList<SidePanel>(tabbedpane.getTabCount());
        for (int i=0;i<tabbedpane.getTabCount();i++) {
        	 //** YESUS -> Acrescentando proteção de instanceof
            if(tabbedpane.getComponentAt(i) instanceof javax.swing.JPanel ){
            	continue;
            }
            l.add((SidePanel)tabbedpane.getComponentAt(i));
        }
        return Collections.unmodifiableCollection(l);
    }

    /**
     * Remove the specified SidePanel from the DocumentPanel.
     * @since 2.10.3
     */
    public void removeSidePanel(SidePanel panel) {
        if (tabbedpane == null || panel == null) {
            return;
        }
        int tab = tabbedpane.indexOfComponent((Component)panel);
        if (tab >= 0) {
            boolean selected = getSelectedSidePanel()==panel;
            if (selected) {
                panel.panelHidden();
            }
            tabbedpane.remove((Component)panel);
            panel.setDocumentPanel(null);
            if (tabbedpane.getTabCount() == 0) {
                selectedsidepanel = null;
                remove(splitpane);
                splitpane.setDividerLocation(splitpane.getMinimumDividerLocation());
                add(viewport, BorderLayout.CENTER);
                tabbedpane = null;
                revalidate();
                repaint();
            } else if (selected) {
                tabbedpane.setSelectedIndex(tab==0 ? 0 : tab-1);
            }
        }
    }

    /**
     * Add the specified sidepanel to the DocumentPanel
     * @since 2.10.3
     */
    public void addSidePanel(final SidePanel panel) {
        final Component comp = (Component)panel;
        initTabbedPane();
        panel.setDocumentPanel(DocumentPanel.this);
        if (comp.isVisible()) {
            if (tabbedpane.indexOfComponent(comp) < 0) {
                Icon icon = null;
                try {
                    icon = panel.getIcon();
                } catch (Throwable e) {}    // Just in case old interface was used.
                String name = UIManager.getString("PDFViewer."+panel.getName());
                comp.setBackground(Color.WHITE);
                if (icon == null || "true".equals(getProperty("useNamedSidePanels"))) {
                    tabbedpane.addTab(name, comp);
                } else {
                    tabbedpane.addTab(null, icon, comp, name);
                }
                comp.applyComponentOrientation(ComponentOrientation.getOrientation(getLocale()));
                if (tabbedpane.getTabCount() == 1) {
                    removeAll();
                    if (viewport instanceof NullDocumentViewport) {
                        add(tabbedpane, BorderLayout.CENTER);
                    } else if (splitpane != null) {
                        add(splitpane, BorderLayout.CENTER);
                        splitpane.setBottomComponent(viewport);
                    }
                    selectedsidepanel = panel;
                    revalidate();
                    repaint();
                }
            }
        }
    }

	private void initTabbedPane() {
		if (tabbedpane == null) {
            tabbedpane = new JTabbedPane(JTabbedPane.TOP);
            tabbedpane.setMinimumSize(new Dimension(0, 0));
            tabbedpane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                    if (splitpaneopen) {
                        if (selectedsidepanel != null) {
                            selectedsidepanel.panelHidden();
                        }
                        
                        //** YESUS -> Acrescentando proteção de instanceof
                        if(tabbedpane.getSelectedComponent() instanceof javax.swing.JPanel ){
                        	return;
                        }
                        
                        selectedsidepanel = (SidePanel)tabbedpane.getSelectedComponent();
                        if (selectedsidepanel != null) {
                            selectedsidepanel.panelVisible();
                            setPreference("sidePanelName", selectedsidepanel.getName());
                        }
                    }
                }
            });
            splitpane.setTopComponent(tabbedpane);
        }
	}

    /**
     * Set the currently displayed {@link SidePanel}
     * @param panel the SidePanel to display.
     * @since 2.10.3 (prior to this release the name of the panel was specified instead)
     */
    public void setSelectedSidePanel(final SidePanel panel) {
        if (panel == null) {
            splitpane.setDividerLocation(splitpane.getMinimumDividerLocation());
        } else if (tabbedpane.indexOfComponent((Component)panel)>=0) {
            tabbedpane.setSelectedComponent((Component)panel);
            if (splitpane.getDividerLocation() < thresholdsize) {
                int size = splitpane.getLastDividerLocation();
                if (size < thresholdsize) {
                    size = initialsize;
                }
                splitpane.setDividerLocation(size);
            }
        }
    }

    /**
     * Return the currently selected {@link SidePanel}, or
     * <code>null</code> if no panels are displayed.
     * @since 2.10.3 (prior to this release the name of the panel was returned instead)
     */
    public SidePanel getSelectedSidePanel() {
        if (splitpane.getDividerLocation()!=splitpane.getMinimumDividerLocation()) {
            return (SidePanel)tabbedpane.getSelectedComponent();
        } else {
            return null;
        }
    }

    //-----------------------------------------------------------------------------
    // setPDF

    /**
     * Set the PDF to be displayed by this <code>DocumentPanel</code>.
     * A value of <code>null</code> will remove the current PDF from this object
     * and free any resources that reference it - this should be done before this
     * object is disposed of.
     * @param pdf the PDF, or <code>null</code> to remove the current PDF
     */
    public void setPDF(PDF pdf) {
        setPDF(pdf, pdf==null ? null : pdf.getPage(0));
    }

    /**
     * Set the PDF to be displayed by this <code>DocumentPanel</code>, and specify the
     * initial page to display.
     * @param pdf the PDF, or <code>null</code> to remove the current PDF
     * @param page the initial page to display, or <code>null</code> to not display an initial
     * page (exactly how this is handled depends on the Viewport).
     * This will be ignored if the DocumentPanel is part of a PDFViewer and the PDF has an
     * open action that sets the page.
     * @since 2.11
     */
    public void setPDF(PDF pdf, PDFPage page) {
        setPDF(pdf!=null ? new PDFParser(pdf) : null, page);
    }

    /**
     * Set the PDF to be displayed by this <code>DocumentPanel</code>, and specify the
     * initial page to display and the exact {@link PDFParser} to use.
     * @param parser the PDFParser to use to retrieve the PDF from
     * @param page the initial page to display, or <code>null</code> to not display an initial
     * page (exactly how this is handled depends on the Viewport).
     * This will be ignored if the DocumentPanel is part of a PDFViewer and the PDF has an
     * open action that sets the page.
     * @since 2.11.3
     */
    public void setPDF(PDFParser parser, PDFPage page) {
        if (getProperty("debug.Event") != null) {
            System.err.println("[PDF] DocumentPanel.setPDF("+(pdf==null?"null":"pdf")+")");
        }
        initialpageset = false;
        loadedfired = false;
        PDF pdf = parser==null ? null : parser.getPDF();
        PDF oldpdf = this.pdf;
        if (oldpdf != pdf && oldpdf != null) {
            // Can't call oldpdf.close() here, as we may still be rendering thumbnails in background threads etc.
            getJSManager().runEventDocWillClose(this);
            raiseDocumentPanelEvent(DocumentPanelEvent.createClosing(DocumentPanel.this));
            dirtylistener.unbind(oldpdf);
            lastpagenumber = 0;
            if (viewer != null && viewer.getActiveDocumentPanel() == this) {
                raiseDocumentPanelEvent(DocumentPanelEvent.createDeactivated(DocumentPanel.this));
            }
        }
        this.parser = parser;
        this.pdf = pdf;
        linearizedsupport = new LinearizedSupport(this);
        if (pdf == null) {
            if (viewport != null) {
                viewport.setDocumentPanel(null);
            }
            for (Iterator<SidePanel> i = getSidePanels().iterator();i.hasNext();) {
                removeSidePanel(i.next());
            }
            removeAll();
            tabbedpane = null;
            splitpane = null;
            viewport = null;
            selectedsidepanel = null;
        } else {
            linearizedsupport.invokeOnDocumentLoad(new Runnable() {
                public void run() {
                    getPDF().getForm().rebuild();
                }
            });
            if (splitpane == null) {                     // First time through - initialize
                // Threshold - minimum size below which the panel is closed
                // Preferred - initial size and size after a double click to close
                splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitpane.setOneTouchExpandable(true);
                splitpane.setDividerSize(8);
                splitpane.setResizeWeight(0);
                splitpane.setContinuousLayout(true);
                initialsize = 120;
                thresholdsize = 120;
                try {
                    if (getProperty("minSidePanelSize") != null) {
                        thresholdsize = Math.max(10, Math.min(500, Integer.parseInt(getProperty("sidePanelSize"))));
                    }
                } catch (NumberFormatException e) { }
                try {
                    if (getProperty("sidePanelSize")!=null) {
                        initialsize = Math.max(10, Math.min(1000, Integer.parseInt(getProperty("sidePanelSize"))));
                    }
                } catch (NumberFormatException e) { }
                try {
                    String s = getPreference("sidePanelSize");
                    if (s != null) {
                        initialsize = Integer.parseInt(s);
                    }
                } catch (NumberFormatException e) { }
                if (initialsize < thresholdsize) {
                    initialsize = thresholdsize;
                }
                splitpane.setDividerLocation(splitpane.getMinimumDividerLocation());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (splitpane != null) {
                            splitpane.setLastDividerLocation(initialsize);
                        }
                    }
                });
                splitpane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName().equals("dividerLocation")) {
                            int newvalue = ((Integer)event.getNewValue()).intValue();
                            if (newvalue >= thresholdsize) {
                                if (!splitpaneopen) {
                                    splitpaneopen = true;
                                    if (tabbedpane != null) {
                                        SidePanel panel = (SidePanel)tabbedpane.getSelectedComponent();
                                        if (panel != null) {
                                            panel.panelVisible();
                                        }
                                    }
                                    if (getSelectedSidePanel() != null) {
                                        setPreference("sidePanelName", getSelectedSidePanel().getName());
                                    }
                                }
                            } else {
                                newvalue = splitpane.getMinimumDividerLocation();
                                splitpane.setDividerLocation(newvalue);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        if (splitpane != null) {
                                            splitpane.setLastDividerLocation(thresholdsize);
                                        }
                                    }
                                });
                                if (splitpaneopen) {
                                    splitpaneopen = false;
                                    if (tabbedpane != null) {
                                        SidePanel panel = (SidePanel)tabbedpane.getSelectedComponent();
                                        if (panel != null) {
                                            panel.panelHidden();
                                        }
                                    }
                                }
                            }
                            setPreference("sidePanelSize", Integer.toString(newvalue));
                        }
                    }
                });
            }

            // From here we may be replacing a PDF, ie we have one previously
            // open. Close down the old tabbedpanes and open new ones, but if
            // we were previously open make sure we restore the size
            final int currentsize = splitpane.getDividerLocation();
            for (Iterator<SidePanel> i = getSidePanels().iterator();i.hasNext();) {
                removeSidePanel(i.next());
            }

            // WARNING. Really weird stuff happens here if running as an applet - 
            // getViewport will set the viewport if not already set, and in previous
            // version of code we were then re-adding the same item. This was fine in
            // an application but in an applet locked solid for 5 mins spinning deep in
            // Container.removeNotify. No idea why, couldn't attach debugger to applet.
            // Reproducible in OpenJDK7u45 and 6u65 (OS X). Happened only with MPDV,
            // tested several LAFs. Removing it later is fine so got rid of the second
            // add(viewport) here and call it fixed, but left this comment here as a
            // warning to others.
            // 
            getViewport();      // Will call setViewport() if we create a new one here

            initTabbedPane();
            
            for (Iterator<SidePanelFactory> i = panelfactories.iterator();i.hasNext();) {
                SidePanelFactory panelfactory = i.next();
                if (panelfactory.isSidePanelRequired(pdf)) {
                    addSidePanel(panelfactory.createSidePanel());
                }
            }
            
            carregarIndice();
            carregarNotas();
            
            tabbedpane.add("Índice", scrollIndice);
            tabbedpane.add("Notas", notas);
            
            if (currentsize != 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        splitpane.setDividerLocation(currentsize);
                    }
                });
            }

            Object pagemode = pdf.getOption("pagemode");
            if (pagemode == null) {
                pagemode = getProperty("defaultPageMode");
            }
            String sidePanelName = null;
            if ("UseOutlines".equals(pagemode)) {
                sidePanelName = "Bookmarks";
            } else if ("UseThumbs".equals(pagemode)) {
                sidePanelName = "Pages";
            } else if ("UseSignatures".equals(pagemode)) {
                sidePanelName = "Signatures";
            } else {
                String s = getPreference("sidePanelSize");
                if (s != null) {
                    try {
                        if (Integer.parseInt(s) > 0) {
                            sidePanelName = getPreference("sidePanelName");
                        }
                    } catch (Exception e) { }
                }
            }
            boolean sidePanelFound = false;
            if (sidePanelName != null) {
                for (Iterator<SidePanel> i=getSidePanels().iterator();i.hasNext();) {
                    SidePanel panel = i.next();
                    if (panel.getName().equals(sidePanelName)) {
                        setSelectedSidePanel(panel);
                        sidePanelFound = true;
                    }
                }
            }
            if (!sidePanelFound) {
                setSelectedSidePanel(null);
            }
            dirtylistener.bind(this.pdf);

            // This validate() is crucial, as it sizes the DocumentPanel and so provides
            // a size to it's children. Without this, requesting the Viewport zooms to
            // fit won't know how big to zoom it to.
            validate();
            viewport.setDocumentPanel(this);

            if (!initialpageset) {
                if (page != null) {
                    String initzoom = getProperty("defaultZoom");
                    if (initzoom == null) {
                        initzoom = getPreference("zoomMode");
                    }
                    int zoommode = viewport.getZoomMode();
                    float zoom = Float.NaN;
                    if ("fit".equals(initzoom)) {
                        zoommode = DocumentViewport.ZOOM_FIT;
                        viewport.setZoomMode(zoommode);
                    } else if ("fitwidth".equals(initzoom)) {
                        zoommode = DocumentViewport.ZOOM_FITWIDTH;
                        viewport.setZoomMode(zoommode);
                    } else if ("fitheight".equals(initzoom)) {
                        zoommode = DocumentViewport.ZOOM_FITHEIGHT;
                        viewport.setZoomMode(zoommode);
                    } else if ("none".equals(initzoom)) {
                        zoommode = DocumentViewport.ZOOM_NONE;
                    } else if (initzoom != null) {
                        try {
                            zoom = Math.max(0.125f, Math.min(64, Float.parseFloat(initzoom) / 100));
                        } catch (Exception e) { }
                    }
                    if (zoom != zoom) {
                        zoom = viewport.getTargetZoom(zoommode, page);
                    }
                    setPage(page, 0, 0, zoom);
                }
            }
            viewport.requestFocusInWindow();
        }

        if (oldpdf != null) {
            // This is necessary to ensure prompt return of resources, but if anyone
            // still has a handle on the file then carnage will ensue. Particularly
            // when merging files this is bad. So let customer enable it.
            if (getProperty("EarlyClose") != null || getProperty("earlyClose") != null) {      // legacy
                oldpdf.close();
            }
        }
        repaint();
    }
    

    /**
     * @author Yesus
     * Carregar Indice
     * @return
     */
    public void carregarIndice() {
    	
    	DefaultMutableTreeNode dmtIndice = new IndiceBO()
				.montarArvoreIndice(viewer.getLivroIDR()
						.getIndiceByteArray());
		JTree arvoreIndice = new JTree(
				dmtIndice);
		arvoreIndice
				.getSelectionModel()
				.setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);
		arvoreIndice
				.addTreeSelectionListener(new TreeSelectionListener() {
					public void valueChanged(
							TreeSelectionEvent arg0) {
						try {
							Item item = (Item) ((DefaultMutableTreeNode) (arg0
									.getPath()
									.getLastPathComponent()))
									.getUserObject();
							setPageNumber(Integer.parseInt(item.getPaginareal()) - 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		
    	scrollIndice = new JScrollPane(
				arvoreIndice);
    }
    
    /**
     * @author Yesus
     * Carregar Notas
     * @return
     */
    public void carregarNotas() {
    	notas = new JPanel();
		notas.setBackground(Color.WHITE);
		DesignGridLayout ds = new DesignGridLayout(notas);
		// DesignGridLayout layoutNotas = new DesignGridLayout(notas);
		NotaBO notaBO = new NotaBO();
		
		ArrayList<Nota> listaNotas = notaBO.listaNotasGravadas(viewer.getTitle());

		for (final Nota nota : listaNotas) {
			JButton btnNota = new JButton("Pág. " + nota.getPagina() + " - "
					+ nota.getTitulo());
			btnNota.setBackground(Color.WHITE);
			btnNota.setIcon(IdrUtil.getImageIcon("gfx/notas.png"));
			btnNota.setHorizontalAlignment(SwingConstants.LEFT);
			btnNota.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setPageNumber(nota.getPagina() - 1);
					new JanelaNota(viewer, viewer.getTitle(), nota.getPagina());
				}
			});
			ds.row().grid().add(btnNota);
		}
	}

    private void postLoaded() {
        boolean found = false;
        if (getProperty("respectSignatureCertification") != null) {
            for (Iterator<FormElement> i = pdf.getForm().getElements().values().iterator();i.hasNext();) {
                FormElement elt = i.next();
                if (elt instanceof FormSignature) {
                    FormSignature sig = (FormSignature)elt;
                    if (sig.getState() == FormSignature.STATE_SIGNED) {
                        int v = sig.getSignatureHandler().getCertificationType();
                        if (v != 0 && (signaturePermissionDenied == 0 || v < signaturePermissionDenied)) {
                            setSignaturePermissionRestrictions(sig);
                            found = true;
                        }
                    }
                }
            }
        }
        if (!found) {
            setSignaturePermissionRestrictions(null);
        }
    }

    //-----------------------------------------------------------------------------
    // Controlling the view

    /**
     * Get the PDFParser being used to parse this PDF.
     */
    public PDFParser getParser() {
        return parser;
    }

    /**
     * Return the PDF currently being displayed by this <code>DocumentPanel</code>
     */
    public PDF getPDF() {
        return pdf;
    }

    /**
     * Return the PDFPage currently being displayed by the {@link DocumentViewport}.
     * If no PDF is set or the first page is still being rendered, this method will return
     * <code>null</code>.
     */
    public PDFPage getPage() {
        return getViewport().getPage();
    }

    /**
     * Return the LinearizedSupport object for this DocumentPanel
     * @since 2.14.1
     */
    public LinearizedSupport getLinearizedSupport() {
        return linearizedsupport;
    }

    /**
     * Set the page being displayed. A shortcut for <code>setPage(getPDF().getPage(i))</code>.
     */
    public void setPageNumber(final int i) {
        getLinearizedSupport().invokeOnPageLoadWithDialog(i, new Runnable() {
            public void run() {
                PDF pdf = getPDF();
                if (pdf != null) {
                    setPage(pdf.getPage(i));
                }
            }
        });
    }

    /**
     * Return the pagenumber of the currently displayed page starting at 0, or -1 if no
     * page is being displayed.
     */
    public int getPageNumber() {
        PDFPage page = getPage();
        return page==null ? -1 : page.getPageNumber()-1;
    }

    /**
     * Return the current zoom level. A value of 1 means the document is being displayed
     * at it's actual size, 0.5 means 50% and so on.
     */
    public float getZoom() {
        return getViewport().getZoom();
    }

    /**
     * Set the current zoom level
     * @param zoom the zoom level
     */
    public void setZoom(float zoom) {
        getViewport().setZoom(zoom);
    }

    /**
     * Set the page to display in the {@link DocumentViewport}. The page is displayed
     * at it's top-left and at the current zoom level.
     * @param page the page
     */
    public void setPage(PDFPage page) {
        setPage(page, 0, 0, getViewport().getTargetZoom(getViewport().getZoomMode(), page));
    }

    /**
     * Set the page to display in the {@link DocumentViewport}. The page is displayed
     * at the co-ordinates supplied and at the specified zoom level.
     * @param page the page
     * @param x the left-most position of the page to display, in units relative to {@link PagePanel#getFullPageView}
     * @param y the top-most position of the page to display, in units relative to {@link PagePanel#getFullPageView}
     * @param zoom the zoom level
     */
    public void setPage(PDFPage page, float x, float y, float zoom) {
        initialpageset = true;
        lastpagenumber = page.getPageNumber()-1;
        getViewport().setPage(page, x, y, zoom);
    }

    int getLastPageNumber() {
        return lastpagenumber;
    }

    /**
     * Redraw the specified object.
     * param o the Object that has been altered - typically a {@link PDFPage} or {@link PDFAnnotation}
     * @deprecated DocumentPanel.redraw() is no longer required as this object now listens to
     * {@link PropertyChangeEvent PropertyChangeEvents} fired by the PDF. This method is not called
     * anywhere and is a no-op
     */
    public void redraw(Object o) {
    }

    /**
     * Set the document as being "dirty", ie that it has been modified since loading.
     * The property <code>noDirtyDocuments</code> can be set to prevent this value
     * from being set.
     * @since 2.11.19
     */
    public void setDirty(boolean dirty) {
        if (dirty != this.dirty && getProperty("noDirtyDocuments") == null) {
            this.dirty = dirty;
            firePropertyChange("dirty", !this.dirty, this.dirty);
        }
    }

    /**
     * Updates the window title with a "*" for dirty or the load progress
     * for linearized. Called in response to the propertyChange events for "dirty" and "loadProgress",
     * to disable it just set the "noProgressInTitle" or "noDirtyInTitle" properties to not-null
     */
    private void updateTitle() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JInternalFrame f = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, DocumentPanel.this);
                if (f != null) {
                    getWindowTitle();
                    int percent = Math.round(getLinearizedSupport().getLoadProgress() * 100);
                    String s = windowtitle;
                    if (percent < 100 && getProperty("noProgressInTitle") == null) {
                        s += " ("+percent+"%)";
                    }
                    if (isDirty() && getProperty("noDirtyInTitle") == null) {
                        s += " *";
                    }
                    f.setTitle(s);
                }
            }
        });
    }

    /**
     * Get the base title of the window containing this DocumentPanel,
     * which does not include any "*" or loading progress details.
     * @since 2.15.4
     */
    public String getWindowTitle() {
        if (windowtitle == null) {
            JInternalFrame f = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, DocumentPanel.this);
            if (f != null) {
                windowtitle = f.getTitle();
            }
            if (windowtitle == null) {
                windowtitle = "Document";
            }
        }
        return windowtitle;
    }

    /**
     * Set the base title of the window containing this DocumentPanel
     * @param title the new title.
     * @since 2.15.4
     */
    public void setWindowTitle(String title) {
        this.windowtitle = title;
        updateTitle();
    }

    /**
     * Return the value of the dirty flag, as set by {@link #setDirty}
     * @since 2.11.19
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Add an {@link UndoableEditListener} to this DocumentPanel
     * @since 2.11.19
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        synchronized(undolisteners) {
            undolisteners.add(l);
        }
    }

    /**
     * Remove an {@link UndoableEditListener} from this DocumentPanel
     * @since 2.11.19
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        synchronized(undolisteners) {
            undolisteners.remove(l);
        }
    }

    /**
     * Fire an {@link UndoableEditEvent} on this DocumentPanel. As a special
     * hack, passing <code>null</code> to this method will truncate the list
     * of events
     * @since 2.11.19
     */
    public void fireUndoableEditEvent(UndoableEditEvent e) {
        synchronized(undolisteners) {
            if (e.getSource() != this) {
                throw new IllegalArgumentException("Source of UndoableEditEvent must be docpanel");
            }
            for (Iterator<UndoableEditListener> i = undolisteners.iterator();i.hasNext();) {
                i.next().undoableEditHappened(e);
            }
        }
    }

    //-----------------------------------------------------------------------------
    // Other document actions

    /**
     * Display a Print dialog for printing this document, or if a {@link PrintService} is
     * specified, print directly to that service without displaying a dialog.
     * @param fservice the PrintService to print to. If this value is <code>null</code>
     * a dialog will be displayed allowing the selection of a service.
     * @param fatts the print attributes - may be set to an AttributeSet to control the
     * printing, or <code>null</code> to use the default.
     */
    public void print(final PrintService fservice, final PrintRequestAttributeSet fatts) throws PrintException, PrinterException {
        if (pdf==null) throw new NullPointerException("Document is null");

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws PrintException, PrinterException {
                    PrintService service = fservice;
                    PrintRequestAttributeSet atts = fatts;
                    if (atts==null) {
                        atts = new HashPrintRequestAttributeSet();
                    }
                    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
                    if (service==null) {
                        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, atts);
                        if (services.length>0) {
                            PDFPage page = getPage();
                            if (atts.get(MediaSize.class)==null) {
                                try {
                                    float w = page.getWidth()/72f;
                                    float h = page.getHeight()/72f;
                                    if (h>w) {
                                        atts.add(MediaSize.findMedia(w, h, Size2DSyntax.INCH));
                                    } else {
                                        atts.add(MediaSize.findMedia(h, w, Size2DSyntax.INCH));
                                        atts.add(OrientationRequested.LANDSCAPE);
                                    }
                                } catch (Exception e) {}
                            }
                            if (atts.get(Sides.class)==null && pdf.getOption("print.duplex")!=null) {
                                String s = (String)pdf.getOption("print.duplex");
                                if ("DuplexFlipShortEdge".equals(s)) {
                                    atts.add(Sides.TWO_SIDED_SHORT_EDGE);
                                } else if ("DuplexFlipLongEdge".equals(s)) {
                                    atts.add(Sides.TWO_SIDED_LONG_EDGE);
                                }
                            }
                            if (atts.get(PageRanges.class)==null) {
                                List l = (List)pdf.getOption("print.pagerange");
                                if (l!=null) {
                                    int[][] x = new int[l.size()][];
                                    for (int i=0;i<l.size();i++) {
                                        x[i] = new int[] { ((PDFPage)l.get(i)).getPageNumber() };
                                    }
                                    atts.add(new PageRanges(x));
                                }
                            }
                            if (atts.get(Copies.class)==null && pdf.getOption("print.numcopies")!=null) {
                                atts.add(new Copies(((Integer)pdf.getOption("print.numcopies")).intValue()));
                            }
                            PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
                            if (ps == null) {
                                ps = services[0];
                            }
                            service =  ServiceUI.printDialog(null, 50, 50, services, ps, flavor, atts);
                        } else {
                            JOptionPane.showMessageDialog(DocumentPanel.this, UIManager.getString("PDFViewer.NoPrinters"), UIManager.getString("PDFViewer.Alert"), JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    if (service!=null) {
                        if (atts.get(DocumentName.class)==null) {
                            try {
                                String title = pdf.getInfo("Title");
                                if (title!=null) atts.add(new DocumentName(title, Locale.getDefault()));
                            } catch (ClassCastException e) {
                            }
                        }

                        final PrinterJob job = PrinterJob.getPrinterJob();
                        job.setPrintService(service);
                        job.setPageable(new Pageable() {
                            public int getNumberOfPages() {
                                return parser.getNumberOfPages();
                            }
                            public Printable getPrintable(int pagenumber) {
                                return parser.getPrintable(pagenumber);
                            }
                            public PageFormat getPageFormat(int pagenumber) {
                                PageFormat format = parser.getPageFormat(pagenumber);
                                Paper paper = job.defaultPage(format).getPaper();
                                paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
                                format.setPaper(paper);
                                return format;
                            }
                        });
                        getJSManager().runEventDocWillPrint(DocumentPanel.this);
                        job.print(atts);
                        // Technically we should be printing with a javax.print.SimpleDoc
                        // and run this event on the print completed event. However we
                        // dropped SimpleDoc for PrinterJob in 1.17, for something to
                        // do with landscape or odd media sizes as I recall. Double
                        // check this before implementing.
                        getJSManager().runEventDocDidPrint(DocumentPanel.this);
                        if (service instanceof StreamPrintService) {
                            ((StreamPrintService)service).dispose();
                        }
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getException() instanceof PrintException) {
                throw (PrintException)e.getException();
            } else {
                throw (PrinterException)e.getException();
            }
        }
    }
    
    
    public void imprimirPaginaAtual(){
		
		if(JOptionPane.showConfirmDialog(this, 
											"Deseja realmente imprimir esta página? ", 
											"Imprimir",
											JOptionPane.YES_NO_OPTION) ==
									JOptionPane.OK_OPTION){	
				
				final PrinterJob job = PrinterJob.getPrinterJob();
				 job.setPageable(new Pageable() {
                      public int getNumberOfPages() {
                          return parser.getNumberOfPages();
                      }
                      public Printable getPrintable(int pagenumber) {
                          return parser.getPrintable(pagenumber);
                      }
                      public PageFormat getPageFormat(int pagenumber) {
                          PageFormat format = parser.getPageFormat(pagenumber);
                          Paper paper = job.defaultPage(format).getPaper();
                          paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight());
                          format.setPaper(paper);
                          return format;
                      }
                  });
				
				PrintRequestAttributeSet atributosImpressao = new HashPrintRequestAttributeSet();
				atributosImpressao.add(new Copies(1));
				//atributosImpressao.add(new CopiesSupported(1));
				atributosImpressao.add(MultipleDocumentHandling.SINGLE_DOCUMENT);
				atributosImpressao.add(new PageRanges(getPage().getPageNumber(), getPage().getPageNumber()));
				
				try {
					job.print(atributosImpressao);
				} catch (PrinterException e) {
					e.printStackTrace();
				}
		}
		
	}
    

    /**
     * Indicates whether the specified type of action is allowed for this
     * DocumentPanel. A permission is allowed by default, but may be denied
     * permanently by the Document's {@link EncryptionHandler}, or temporarily
     * by a call to {@link #setPermission}. The currently used list of
     * permissions includes:
     * <dl>
     * <dt>Print</dt><dd>The document may be printed.</dd>
     * <dt>Extract</dt><dd>Text may be extracted from the document.</dd>
     * <dt>Assemble</dt><dd>The pages in the document may be added, removed, rotated or reordered.</dd>
     * <dt>Annotate</dt><dd>Annotations may be added, removed, moved around, or edited.</dd>
     * <dt>FormFill</dt><dd>Form fields may be filled in and the form submitted.</dd>
     * <dt>PageEdit</dt><dd>The page content may be edited, ie the page may be cropped or redacted</dd>
     * <dt>Save</dt><dd>The document may be saved.</dd>
     * </dl>
     * Other non-standard permissions may be also be used if customization is required.
     * By default anything not recognized will return true.
     * @see #setPermission
     * @see #setSignaturePermissionRestrictions
     * @see EncryptionHandler#hasRight
     * @see FormSignature#getCertificationType
     * @since 2.13
     */
    public boolean hasPermission(String permission) {
        EncryptionHandler handler = pdf == null ? null : pdf.getEncryptionHandler();
        return (handler == null || handler.hasRight(permission)) &&
               (signaturePermissionDenied != FormSignature.CERTIFICATION_NOCHANGES || (!"Assemble".equals(permission) && !"Annotate".equals(permission) && !"FormFill".equals(permission)) && !"PageEdit".equals(permission)) &&
               (signaturePermissionDenied != FormSignature.CERTIFICATION_ALLOWFORMS || (!"Assemble".equals(permission) && !"Annotate".equals(permission) && !"PageEdit".equals(permission))) &&
               (signaturePermissionDenied != FormSignature.CERTIFICATION_ALLOWCOMMENTS || (!"Assemble".equals(permission) && !"PageEdit".equals(permission))) &&
                !permissiondenied.contains(permission);
    }

    /**
     * Sets whether the specified permission is allowed on this DocumentPanel.
     * @param permission the permission
     * @param enable true to allow the action, false otherwise
     * @see #hasPermission
     * @see #setSignaturePermissionRestrictions
     * @since 2.13
     */
    public void setPermission(String permission, boolean enable) {
        if (enable ? permissiondenied.remove(permission) : permissiondenied.add(permission)) {
            raiseDocumentPanelEvent(DocumentPanelEvent.createPermissionChanged(this, permission));
        }
    }

    /**
     * Limit the permissions that can be {@link #setPermission set} on this PDF
     * to ensure they don't conflict with the {@link FormSignature#getCertificationType certification}
     * of this signature. This can be used to ensure that modifications to a PDF don't
     * invalidate an existing digital siganture that disallows them. By default this is
     * not the case, but setting the
     * <code>respectSignatureCertification</code> <a href="#initParam">initialization-parameter</a>
     * will ensure those restrictions are respected. This method can be called with <code>null</code>
     * to make the setting of permissions unrestricted.
     * @see #hasPermission
     * @see FormSignature#getCertificationType
     * @since 2.13
     */
    public void setSignaturePermissionRestrictions(FormSignature sig) {
        int oldval = signaturePermissionDenied;
        signaturePermissionDenied = sig == null || sig.getSignatureHandler() == null ? 0 : sig.getSignatureHandler().getCertificationType();
        if (oldval != signaturePermissionDenied) {
            raiseDocumentPanelEvent(DocumentPanelEvent.createPermissionChanged(this, null));
        }
    }
    
    public void refreshTabs(){
    	tabbedpane.remove(1);
    	tabbedpane.remove(1);
  	
  	    carregarIndice();
        carregarNotas();
      
        tabbedpane.add("Índice", scrollIndice);
        tabbedpane.add("Notas", notas);
        
        tabbedpane.setSelectedComponent(notas);
     
//    	tabbedpane.revalidate();	
//    	viewer.revalidate();
    }
    
    /*
    public static void main(final String args[]) throws Exception {
        final PDF pdf = new PDF(new PDFReader(new java.io.File(args[0])));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final DocumentPanel panel = new DocumentPanel();
//                panel.addAnnotationComponentFactory(new org.faceless.pdf2.viewer3.feature.FormTextWidgetFactory());
                JFrame frame = new JFrame("BFO");
                frame.getContentPane().add(panel);
                panel.setPDF(pdf);
                frame.setSize(600, 700);
                frame.setVisible(true);
            }
        });
    }
    */
}
