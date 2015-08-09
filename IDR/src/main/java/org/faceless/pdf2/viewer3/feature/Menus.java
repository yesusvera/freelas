// $Id: Menus.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;

/**
 * A special feature to enable the Menubar.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Menus</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public final class Menus extends ViewerFeature
{
    private static Menus instance;

    /**
     * Return the singleton instance of this class
     */
    public static ViewerFeature getInstance() {
        if (instance==null) instance = new Menus();
        return instance;
    }

    private Menus() {
        super("Menus");
    }
}
