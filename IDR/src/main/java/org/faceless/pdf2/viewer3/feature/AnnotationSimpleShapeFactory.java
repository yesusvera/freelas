// $Id: AnnotationSimpleShapeFactory.java 20413 2014-12-02 14:19:54Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.faceless.pdf2.AnnotationShape;
import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.PDFStyle;
import org.faceless.pdf2.viewer3.Util;
import org.faceless.pdf2.viewer3.util.ColorChoicePanel;

abstract class AnnotationSimpleShapeFactory extends AnnotationShapeFactory {

    private String prefprefix;

    public AnnotationSimpleShapeFactory(String name) {
        super(name);
    }

    protected void setPreferencePrefix(String prefix) {
        this.prefprefix = prefix;
    }

    protected JComponent createEditor(final AnnotationShape annot, PDFParser parser) {
        PDFStyle style = annot.getStyle();
        JPanel editorPane = new JPanel();
        editorPane.setLayout(new GridBagLayout());

        addStockDetailsToEditComponent(annot, editorPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        final JTextArea textarea = new JTextArea(5, 20);
        Util.setAutoFocusComponent(textarea);
        textarea.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { }
            public void focusLost(FocusEvent e) {
                annot.setContents(textarea.getText());
            }
        });

        textarea.setEditable(true);
        textarea.setLineWrap(true);
        textarea.setText(annot.getContents());

        editorPane.add(new JScrollPane(textarea), gbc);
        JPanel appearancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        editorPane.add(appearancePanel, gbc);

        BorderStyleEditor borderStyle = new BorderStyleEditor(annot, true);
        borderStyle.setPreferences(getPreferences(), prefprefix+".lineWeight", prefprefix+".dashPattern");
        appearancePanel.add(borderStyle);

        Color color = getLineColor(style);
        JComponent colorEditor = ColorChoicePanel.createColorChoiceButton(color, new ColorChoicePanel.ColorChoiceListener() {
            public void colorChosen(Color color) {
                PDFStyle style = annot.getStyle();
                style.setLineColor(color);
                annot.setStyle(style);
                Preferences preferences = getPreferences();
                if (preferences!=null && prefprefix!=null) {
                    ColorChoicePanel.saveColor(preferences, prefprefix+".lineColor", color);
                }
            }
        }, ColorChoicePanel.TYPE_BORDER, true, true, UIManager.getString("PDFViewer.annot.setColor"));

        Color fillColor = getFillColor(style);
        JComponent fillColorEditor = ColorChoicePanel.createColorChoiceButton(fillColor, new ColorChoicePanel.ColorChoiceListener() {
            public void colorChosen(Color color) {
                PDFStyle style = annot.getStyle();
                style.setFillColor(color);
                annot.setStyle(style);
                Preferences preferences = getPreferences();
                if (preferences!=null && prefprefix!=null) {
                    ColorChoicePanel.saveColor(preferences, prefprefix+".fillColor", color);
                }
            }
        }, ColorChoicePanel.TYPE_RECTANGLE, true, true, UIManager.getString("PDFViewer.annot.setColor"));

        appearancePanel.add(colorEditor);

        JPanel space = new JPanel();
        space.setPreferredSize(new Dimension(2, 20));
        appearancePanel.add(space);

        appearancePanel.add(fillColorEditor);

        return editorPane;
    }
}
