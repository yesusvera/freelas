// $Id: AbstractRegionSelector.java 17323 2013-04-11 16:23:46Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;

/**
 * An abstract superclass for any widgets that require a region to be selected. Subclasses
 * should override the {@link #action(PagePanel, Point2D, Point2D)} method.
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8.5
 */
public abstract class AbstractRegionSelector extends ToggleViewerWidget implements DocumentPanelListener, PagePanelInteractionListener {

    static final int CRAWLSPEED = 60, CRAWLLENGTH = 4;
    private JComponent rubberbox;
    private Point startpoint;

    protected AbstractRegionSelector(String name) {
        super(name, DragScroll.GROUP);
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public void action(ViewerEvent event) {
        if (!isSelected()) {
            setSelected(true);
        }
    }

    protected void updateViewport(DocumentViewport vp, boolean selected) {
        if (selected) {
            vp.addPagePanelInteractionListener(this);
            if (vp.getPagePanel()!=null) {
                vp.getPagePanel().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        } else {
            vp.removePagePanelInteractionListener(this);
            if (vp.getPagePanel()!=null) {
                vp.getPagePanel().setCursor(null);
            }
        }
    }

    public void documentUpdated(DocumentPanelEvent event) {
        String type = event.getType();
        if ("viewportChanged".equals(type) || "activated".equals(type)) {
            if (getGroupSelection(getGroupName()) == null) {
                PropertyManager manager = getViewer()==null ? PDF.getPropertyManager() : getViewer().getPropertyManager();
                String defaultmode = manager == null ? null : manager.getProperty("default"+getGroupName());
                if (defaultmode == null && manager != null) {
                    defaultmode = manager.getProperty("Default"+getGroupName());        // legacy
                }
                if (getName().equals(defaultmode)) {
                    setSelected(true);
                }
            }
            if (isSelected()) {
                updateViewport(event.getDocumentPanel().getViewport(), true);
            }
        }
    }

    /**
     * Create the JComponent that it used to display the "rubber box". If you need to
     * display some custom appearance when overriding this class, this method should
     * be overridden.
     * @since 2.11.7
     */
    protected JComponent createRubberBoxComponent() {
        return new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintRubberBandComponent(this, (Graphics2D)g);
                BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 15, new float[] { CRAWLLENGTH, CRAWLLENGTH }, (int)((System.currentTimeMillis()/CRAWLSPEED)%(CRAWLLENGTH*2)));
                g.setColor(Color.black);
                ((Graphics2D)g).setStroke(stroke);
                g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            }
        };
    }

    public void pageAction(PagePanelInteractionEvent event) {
        PagePanel panel = event.getPagePanel();
        if (isSelected()) {
            if (event.getType()=="mousePressed") {
                rubberbox = createRubberBoxComponent();
                startpoint = event.getMouseEvent().getPoint();
                rubberbox.setLocation(startpoint);
                rubberbox.setOpaque(false);
                rubberbox.setSize(0, 0);
                panel.add(rubberbox);
                new Thread() {
                    public void run() {
                        try {
                            while (rubberbox!=null) {
                                Thread.sleep(CRAWLSPEED);
                                JComponent c = rubberbox;
                                if (c!=null) c.repaint();
                            }
                        } catch (InterruptedException e) { }
                    }
                }.start();
            } else if (event.getType()=="mouseDragged" && startpoint!=null) {
                Point p = event.getMouseEvent().getPoint();
                p.x = Math.min(Math.max(0, p.x), panel.getWidth());
                p.y = Math.min(Math.max(0, p.y), panel.getHeight());
                rubberbox.setLocation(Math.min(p.x, startpoint.x), Math.min(p.y, startpoint.y));
                rubberbox.setSize(Math.abs(p.x-startpoint.x), Math.abs(p.y-startpoint.y));
            } else if (event.getType()=="mouseReleased" && startpoint!=null) {
                Point p = event.getMouseEvent().getPoint();
                p.x = Math.min(Math.max(0, p.x), panel.getWidth());
                p.y = Math.min(Math.max(0, p.y), panel.getHeight());
                panel.remove(rubberbox);
                panel.repaint();
                action(panel, panel.getPDFPoint(startpoint.x, startpoint.y), panel.getPDFPoint(p.x, p.y));
                startpoint = null;
                rubberbox = null;
            }
        }
    }

    /**
     * Paint the component while the "rubber band" box is being stretched.
     * This method may be overriden if something is to be painted inside the
     * box during this time.
     * @param component the "rubber band" box being drawn
     * @param g the Graphic2D object to draw on.
     */
    public void paintRubberBandComponent(JComponent component, Graphics2D g) {
    }

    /**
     * Called when an area of the PDF has been selected.
     * @param panel the PagePanel the selection was made on.
     * @param start the start point of the selection, in PDF-units
     * @param end the end point of the selection, in PDF-units
     */
    public void action(PagePanel panel, Point2D start, Point2D end) {
    }

}
