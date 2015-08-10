// $Id: ShowHideActionHandler.java 16472 2012-10-23 16:41:41Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.PDFAction;
import org.faceless.pdf2.WidgetAnnotation;
import org.faceless.pdf2.viewer3.ActionHandler;
import org.faceless.pdf2.viewer3.DocumentPanel;

/**
 * Handles the "ShowWidget" and "HideWidget" types of {@link PDFAction}.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ShowHideActionHandler</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class ShowHideActionHandler extends ActionHandler {

    /**
     * Create a new ShowHideActionHandler
     * @since 2.11
     */
    public ShowHideActionHandler() {
        super("ShowHideActionHandler");
    }

    public boolean matches(DocumentPanel panel, PDFAction action) {
        String type = action.getType();
        return type.equals("ShowWidget") || type.equals("HideWidget");
    }

    public void run(DocumentPanel docpanel, PDFAction action) {
        WidgetAnnotation annot = action.getAnnotation();
        annot.setVisible(action.getType().equals("ShowWidget"));
    }
}
