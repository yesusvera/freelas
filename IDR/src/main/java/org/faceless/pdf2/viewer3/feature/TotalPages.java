// $Id: TotalPages.java 17372 2013-04-16 10:33:53Z chris $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Creates a {@link JLabel} which displays the total number of pages on the Toolbar.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">TotalPages</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class TotalPages extends NavigationWidget {

    private JLabel totalpages;

    public TotalPages() {
        super("TotalPages");
        totalpages = new JLabel();
        totalpages.setFont(null);
        totalpages.setHorizontalAlignment(JTextField.LEFT);
        totalpages.setText("/       ");
        setComponent("Navigation.ltr", totalpages);
    }

    protected void pageChanged() {
        if (pdf == null) {
            totalpages.setText("/       ");
        } else {
            totalpages.setText("/ "+pdf.getNumberOfPages());
        }
    }

}
