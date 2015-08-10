// $Id: BackgroundSignatureVerifier.java 19623 2014-07-11 15:17:50Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import org.faceless.pdf2.FormElement;
import org.faceless.pdf2.FormSignature;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.PDFBackgroundTask;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.SignatureProvider;
import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * This feature will cause signatures in the PDF to be verified automatically when a PDF is loaded
 * by the viewer, using a thread that runs transparently in the background.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">BackgroundSignatureVerifier</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.7
 */
public class BackgroundSignatureVerifier extends ViewerFeature implements DocumentPanelListener, PDFBackgroundTask
{
    private static final int RUNNING=0, PAUSING=1, PAUSED=2, DONE=3;
    private volatile int state;

    public BackgroundSignatureVerifier() {
        super("BackgroundSignatureVerifier");
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
            DocumentPanel docpanel = event.getDocumentPanel();
            startVerification(docpanel, docpanel.getPDF().getForm().getElements().values());
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
     * Start a background thread that runs the {@link #verify verify()} method
     * @param docpanel the DocumentPanel containing the signatures being verified
     * @param fields a Collection containing the fields to verify
     */
    public void startVerification(final DocumentPanel docpanel, final Collection<? extends FormElement> fields) {
        if (docpanel!=null) {
            Thread thread = new Thread("SignatureVerifier") {
                public void run() {
                    verify(docpanel, fields);
                }
            };
            thread.setPriority(Thread.MIN_PRIORITY + 2);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Verify the specified collection of fields. Fields will be verified if
     * only one {@link SignatureProvider} can be found that {@link SignatureProvider#canVerify canVerify()}
     * them.
     * @param docpanel the DocumentPanel containing the signatures being verified
     * @param fields a Collection containing the fields to verify. Only signed {@link FormSignature}
     * fields from this Collection will be checked, so it's safe to pass
     * <code>pdf.getForm().getElements().values()</code> to check all fields
     */
    public void verify(final DocumentPanel docpanel, final Collection<? extends FormElement> fields) {
        setMyState(RUNNING);
        List<FormSignature> copy = new ArrayList<FormSignature>();
        for (Iterator<? extends FormElement> i = fields.iterator();i.hasNext() && docpanel.isDisplayable();) {
            FormElement o = i.next();
            if (o instanceof FormSignature && ((FormSignature)o).getState() == FormSignature.STATE_SIGNED) {
                copy.add((FormSignature)o);
            }
        }
        for (Iterator<FormSignature> i = copy.iterator();i.hasNext() && docpanel.isDisplayable();) {
            final FormSignature field = i.next();
            SignatureProvider.SignatureState state = SignatureProvider.getSignatureState(docpanel, field);
            if (state == null) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            SignatureProvider.selectVerifyProvider(docpanel, field, null, null, new ActionListener() {
                                public void actionPerformed(ActionEvent event) {
                                    SignatureProvider provider = (SignatureProvider)event.getSource();
                                    SignatureProvider.SignatureState state = provider.verify(docpanel, field);
                                    SignatureProvider.setSignatureState(docpanel, field, state);
                                }
                            });
                        }
                    });
                } catch (Exception e) { }
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
        docpanel.repaint();
    }
}
