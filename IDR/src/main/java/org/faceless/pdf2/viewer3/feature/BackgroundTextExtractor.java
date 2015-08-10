// $Id: BackgroundTextExtractor.java 20525 2014-12-16 14:47:51Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.PageExtractor;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFBackgroundTask;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * <p>
 * This feature will cause text to be extracted automatically when a PDF is loaded by the viewer.
 * A background thread will be started and run at low priority - early extraction like this means
 * that other features that depend on text extraction (searching, selecting text matching a certain
 * pattern and a "select all" on text - ie {@link SearchPanel} and {@link TextTool}) will not have
 * to run the extraction on demand and so should feel noticably faster to the user.
 * </p><p>
 * Note this feature is not enabled by default - it must be explicitly selected
 * </p>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">BackgroundTextExtractor</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.7
 */
public class BackgroundTextExtractor extends ViewerFeature implements DocumentPanelListener, PDFBackgroundTask
{
    private static final int RUNNING=0, PAUSING=1, PAUSED=2, DONE=3;
    private volatile int state;

    public BackgroundTextExtractor() {
        super("BackgroundTextExtractor");
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        viewer.addDocumentPanelListener(this);
    }

    public boolean isEnabledByDefault() {
        return false;
    }

    public void documentUpdated(DocumentPanelEvent event) {
        if (event.getType()=="loaded") {
            startExtraction(event.getDocumentPanel());
        }
    }

    private synchronized int getMyState() {
        return state;
    }

    private synchronized void setMyState(int state) {
        this.state = state;
        notifyAll();
    }

    public boolean isPaused() {
        return getMyState()==PAUSED;
    }

    public boolean isRunning() {
        return getMyState()!=DONE;
    }

    public synchronized void pause() {
        if (getMyState()==RUNNING) {
            setMyState(PAUSING);
            while (!isPaused()) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
        }
    }

    public synchronized void unpause() {
        if (isPaused()) {
            setMyState(RUNNING);
        }
    }

    /**
     * Start a background thread to run the {@link #extract} method
     * @param docpanel the DocumentPanel
     */
    public void startExtraction(final DocumentPanel docpanel) {
        if (docpanel!=null) {
            Thread thread = new Thread("TextExtractor") {
                public void run() {
                    extract(docpanel);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY + 2);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * This method is run by this feature when a new DocumentPanel is loaded.
     * If the feature is not used, it could also be called externally to start
     * background extraction for the specified panel.
     * @param docpanel the DocumentPanel
     */
    public void extract(DocumentPanel docpanel) {
        PDFParser parser = docpanel.getParser();
        int pagenumber = parser==null ? -1 : parser.getNumberOfPages();
        setMyState(RUNNING);
        while (parser!=null && --pagenumber >= 0 && docpanel.isDisplayable()) {
            PageExtractor extractor = parser.getPageExtractor(pagenumber);
            if (!extractor.isExtracted()) {
                extractor.getTextUnordered();
            }
            synchronized(this) {
                if (getMyState()==PAUSING) {
                    setMyState(PAUSED);
                }
                while (getMyState()==PAUSED) {
                    try {
                        wait();
                    } catch (InterruptedException e) {}
                }
            }
        }
        setMyState(DONE);
    }
}
