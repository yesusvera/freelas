// $Id: ToolbarDisabling.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.ViewerFeature;

/**
 * A special feature that creates a menu option allowing the user to enable or disable toolbars.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">ToolbarDisabling</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public final class ToolbarDisabling extends ViewerFeature
{
    private static ToolbarDisabling instance;

    /**
     * Return the singleton instance of this class
     */
    public static ViewerFeature getInstance() {
        if (instance==null) instance = new ToolbarDisabling();
        return instance;
    }

    private ToolbarDisabling() {
        super("ToolbarDisabling");
    }
}
