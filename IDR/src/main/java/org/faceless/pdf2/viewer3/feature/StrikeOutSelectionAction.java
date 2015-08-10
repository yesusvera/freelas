// $Id: StrikeOutSelectionAction.java 15769 2012-06-07 14:10:17Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Color;

import org.faceless.pdf2.AnnotationMarkup;

/**
 * A {@link MarkupSelectionAction} that will create an StrikeOut
 * {@link AnnotationMarkup} on the selected text.
 * <span class="featurename">The name of this feature is <span class="featureactualname">StrikeOutSelectionAction</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 *
 * @since 2.11.7
 */
public class StrikeOutSelectionAction extends MarkupSelectionAction {

    public StrikeOutSelectionAction() {
        super("StrikeOutSelectionAction");
        setType("StrikeOut");
        setColor(Color.red);
        setDescription("PDFViewer.annot.StrikeOut");
    }

}
