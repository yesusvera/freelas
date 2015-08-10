// $Id: SignatureProvider.java 20861 2015-02-11 10:58:38Z mike $

package org.faceless.pdf2.viewer3;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.faceless.pdf2.FormSignature;

/**
 * <p>
 * A ViewerFeature that acts as a service provider for applying Digital Signatures.
 * When a digital signature field is encounted by the PDF viewer, it will search
 * its list of features for an instance of this class that {@link #canSign can sign}
 * or {@link #canVerify can verify} the field as appropriate. A dialog will then
 * be presented.
 * </p><p>
 * Although each type of subclass will be different, there are several properties
 * that apply to any digital signature and so they can be specified here. Subclasses
 * implementing the {@link #showSignDialog showSignDialog()} method are expected to
 * use these values if specified, or prompt the user otherwise.
 * </p>
 * <div class="initparams">
 * The following <a href="doc-files/initparams.html">initialization parameters</a> can be specified to configure subclasses of this feature.
 * <table summary="">
 * <tr><th>name</th><td>The name of the entity signing the document - the default value of {@link #getDefaultName}</td></tr>
 * <tr><th>reason</th><td>The reason for the signature - the default value of {@link #getDefaultReason}.</td></tr>
 * <tr><th>location</th><td>The location the signature is being applied - the default value of {@link #getDefaultLocation}.</td></tr>
 * <tr><th>certification</th><td>The type of certification to use for the first signature applied to a PDF (the default value of {@link #getDefaultCertificationType}). Valid values are {@link FormSignature#CERTIFICATION_UNCERTIFIED none}, {@link FormSignature#CERTIFICATION_NOCHANGES nochanges}, {@link FormSignature#CERTIFICATION_ALLOWFORMS forms} or {@link FormSignature#CERTIFICATION_ALLOWCOMMENTS comments}. If the signature being applied is not the initial signature this is ignored</td></tr>
 * </table>
 * </div>
 *
 * @since 2.11
 */
public abstract class SignatureProvider extends ViewerFeature {

    private PDFViewer viewer;

    protected SignatureProvider(String name) {
        super(name);
    }

    public void initialize(PDFViewer viewer) {
        this.viewer = viewer;
        super.initialize(viewer);
    }

    /**
     * Return the {@link PDFViewer} set in {@link #initialize}
     */
    public final PDFViewer getViewer() {
        return viewer;
    }

    /**
     * Return the "user friendly" name of this SignatureProvider,
     * to use in dialogs and menus.
     */
    public abstract String getDisplayName();

    /**
     * Return the name of the entity signing the document
     * using the {@link SignatureProvider#showSignDialog showSignDialog()} method,
     * or <code>null</code> to not specify a default.
     */
    public String getDefaultName() {
        return getFeatureProperty(viewer, "name");
    }

    /**
     * Return the reason that the new signature is being applied
     * using the {@link SignatureProvider#showSignDialog showSignDialog()} method,
     * or <code>null</code> to not specify a default.
     */
    public String getDefaultReason() {
        return getFeatureProperty(viewer, "reason");
    }

    /**
     * Return the location of the new signature being applied
     * using the {@link SignatureProvider#showSignDialog showSignDialog()} method,
     * or <code>null</code> to not specify a default.
     */
    public String getDefaultLocation() {
        return getFeatureProperty(viewer, "location");
    }

    /**
     * Return the default type of certification for any new signatures
     * using the {@link SignatureProvider#showSignDialog showSignDialog()} method,
     * or -1 to not specify a default.
     * @return one of {@link FormSignature#CERTIFICATION_UNCERTIFIED}, {@link FormSignature#CERTIFICATION_NOCHANGES}, {@link FormSignature#CERTIFICATION_ALLOWFORMS}, {@link FormSignature#CERTIFICATION_ALLOWCOMMENTS}, or the value -1 to prompt the user (the default).
     */
    public int getDefaultCertificationType() {
        String certtype = getFeatureProperty(viewer, "certification");
        if ("none".equals(certtype)) {
            return FormSignature.CERTIFICATION_UNCERTIFIED;
        } else if ("nochanges".equals(certtype)) {
            return FormSignature.CERTIFICATION_NOCHANGES;
        } else if ("forms".equals(certtype)) {
            return FormSignature.CERTIFICATION_ALLOWFORMS;
        } else if ("annotations".equals(certtype)) {
            return FormSignature.CERTIFICATION_ALLOWCOMMENTS;
        } else {
            return -1;
        }
    }

    /**
     * Return true if this SignatureProvider can sign the specified field
     */
    public abstract boolean canSign(FormSignature field);

    /**
     * Return true if this SignatureProvider can verify the specified field
     */
    public abstract boolean canVerify(FormSignature field);

    /**
     * Display the signing dialog for the specified field, and assuming all goes well
     * sign the field at the end.
     * @param root the JCompoment the dialog should be relative to - typically this is the {@link DocumentPanel}
     * @param field the field to be signed
     */
    public abstract void showSignDialog(JComponent root, FormSignature field) throws IOException, GeneralSecurityException;

    /**
     * Show a dialog displaying information about the specified (signed) digital signature field.
     * The dialog should display the signatures verification state, which may be determined by this
     * method or retrieved from a previous verification
     * @param root the JCompoment the dialog should be relative to - typically this is the {@link DocumentPanel}
     * @param field the field to be verified
     */
    public abstract void showVerifyDialog(JComponent root, FormSignature field);

    /**
     * Verify the field. Must be overridden by any SignatureProvider that
     * returns true from {@link #canVerify canVerify()}. This method may
     * provide visual feedback to the user, but it's primary purpose is
     * to verify the field and return its state so it should not block
     * user progress unless it's unavoidable.
     *
     * @param root the component that should be used as a root for
     * @param field the signed field
     * @since 2.11.7
     */
    public SignatureState verify(JComponent root, FormSignature field) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get an Icon that can be used to describe the specified signature.
     */
    public static final ImageIcon getIcon(DocumentPanel docpanel, FormSignature field) {
        SignatureState state = getSignatureState(docpanel, field);
        if (state==null) {
            return new ImageIcon(PDFViewer.class.getResource("resources/icons/help.png"));
        } else {
            return state.getIcon();
        }
    }

    private static final void selectProvider(final DocumentPanel docpanel, final FormSignature field, final JComponent comp, final Point point, final ActionListener listener, final boolean sign) {
        if (field.getState()==FormSignature.STATE_PENDING) {
            JOptionPane.showMessageDialog(docpanel, UIManager.getString("PDFViewer.PendingSignature"), UIManager.getString("PDFViewer.Alert"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPopupMenu providermenu = new JPopupMenu();
        ViewerFeature[] features = docpanel.getViewer().getFeatures();
        int found = 0;
        SignatureProvider lastssp = null;
        for (int i = 0; i < features.length; i++) {
            Object feature = features[i];
            if (feature instanceof SignatureProvider) {
                final SignatureProvider ssp = (SignatureProvider)feature;
                if (sign ? ssp.canSign(field) : ssp.canVerify(field)) {
                    lastssp = ssp;
                    found++;
                    Action action = new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            listener.actionPerformed(new ActionEvent(ssp, e.getID(), sign ? "sign" : "verify"));
                        }
                    };
                    action.putValue(Action.NAME, ssp.getDisplayName());
                    providermenu.add(new JMenuItem(action));
                }
            }
        }
        if (found==0) {
            JOptionPane.showMessageDialog(docpanel, UIManager.getString("PDFViewer.NoSignatureProvider"), UIManager.getString("PDFViewer.Alert"), JOptionPane.INFORMATION_MESSAGE);
        } else if (found==1) {
            listener.actionPerformed(new ActionEvent(lastssp, 0, sign ? "sign" : "verify"));
        } else if (point!=null) {
            providermenu.show(comp, point.x, point.y);
        }
    }

    /**
     * <p>
     * Select a SignatureProvider that can be used to sign the specified signature field.
     * The <code>listener</code> parameter specifies an {@link ActionListener} which will be called with
     * the chosen provider - the <code>ActionEvent</code> it will be given will have the source set to
     * the chosen provider and the action type set to "sign".
     * </p><p>
     * If more than one SignatureProvider is available this method will show a dialog allowing the user to choose,
     * otherwise the the <code>listener</code> will be called without a dialog being displayed.
     * </p><p>
     * @param docpanel the DocumentPanel containing the PDF
     * @param field the field the user is requesting to sign
     * @param comp the Component the user has clicked on or selected to request the signing
     * @param point the position relative to <code>comp</code> that any dialog should be based around
     * @param listener the ActionListener that should be called when the SignatureProvider is chosen
     */
    public static final void selectSignProvider(final DocumentPanel docpanel, final FormSignature field, final JComponent comp, final Point point, final ActionListener listener) {
        selectProvider(docpanel, field, comp, point, listener, true);
    }

    /**
     * <p>
     * Select a SignatureProvider that can be used to verify the specified signature field.
     * The <code>listener</code> parameter specifies an {@link ActionListener} which will be called with
     * the chosen provider - the {@link ActionEvent} it will be given will have the source set to the chosen
     * provider and the action type set to "verify".
     * </p><p>
     * If more than one SignatureProvider is available this method will show a dialog allowing the user to choose,
     * otherwise the the <code>listener</code> will be called without a dialog being displayed.
     * </p><p>
     * @param docpanel the DocumentPanel containing the PDF
     * @param field the field the user is requesting to verify
     * @param comp the Component the user has clicked on or selected to request the verification
     * @param point the position relative to <code>comp</code> that any dialog should be based around
     * @param listener the ActionListener that should be called when the SignatureProvider is chosen
     */
    public static final void selectVerifyProvider(final DocumentPanel docpanel, final FormSignature field, final JComponent comp, final Point point, final ActionListener listener) {
        selectProvider(docpanel, field, comp, point, listener, false);
    }


    //--------------------------------------------------------------------------------------------

    /**
     * A SignatureState contains information about a {@link FormSignature} once it's been verified.
     * This is used to display information about the signatures in the dialog displayed
     * by {@link #showVerifyDialog showVerifyDialog()}, and to determine which Icon to display on
     * any visual representation of the Signature in the PDF (see {@link SignatureProvider#getIcon}).
     * Subclasses of SignatureState may extend this class to store additional information if necessary.
     */
    public class SignatureState {
        private final FormSignature sig;
        private Boolean validity;
        private String reason;
        private boolean alteredsince;
        private Exception exception;

        /**
         * Create a new SignatureState
         * @param sig the signature
         * @param validity {@link Boolean#TRUE}, {@link Boolean#FALSE} or null to indicate the signature is valid, invalid or hasn't be validated
         * @param reason the reason for signing
         * @param alteredsince whether the PDF has been altered since the signature was applied
         * @param exception the exception encountered during validation, or null if it succeeded
         */
        public SignatureState(FormSignature sig, Boolean validity, String reason, boolean alteredsince, Exception exception) {
            this.validity = validity;
            this.reason = reason;
            this.alteredsince = alteredsince;
            this.exception = exception;
            this.sig = sig;
        }

        /**
         * Return the validity of the Signature. {@link Boolean#TRUE} for valid, {@link Boolean#FALSE} for invalid
         * or <code>null</code> for unknown validity.
         */
        public Boolean getValidity() {
            return validity;
        }

        /**
         * Return the descriptive text describing this state
         */
        public String getReason() {
            return reason;
        }

        /**
         * Return true of the PDF has been altered since the signature was applied.
         * Only useful if {@link #getValidity} returns True.
         */
        public boolean isAlteredSince() {
            return alteredsince;
        }

        /**
         * Return the Exception that occurred when trying to verify the signature or
         * certificate, or <code>null</code> if none was thrown.
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Return the signature itself
         */
        public FormSignature getSignature() {
            return sig;
        }

        /**
         * Return the {@link SignatureProvider} that verified the Signature and
         * created this SignatureState object.
         */
        public SignatureProvider getSignatureProvider() {
            return SignatureProvider.this;
        }

        /**
         * Return an {@link Icon} that visually represents the state of the signature. This
         * will be displayed in the {@link DocumentPanel} and in the dialog displayed by
         * {@link #showVerifyDialog showVerifyDialog()}
         */
        public ImageIcon getIcon() {
            ImageIcon icon;
            if (getValidity()==null) {
                icon = new ImageIcon(PDFViewer.class.getResource("resources/icons/help.png"));
            } else if (getValidity().booleanValue()==false) {
                icon = new ImageIcon(PDFViewer.class.getResource("resources/icons/cross.png"));
            } else {
                int type = getSignature().getCertificationType();
                if (type==FormSignature.CERTIFICATION_UNCERTIFIED) {
                    icon = new ImageIcon(PDFViewer.class.getResource("resources/icons/tick.png"));
                } else {
                    icon = new ImageIcon(PDFViewer.class.getResource("resources/icons/rosette.png"));
                }
            }
            return icon;
        }
    }

    /**
     * Get a previously determined {@link SignatureState} for the specified signature field, as set by
     * {@link #setSignatureState setSignatureState()}. If this method returns
     * <code>null</code> then the signature has not been verified yet.
     *
     * @param docpanel the DocumentPanel containing the signature
     * @param field the FormSignature whose state is being checked
     * @since 2.11.7
     */
    public static final SignatureState getSignatureState(final DocumentPanel docpanel, final FormSignature field) {
        final SignatureState[] stateholder = new SignatureState[1];
        Runnable r = new Runnable() {
            public void run() {
                stateholder[0] = (SignatureProvider.SignatureState)docpanel.getClientProperty(field);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InvocationTargetException e) {
                throw (RuntimeException)e.getTargetException();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return stateholder[0];
    }

    /**
     * Set the {@link SignatureState} of this field - should be called by the {@link #showVerifyDialog showVerifyDialog()}
     * method after the field has been verified, to save the details of the verification.
     * This method may be called in any thread, but it will fire the "stateChanged"
     * {@link DocumentPanelEvent} on the Swing Event Dispatch Thread.
     *
     * @param docpanel the DocumentPanel containing the signature
     * @param field the FormSignature that was verified
     * @param state the state of the signature
     */
    public static final void setSignatureState(final DocumentPanel docpanel, final FormSignature field, final SignatureState state) {
        Runnable r = new Runnable() {
            public void run() {
                docpanel.putClientProperty(field, state);
                docpanel.raiseDocumentPanelEvent(DocumentPanelEvent.createStateChanged(docpanel, state));
                docpanel.repaint();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InvocationTargetException e) {
                throw (RuntimeException)e.getTargetException();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
