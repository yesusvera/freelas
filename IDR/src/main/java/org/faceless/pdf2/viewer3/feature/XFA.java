// $Id: XFA.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.ViewerWidget;

/**
 * A class which suppressed warnings about missing XFA support in the PDF Viewer when
 * JavaScript is supported. In the future this may well form the basis of XFA support
 * in the viewer, but for now it's largely a no-op.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">XFA</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class XFA extends ViewerWidget
{
    public XFA() {
        super("XFA");
    }

    public String getCustomJavaScript(String type, String name) {
        if ("Doc".equals(type) && "Open".equals(name)) {
            return "var ADBE = new Object(); var xfa_installed = true; var xfa_version=2.1;";
        } else {
            return null;
        }
    }
}
