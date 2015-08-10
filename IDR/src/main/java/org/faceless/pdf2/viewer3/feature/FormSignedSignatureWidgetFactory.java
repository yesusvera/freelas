// $Id: FormSignedSignatureWidgetFactory.java 10851 2009-09-08 15:10:31Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.faceless.pdf2.Event;
import org.faceless.pdf2.FormSignature;
import org.faceless.pdf2.PDFAnnotation;
import org.faceless.pdf2.WidgetAnnotation;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.PagePanel;
import org.faceless.pdf2.viewer3.SignatureProvider;

/**
 * Create annotations to handle {@link WidgetAnnotation} objects belonging to signed
 * {@link FormSignature} fields. When an annotation created by this field is clicked on,
 * a {@link SignatureProvider} wil be chosen to verify the field and that objects
 * {@link SignatureProvider#showVerifyDialog showVerifyDialog()} method called.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">FormSignedSignatureWidgetFactory</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8, with much of the functionality moved to {@link SignatureProvider} in 2.11
 */
public class FormSignedSignatureWidgetFactory extends WidgetComponentFactory
{
    /**
     * Create a new FormSignedSignatureWidgetFactory that verifies
     * against the default KeyStore.
     */
    public FormSignedSignatureWidgetFactory() {
        super("FormSignedSignatureWidgetFactory");
    }

    public boolean matches(PDFAnnotation annot) {
        return annot instanceof WidgetAnnotation && ((WidgetAnnotation)annot).getField() instanceof FormSignature && ((FormSignature)((WidgetAnnotation)annot).getField()).getState()==FormSignature.STATE_SIGNED;
    }

    protected void paintComponentAnnotations(JComponent comp, Graphics g) {
         super.paintComponentAnnotations(comp, g);
         WidgetAnnotation annot = (WidgetAnnotation)comp.getClientProperty("pdf.annotation");
         DocumentPanel docpanel = (DocumentPanel)SwingUtilities.getAncestorOfClass(DocumentPanel.class, comp);
         ImageIcon icon = SignatureProvider.getIcon(docpanel, (FormSignature)annot.getField());
         icon.paintIcon(comp, g, 0, 0);
    }

    public JComponent createComponent(final PagePanel pagepanel, PDFAnnotation annot) {
        final WidgetAnnotation widget = (WidgetAnnotation)annot;
        final DocumentPanel docpanel = pagepanel.getDocumentPanel();
        final FormSignature field = (FormSignature)widget.getField();
        final JComponent comp = createComponent(pagepanel, annot, null);

        comp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                SignatureProvider.selectVerifyProvider(docpanel, field, comp, event.getPoint(), new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        SignatureProvider ssp = (SignatureProvider)event.getSource();
                        verify(field, docpanel, ssp);
                        comp.repaint();
                    }
                });
            }
        });

        comp.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                comp.repaint();
                docpanel.runAction(widget.getAction(Event.FOCUS));
            }
            public void focusLost(FocusEvent event) {
                if (comp.isValid()) {
                    comp.repaint();
                    docpanel.runAction(widget.getAction(Event.BLUR));
                }
            }
        });
        return comp;
    }

    /**
     * Verify the signature field, by calling the
     * {@link SignatureProvider#showVerifyDialog showVerifyDialog()} method on the
     * specified SignatureProvider
     * @param field the signed Signature field to verify
     * @param docpanel the DocumentPanel
     * @param provider the SignatureProvider to use to verify the signature
     */
    public void verify(FormSignature field, DocumentPanel docpanel, SignatureProvider provider) {
        provider.showVerifyDialog(docpanel, field);
    }
}
