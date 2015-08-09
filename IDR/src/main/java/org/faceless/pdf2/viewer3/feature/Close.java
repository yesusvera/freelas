// $Id: Close.java 10509 2009-07-15 14:55:21Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import javax.swing.*;

/**
 * Create a "File : Close" menu item to close the current document.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">Close</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class Close extends ViewerWidget
{
    public Close() {
        super("Close");
        setMenu("File\tClose", 'w');
    }

    public void action(ViewerEvent event) {
        event.getViewer().closeDocumentPanel(event.getViewer().getActiveDocumentPanel());
    }
}
