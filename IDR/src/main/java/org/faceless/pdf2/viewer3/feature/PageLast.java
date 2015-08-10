// $Id: PageLast.java 17586 2013-05-10 09:48:53Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

/**
 * Create a button to jump to the last page.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">PageLast</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class PageLast extends NavigationWidget {

    private AbstractAction action;

    public PageLast() {
        super("PageLast");
        setButton("Navigation.ltr", "resources/icons/LastPage.png", "PDFViewer.tt.PageLast");
        setMenu("View\tGoTo\tLastPage");
        action = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                docpanel.setPageNumber(pdf.getNumberOfPages() - 1);
            }
        };
    }

    public ActionListener createActionListener() {
        return action;
    }

    protected void pageChanged() {
        action.setEnabled(pdf != null && docpanel.getPageNumber() < pdf.getNumberOfPages() - 1);
    }

}
