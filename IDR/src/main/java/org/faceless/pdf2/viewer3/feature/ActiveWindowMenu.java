// $Id: ActiveWindowMenu.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;

/**
 * A special feature to enable the list of open windows under the "Window" menu.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ActiveWindowMenu</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.1
 */
public final class ActiveWindowMenu extends ViewerFeature
{
    private static ActiveWindowMenu instance;

    /**
     * Return the singleton instance of this class
     */
    public static ViewerFeature getInstance() {
        if (instance==null) instance = new ActiveWindowMenu();
        return instance;
    }

    private ActiveWindowMenu() {
        super("ActiveWindowMenu");
    }
}
