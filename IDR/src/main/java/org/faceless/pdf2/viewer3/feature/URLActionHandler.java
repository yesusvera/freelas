// $Id: URLActionHandler.java 10851 2009-09-08 15:10:31Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Create an action handler for "URL" actions, which will attempt to open a URL in the system web
 * browser.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">URLActionHandler</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.11.1 (from 2.8 to 2.11 this was called URLActionHandler as it worked for applets only)
 */
public class URLActionHandler extends ActionHandler
{
    /**
     * Return a new URLActionHandler
     * @since 2.11
     */
    public URLActionHandler() {
        super("URLActionHandler");
    }

    public boolean matches(DocumentPanel panel, PDFAction action) {
        return action.getType()!=null && action.getType().equals("URL");
    }

    public void run(DocumentPanel panel, PDFAction action) {
        try {
            Util.openURL(new URL(action.getURL()), panel);
        } catch (MalformedURLException e) {
            Util.displayThrowable(e, panel);
        }
    }
}
