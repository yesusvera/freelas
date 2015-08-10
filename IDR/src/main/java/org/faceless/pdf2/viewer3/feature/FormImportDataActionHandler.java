// $Id: FormImportDataActionHandler.java 19623 2014-07-11 15:17:50Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.faceless.pdf2.FDF;
import org.faceless.pdf2.PDFAction;
import org.faceless.pdf2.viewer3.ActionHandler;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.Util;

/**
 * Create an action handler to deal with "FormImportData" {@link PDFAction}.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">FormImportDataActionHandler</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class FormImportDataActionHandler extends ActionHandler {

    /**
     * Create a new FormImportDataActionHandler
     * @since 2.11
     */
    public FormImportDataActionHandler() {
        super("FormImportDataActionHandler");
    }

    public boolean matches(DocumentPanel panel, PDFAction action) {
        return action.getType().equals("FormImportData");
    }

    public void run(final DocumentPanel docpanel, final PDFAction action) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                InputStream in = null;
                try {
                    URL url = Util.toURL(docpanel, action.getURL());
                    in = url.openConnection().getInputStream();
                    FDF fdf = new FDF(in);
                    docpanel.getPDF().importFDF(fdf);
                } catch (Exception e) {
                    Util.displayThrowable(e, docpanel.getViewer());
                } finally {
                    if (in != null) try { in.close(); } catch (IOException e) {}
                }
                return null;
            }
        });
    }

}
