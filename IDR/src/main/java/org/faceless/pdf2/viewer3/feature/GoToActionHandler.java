// $Id: GoToActionHandler.java 18779 2013-12-17 17:21:00Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFAction;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.ActionHandler;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentViewport;
import org.faceless.pdf2.viewer3.PagePanel;
import org.faceless.pdf2.viewer3.Util;

/**
 * Create an action handler for "GoTo" actions and the named actions that move between
 * pages.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">GoToActionHandler</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class GoToActionHandler extends ActionHandler {

    /**
     * Create a new GoToActionHandler
     * @since 2.11 just calls the public constructor
     */
    public GoToActionHandler() {
        super("GoToActionHandler");
    }

    public boolean matches(DocumentPanel panel, PDFAction action) {
        String type = action.getType();
        return type.equals("GoToFit") || type.equals("GoTo") || type.equals("GoToFitWidth") || type.equals("GoToFitHeight") || type.equals("GoToFitRectangle") || type.equals("Named:FirstPage") || type.equals("Named:PrevPage") || type.equals("Named:NextPage") || type.equals("Named:LastPage");
    }

    public void run(DocumentPanel docpanel, PDFAction action) {
        PDF pdf = docpanel.getPDF();
        String type = action.getType();
        GoToAction ga = null;

        if (type.equals("GoTo")) {
            ga = new GoTo(docpanel, pdf, action);
        } else if (type.equals("GoToFit")) {
            ga = new GoToFit(docpanel, pdf, action);
        } else if (type.equals("GoToFitWidth")) {
            ga = new GoToFitWidth(docpanel, pdf, action);
        } else if (type.equals("GoToFitHeight")) {
            ga = new GoToFitHeight(docpanel, pdf, action);
        } else if (type.equals("GoToFitRectangle")) {
            ga = new GoToFitRectangle(docpanel, pdf, action);
        } else if (type.equals("Named:FirstPage")) {
            ga = new GoToNamed(docpanel, pdf, 0);
        } else if (type.equals("Named:LastPage")) {
            ga = new GoToNamed(docpanel, pdf, pdf.getNumberOfPages()-1);
        } else {
            int currentpagenumber = docpanel.getPageNumber();
            if (type.equals("Named:NextPage")) {
                if (currentpagenumber < pdf.getNumberOfPages()-1) {
                    ga = new GoToNamed(docpanel, pdf, currentpagenumber+1);
                }
            } else if (type.equals("Named:PrevPage")) {
                if (currentpagenumber > 0) {
                    ga = new GoToNamed(docpanel, pdf, currentpagenumber-1);
                }
            }
        }

        if (ga != null) {
            docpanel.getLinearizedSupport().invokeOnPageLoadWithDialog(ga.pagenumber, ga);
        }
    }

    private static abstract class GoToAction implements Runnable {

        final DocumentPanel docpanel;
        final PDF pdf;
        final int pagenumber;
        
        PDFPage page;
        float x = Float.NaN, y = Float.NaN, zoom = 0;

        GoToAction(DocumentPanel docpanel, PDF pdf, int pagenumber) {
            this.docpanel = docpanel;
            this.pdf = pdf;
            this.pagenumber = pagenumber;
        }

        public void run() {
            // x and y are relative to mediabox, but setpage wants them
            // relative to Viewbox and inverted - ugh.
            Rectangle2D crop = PagePanel.getFullPageView(page);
            x -= crop.getMinX();
            y = (float)crop.getMaxY() - y;
            docpanel.setPage(page, x, y, zoom);
        }

    }

    private static class GoTo extends GoToAction {

        private PDFAction action;

        GoTo(DocumentPanel docpanel, PDF pdf, PDFAction action) {
            super(docpanel, pdf, action.getPageNumber());
            this.action = action;
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                float[] coords = action.getGoToCoordinates();
                if (coords!=null && coords.length>=3) {
                    x = coords[0];
                    y = coords[1];
                    zoom = coords[2];
                }
                super.run();
            }
        }

    }

    private static class GoToFit extends GoToAction {

        GoToFit(DocumentPanel docpanel, PDF pdf, PDFAction action) {
            super(docpanel, pdf, action.getPageNumber());
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                zoom = docpanel.getViewport().getTargetZoom(DocumentViewport.ZOOM_FIT, page);
                super.run();
            }
        }

    }

    private static class GoToFitWidth extends GoToAction {

        private PDFAction action;

        GoToFitWidth(DocumentPanel docpanel, PDF pdf, PDFAction action) {
            super(docpanel, pdf, action.getPageNumber());
            this.action = action;
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                zoom = docpanel.getViewport().getTargetZoom(DocumentViewport.ZOOM_FITWIDTH, page);
                float[] coords = action.getGoToCoordinates();
                if (coords!=null && coords.length>=1) {
                    y = coords[0];
                }
                super.run();
            }
        }

    }

    private static class GoToFitHeight extends GoToAction {

        private PDFAction action;

        GoToFitHeight(DocumentPanel docpanel, PDF pdf, PDFAction action) {
            super(docpanel, pdf, action.getPageNumber());
            this.action = action;
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                zoom = docpanel.getViewport().getTargetZoom(DocumentViewport.ZOOM_FITHEIGHT, page);
                float[] coords = action.getGoToCoordinates();
                if (coords!=null && coords.length>=1) {
                    x = coords[0];
                }
                super.run();
            }
        }

    }

    private static class GoToFitRectangle extends GoToAction {

        static final int PAD = 4;

        private PDFAction action;

        GoToFitRectangle(DocumentPanel docpanel, PDF pdf, PDFAction action) {
            super(docpanel, pdf, action.getPageNumber());
            this.action = action;
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                float[] coords = action.getGoToCoordinates();
                if (coords != null && coords.length >= 4) {
                    Dimension avail = docpanel.getViewport().getViewportSize();
                    double availw = avail.getWidth() - PAD;
                    double availh = avail.getHeight() - PAD;
                    int dpi = Util.getScreenResolution(docpanel);
                    x = Math.min(coords[0], coords[2]);
                    y = Math.max(coords[1], coords[3]);
                    float w = Math.abs(coords[2] - coords[0]);
                    float h = Math.abs(coords[3] - coords[1]);
                    zoom = (float)Math.min(availw / w, availh / h) / dpi * 72;
                    // x and y are the top right of the rectangle
                }
                super.run();
            }
        }
    
    }
    
    private static class GoToNamed extends GoToAction {

        GoToNamed(DocumentPanel docpanel, PDF pdf, int pagenumber) {
            super(docpanel, pdf, pagenumber);
        }

        public void run() {
            page = pdf.getPage(pagenumber);
            if (page != null) {
                super.run();
            }
        }

    }

}
