// $Id: SelectArea.java 17736 2013-06-20 12:43:11Z chris $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * An {@link AbstractRegionSelector} that allows a rectangular to be selected for
 * PDF operations. Once selected, this class will look for any features that implement
 * {@link AreaSelectionAction} and allow for the user to choose from them when the
 * area is right-clicked.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">SelectArea</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.25
 * @see TextTool
 * @see AreaSelectionAction
 */
public class SelectArea extends AbstractRegionSelector {

    private SelectionComponent comp;
    private static final int DRAG_HANDLE_WIDTH = 5;

    public SelectArea() {
        super("SelectArea");
        setButton("Mode", "resources/icons/select_area.png", "PDFViewer.tt.SelectArea");
    }

    protected void updateViewport(DocumentViewport viewport, boolean selected) {
        super.updateViewport(viewport, selected);
        if (!selected) {
            removeSelection();
        }
    }

    protected JComponent createRubberBoxComponent() {
        removeSelection();      // To remove existing selection
        return super.createRubberBoxComponent();
    }

    private void removeSelection() {
        if (comp != null) {
            comp.setVisible(false);
            if (comp.getParent() != null) {
                comp.getParent().remove(comp);
            }
            comp = null;
        }
    }

    public void action(PagePanel panel, Point2D start, Point2D end) {
        removeSelection();
        comp = new SelectionComponent();
        // Done this way to ensure it stays the same location when we change zoom levels
        AnnotationComponentFactory.bindComponentLocation(comp, (float)start.getX(), (float)start.getY(), (float)end.getX(), (float)end.getY());
        comp.setAdjustable(true);
        panel.add(comp);
        panel.revalidate();
    }

    private class SelectionComponent extends JComponent implements MouseListener, MouseMotionListener {

        private int c; // current op
        private Point down; // mousedown
        private boolean adjustable;

        public void setAdjustable(boolean adjustable) {
            this.adjustable = adjustable;
            if (adjustable) {
                addMouseListener(this);
                addMouseMotionListener(this);
            } else {
                removeMouseListener(this);
                removeMouseMotionListener(this);
            }
        }

        public void addNotify() {
            super.addNotify();
            new Thread() {
                public void run() {
                    try {
                        while (isDisplayable()) {
                            Thread.sleep(AbstractRegionSelector.CRAWLSPEED);
                            repaint();
                        }
                    } catch (InterruptedException e) { }
                }
            }.start();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 15, new float[] { CRAWLLENGTH, CRAWLLENGTH }, (int)((System.currentTimeMillis()/CRAWLSPEED)%(CRAWLLENGTH*2)));
            g.setColor(Color.black);
            ((Graphics2D)g).setStroke(stroke);
            Dimension size = getSize();
            int w = size.width - 1, h = size.height - 1;
            g.drawRect(0, 0, w, h);
            if (adjustable) {
                // Draw drag handles
                drawDragHandle(g, 0, 0);
                drawDragHandle(g, (w / 2) - (DRAG_HANDLE_WIDTH / 2), 0);
                drawDragHandle(g, w - DRAG_HANDLE_WIDTH, 0);
                drawDragHandle(g, 0, (h / 2) - (DRAG_HANDLE_WIDTH / 2));
                drawDragHandle(g, w - DRAG_HANDLE_WIDTH, (h / 2) - (DRAG_HANDLE_WIDTH / 2));
                drawDragHandle(g, 0, h - DRAG_HANDLE_WIDTH);
                drawDragHandle(g, (w / 2) - (DRAG_HANDLE_WIDTH / 2), h - DRAG_HANDLE_WIDTH);

                drawDragHandle(g, w - DRAG_HANDLE_WIDTH, h - DRAG_HANDLE_WIDTH);
            }
        }

        private void drawDragHandle(Graphics g, int x, int y) {
            g.fillRect(x, y, DRAG_HANDLE_WIDTH, DRAG_HANDLE_WIDTH);
        }

        private void updateCursor(Point p) {
            int x = p.x, y = p.y;
            Dimension size = getSize();
            int w = size.width - 1, h = size.height - 1;
            if (y <= DRAG_HANDLE_WIDTH) {
                if (x <= DRAG_HANDLE_WIDTH) {
                    c = Cursor.NW_RESIZE_CURSOR;
                } else if (x >= w - DRAG_HANDLE_WIDTH) {
                    c = Cursor.NE_RESIZE_CURSOR;
                } else {
                    c = Cursor.N_RESIZE_CURSOR;
                }
            } else if (y >= h - DRAG_HANDLE_WIDTH) {
                if (x <= DRAG_HANDLE_WIDTH) {
                    c = Cursor.SW_RESIZE_CURSOR;
                } else if (x >= w - DRAG_HANDLE_WIDTH) {
                    c = Cursor.SE_RESIZE_CURSOR;
                } else {
                    c = Cursor.S_RESIZE_CURSOR;
                }
            } else if (x <= DRAG_HANDLE_WIDTH) {
                c = Cursor.W_RESIZE_CURSOR;
            } else if (x >= w - DRAG_HANDLE_WIDTH) {
                c = Cursor.E_RESIZE_CURSOR;
            } else {
                c = Cursor.HAND_CURSOR;
            }
            setCursor(Cursor.getPredefinedCursor(c));
        }

        public void mousePressed(MouseEvent event) {
            down = event.getPoint();
        }

        public void mouseReleased(MouseEvent event) {
            PagePanel pagepanel = (PagePanel)getParent();
            Rectangle2D bounds = pagepanel.getScreenToPageTransform().createTransformedShape(getBounds()).getBounds2D();
            AnnotationComponentFactory.bindComponentLocation(comp, bounds);
            down = null;
        }

        public void mouseExited(MouseEvent event) {
        }

        public void mouseClicked(MouseEvent event) {
            final PagePanel pagepanel = (PagePanel)getParent();
            final DocumentViewport viewport = pagepanel.getViewport();
            final PDFViewer viewer = getViewer();

            ActionMap actionmap = viewport.getActionMap();
            ViewerFeature[] features = viewer.getFeatures();
            for (int i = 0; i < features.length; i++) {
                if (features[i] instanceof AreaSelectionAction) {
                    final AreaSelectionAction asa = (AreaSelectionAction) features[i];
                    String name = features[i].getName();
                    String label = asa.getDescription();
                    Action action = new AbstractAction(label) {
                        public void actionPerformed(ActionEvent e) {
                            Rectangle2D bounds = pagepanel.getScreenToPageTransform().createTransformedShape(getBounds()).getBounds2D();
                            asa.selectArea(pagepanel, bounds);
                            removeSelection();
                        }
                    };
                    actionmap.put(features[i], action);
                }
            }

            JPopupMenu popup = new JPopupMenu();
            Object[] keys = actionmap.keys();
            boolean found = false;
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] instanceof AreaSelectionAction) {
                    AreaSelectionAction asa = (AreaSelectionAction) keys[i];
                    if (asa.isEnabled()) {
                        found = true;
                        popup.add(actionmap.get(asa));
                    }
                }
            }
            if (found) {
                popup.show(event.getComponent(), event.getX(), event.getY());
            } else {
                pagepanel.remove(this);
            }
        }

        public void mouseEntered(MouseEvent event) {
            if (down == null) { // otherwise quick drag out and in will change handle
                updateCursor(event.getPoint());
            }
        }

        public void mouseDragged(MouseEvent event) {
            if (down == null) {
                return;
            }
            Point p = event.getPoint();
            int dx = p.x - down.x, dy = p.y - down.y;
            Rectangle bounds = getBounds();
            switch (c) {
                case Cursor.N_RESIZE_CURSOR:
                    bounds.y += dy;
                    bounds.height -= dy;
                    p.y -= dy;
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    bounds.height += dy;
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    bounds.x += dx;
                    bounds.width -= dx;
                    p.x -= dx;
                    break;
                case Cursor.E_RESIZE_CURSOR:
                    bounds.width += dx;
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    bounds.x += dx;
                    bounds.y += dy;
                    bounds.width -= dx;
                    bounds.height -= dy;
                    p.x -= dx;
                    p.y -= dy;
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    bounds.y += dy;
                    bounds.width += dx;
                    bounds.height -= dy;
                    p.y -= dy;
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    bounds.x += dx;
                    bounds.width -= dx;
                    bounds.height += dy;
                    p.x -= dx;
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    bounds.width += dx;
                    bounds.height += dy;
                    break;
                default:
                    bounds.x += dx;
                    bounds.y += dy;
                    p.x -= dx;
                    p.y -= dy;
            }
            down = p;
            bounds.width = Math.max(8, bounds.width);
            bounds.height = Math.max(8, bounds.height);
            setBounds(bounds);
        }

        public void mouseMoved(MouseEvent event) {
            updateCursor(event.getPoint());
        }

    }

}
