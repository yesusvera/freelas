// $Id: AnnotationAddLine.java 17519 2013-05-02 12:38:01Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;

import org.faceless.pdf2.AnnotationShape;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.PagePanel;
import org.faceless.pdf2.viewer3.ViewerEvent;

/**
 * A feature that allows new line annotations to be
 * drawn directly onto the PDF Page.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">AnnotationAddLine</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11
 */
public class AnnotationAddLine extends AbstractRegionSelector {

    private AnnotationLineFactory factory;
    private final Action action;

    public AnnotationAddLine() {
        super("AnnotationAddLine");
        setButton("Mode", "resources/icons/arrow_add.png", "PDFViewer.tt.AnnotationAddLine");
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
        factory = new AnnotationLineFactory();
        factory.initialize(viewer);
    }

    public void documentUpdated(DocumentPanelEvent event) {
        super.documentUpdated(event);
        String type = event.getType();
        if (type.equals("activated") || (type.equals("permissionChanged") && event.getDocumentPanel() == getViewer().getActiveDocumentPanel())) {
            action.setEnabled(event.getDocumentPanel().getPDF() != null && event.getDocumentPanel().hasPermission("Annotate"));
        } else if (type.equals("deactivated")) {
            action.setEnabled(false);
        }
        if (isSelected() && !action.isEnabled()) {
            setSelected(false);
        }
    }

    protected JComponent createRubberBoxComponent() {
        if (!action.isEnabled()) {
            return null;
        }
        return new JPanel() {
            Point startpoint;
            public void setLocation(int x, int y) {
                super.setLocation(x, y);
                if (startpoint==null) {
                    startpoint = new Point(x, y);
                }
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 15, new
                float[] { CRAWLLENGTH, CRAWLLENGTH }, (int)((System.currentTimeMillis()/CRAWLSPEED)%(CRAWLLENGTH*2)));
                g.setColor(Color.black);
                ((Graphics2D)g).setStroke(stroke);
                Rectangle b = getBounds();
                int ex = b.x != startpoint.x ? b.x : b.x + b.width;
                int ey = b.y != startpoint.y ? b.y : b.y + b.height;
                g.drawLine(startpoint.x - b.x, startpoint.y - b.y, ex - b.x, ey - b.y);
            }
        };
    }

    public void action(PagePanel panel, Point2D start, Point2D end) {
        if (!action.isEnabled()) {
            return;
        }
        final PDFPage page = panel.getPage();
        float[] box = page.getBox("ViewBox");
        Rectangle2D rect = new Rectangle2D.Float(box[0], box[1], box[2]-box[0], box[3]-box[1]);
        if (rect.contains(start) && rect.contains(end)) {
            Line2D.Float line = new Line2D.Float(start, end);
            final AnnotationShape annot = (AnnotationShape)factory.createNewAnnotation(0, 0, 0, 0);
            annot.setShape(line);
            annot.setPage(panel.getPage());
            annot.rebuild();

            final DocumentPanel docpanel = panel.getViewport().getDocumentPanel();
            docpanel.fireUndoableEditEvent(new UndoableEditEvent(docpanel, new AbstractUndoableEdit() {
                public String getPresentationName() {
                    return UIManager.getString("PDFViewer.annot.Line");
                }
                public void undo() {
                    super.undo();
                    annot.setPage(null);
                }
                public void redo() {
                    super.redo();
                    annot.setPage(page);
                    annot.rebuild();
                }
            }));
        }
    }

}
