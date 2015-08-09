// $Id: PagePrevious.java 17825 2013-07-09 17:13:40Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.PDF;
import java.awt.event.*;
import javax.swing.*;

/**
 * Create a button that jumps to the previous page.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PagePrevious</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class PagePrevious extends NavigationWidget {

    private AbstractAction action;

    public PagePrevious() {
        super("PagePrevious");
        setButton("Navigation.ltr", "resources/icons/PreviousPage.png", "PDFViewer.tt.PagePrevious");
        setMenu("View\tGoTo\tPreviousPage");
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                DocumentViewport viewport = docpanel.getViewport();
                PDFPage page = viewport.getRenderingPage();
                if (page != null) {
                    int index = viewport.getPreviousSelectablePageIndex(page);
                    if (index >= 0) {
                        docpanel.setPage(page.getPDF().getPage(index));
                    }
                }
            }
        };
    }

    public ActionListener createActionListener() {
        return action;
    }

    protected void pageChanged() {
        if (pdf == null) {
            action.setEnabled(false);
        } else {
            DocumentViewport viewport = docpanel.getViewport();
            PDFPage page = viewport.getRenderingPage();
            action.setEnabled(page != null && viewport.getPreviousSelectablePageIndex(page) >= 0);
        }
    }

}
