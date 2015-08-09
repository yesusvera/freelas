// $Id: RemoteSignatureProvider.java 20861 2015-02-11 10:58:38Z mike $

package org.faceless.pdf2.viewer3.feature;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.faceless.pdf2.*;
import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.viewer3.util.DialogPanel;

/**
 * <p>
 * A SignatureProvider that allows digital signatures to applied using
 * the {@link RemoteSignatureHandlerFactory} class. This factory works together
 * with a {@link RemoteSigningServlet} on a remote host to sign the PDF without needing
 * to manage the {@link java.security.KeyStore} on the client.
 * </p><p>
 * This class needs a {@link RemoteSignatureHandlerFactory Factory} to operate. This
 * can be set by calling {@link #setSignatureHandlerFactory setSignatureHandlerFactory()},
 * or by specifying the URL of the Factory as an initialization parameter (if neither is done
 * then this class is not used).
 * </p>
 *
 *
 * <div class="initparams">
 * The following <a href="../doc-files/initparams.html">initialization parameters</a> can be specified to configure this feature, as well as those parameters specified in the {@link SignatureProvider} API documentation.
 * <table summary="">
 * <tr><th>url</th><td>The URL of the {@link RemoteSigningServlet}</td></tr>
 * <tr><th>digestAlgorithm</th><td>The digest algorithm to be use. The default (defined by the {@link RemoteSignatureHandlerFactory}) is "SHA1", other valid values are "MD5" or "SHA256".</td></tr>
 * </table>
 * </div>
 *
 * <p>
 * Here's a fully working example of how to specify these parameters when the PDF Viewer
 * is installed as an applet. The URL specified is the URL of a demonstration
 * {@link RemoteSigningServlet} which can be used for testing.
 * </p>
 * <pre class="example">
 * &lt;applet code="org.faceless.pdf2.viewer3.PDFViewerApplet" name="pdfapplet" archive="bfopdf.jar"&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.url" value="http://bfo.com/signdemo/" /&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.alias" value="" /&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.name" value="John User" /&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.certification" value="none" /&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.reason" value="" /&gt;
 *  &lt;param name="feature.RemoteSignatureProvider.location" value="" /&gt;
 * &lt;/applet&gt;
 * </pre>
 *
 * <p>
 * In this exampel we're specifying all the parameters required for signing, so no dialog will
 * be presented to the user when signing - in fact, if this is the only {@link SignatureProvider}
 * available to the {@link PDFViewer}, clicking a signature field will immediately sign the field
 * using these parameters without prompting the user for any further information. Note the
 * {@link RemoteSigningServlet} may be set up to ignore and override these values.
 * </p>
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">RemoteSignatureProvider</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 *
 * @since 2.11
 */
public class RemoteSignatureProvider extends SignatureProvider {

    private RemoteSignatureHandlerFactory factory;

    public RemoteSignatureProvider() {
        super("RemoteSignatureProvider");
    }

    public String getDisplayName() {
        return factory==null ? null : "Service at "+factory.getURL();
    }

    public void initialize(PDFViewer viewer) {
        super.initialize(viewer);
        String url = getFeatureProperty(viewer, "url");
        if (url!=null) {
            try {
                setSignatureHandlerFactory(new RemoteSignatureHandlerFactory(new URL(url)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            String digest = getFeatureProperty(viewer, "digestAlgorithm");
            if (digest!=null && factory!=null) {
                factory.setDigestAlgorithm(digest);
            }
        }
    }

    /**
     * Return the default value of "alias" to be passed in to the signing method. Whether this
     * field is used or not depends on how the {@link RemoteSigningServlet} is set up - the
     * default implementation of this method returns the <code>alias</code> initialization
     * parameter for the viewer. If this method returs <code>null</code> then the user will be
     * prompted for a value.
     */
    protected String getDefaultAlias() {
        return getFeatureProperty(getViewer(), "alias");
    }

    /**
     * Set the {@link RemoteSignatureHandlerFactory} that is to be used to sign this
     * PDF. This will override any factory specified by the <code>url</code> property
     * to this feature, as described in the API docs
     */
    public void setSignatureHandlerFactory(RemoteSignatureHandlerFactory factory) {
        this.factory = factory;
    }

    /**
     * Return true if a {@link RemoteSignatureHandlerFactory} has been specified for signing.
     */
    public boolean canSign(FormSignature field) {
        return factory != null;
    }

    /**
     * Signatures do not need to be verified remotely, so this method always returns false.
     */
    public boolean canVerify(FormSignature field) {
        return false;
    }

    public void showSignDialog(JComponent root, FormSignature field) throws IOException, GeneralSecurityException {
        DialogPanel panel = new DialogPanel();
        String alias = getDefaultAlias();
        String name = getDefaultName();
        String reason = getDefaultReason();
        String location = getDefaultLocation();
        int certificationType = getDefaultCertificationType();

        JTextField aliasfield = new JTextField(alias);
        JTextField namefield = new JTextField(name);
        JTextField reasonfield = new JTextField(reason);
        JTextField locationfield = new JTextField(location);
        JComboBox<String> certificationfield = null;

        if (alias!=null) {
            aliasfield.setEditable(false);
        }
        if (name!=null) {
            namefield.setEditable(false);
        }
        if (reason!=null) {
            reasonfield.setEditable(false);
        }
        if (location!=null) {
            locationfield.setEditable(false);
        }

        if (field.getForm().getPDF().getBasicOutputProfile().isSet(OutputProfile.Feature.DigitallySigned)) {
            certificationType = FormSignature.CERTIFICATION_UNCERTIFIED;
        }
        if (certificationType==-1) {
            String[] values = {
                UIManager.getString("PDFViewer.cert.Uncertified"),
                UIManager.getString("PDFViewer.cert.NoChanges"),
                UIManager.getString("PDFViewer.cert.ModifyForms"),
                UIManager.getString("PDFViewer.cert.ModifyComments"),
            };
            certificationfield = new JComboBox<String>(values);
        }

        panel.addComponent("Alias", aliasfield);
        panel.addComponent("Name", namefield);
        panel.addComponent("Reason", reasonfield);
        panel.addComponent("Location", locationfield);
        if (certificationfield!=null) {
            panel.addComponent("Certification", certificationfield);
        }

        if ((alias != null && name != null && reason != null && location != null && certificationType != -1) || panel.showDialog(root)) {
            if (aliasfield!=null) {
                alias = aliasfield.getText();
            }
            if (namefield!=null) {
                name = namefield.getText();
            }
            if (reasonfield!=null) {
                reason = reasonfield.getText();
            }
            if (locationfield!=null) {
                try {
                    location = locationfield.getText();
                } catch (Exception e) {}
            }
            if (certificationfield!=null) {
                certificationType = certificationfield.getSelectedIndex();
            }
            field.sign(null, alias, null, factory);
            field.setCertificationType(certificationType, null);

            if (reason.length()>0) {
                try {
                    field.setReason(reason);
                } catch (Exception e) {}
            }
            if (location.length()>0) {
                try {
                    field.setLocation(location);
                } catch (Exception e) {}
            }
            if (name.length()==0) {
                SignatureHandler signatureHandler = field.getSignatureHandler();
                name = signatureHandler.getDefaultName();
            }
            if (name!=null && name.length()>0) {
                try {
                    field.setName(name);
                } catch (Exception e) {}
            }
        }
    }

    /**
     * As the {@link #canVerify canVerify()} method always returns false, this method
     * does nothing.
     */
    public void showVerifyDialog(JComponent root, FormSignature field) {
        // Never called
    }
}

