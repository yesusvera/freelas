// $Id: SidePanelFactory.java 10479 2009-07-10 09:51:07Z chris $

package org.faceless.pdf2.viewer3;

import org.faceless.pdf2.*;

/**
 * A type of ViewerFeature that creates {@link SidePanel} objects.
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public abstract class SidePanelFactory extends ViewerFeature
{
    /**
     * Create a new SidePanelFactory.
     * @param name the official name of the feature
     */
    public SidePanelFactory(String name) {
        super(name);
    }

    public String toString() {
        return "SidePanelFactory:"+super.toString();
    }

    /**
     * Determines whether this side panel is required for this PDF.
     * By default this method returns true, but an example of where this
     * wouldn't apply is for the Bookmarks panel on a PDF with no
     * bookmarks.
     */
    public boolean isSidePanelRequired(PDF pdf) {
        return true;
    }

    /**
     * Create and return a new {@link SidePanel}.
     */
    public abstract SidePanel createSidePanel();
}
