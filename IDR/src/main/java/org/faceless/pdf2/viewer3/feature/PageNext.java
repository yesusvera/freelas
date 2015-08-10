// $Id: PageNext.java 17825 2013-07-09 17:13:40Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import org.faceless.pdf2.PDFPage;
import org.faceless.pdf2.viewer3.DocumentViewport;

/**
 * Create a button to jump to the next page.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PageNext</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class PageNext extends NavigationWidget {

    private AbstractAction action;

    public PageNext() {
        super("PageNext");
        setButton("Navigation.ltr", "resources/icons/NextPage.png", "PDFViewer.tt.PageNext");
        setMenu("View\tGoTo\tNextPage");
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                DocumentViewport viewport = docpanel.getViewport();
                PDFPage page = viewport.getRenderingPage();
                if (page != null) {
                    int index = viewport.getNextSelectablePageIndex(page);
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
            action.setEnabled(page != null && viewport.getNextSelectablePageIndex(page) >= 0);
        }
    }

}
