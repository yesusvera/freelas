// $Id: FormTextWidgetFactory.java 21181 2015-03-31 14:43:48Z mike $

package org.faceless.pdf2.viewer3.feature;

import org.faceless.pdf2.viewer3.*;
import org.faceless.pdf2.*;
import org.faceless.pdf2.Event;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * Create annotations to handle {@link WidgetAnnotation} objects belonging to a {@link FormText}.
 *
 * <span class="featurename">The name of this feature is <span class="featureactualname">FormTextWidgetFactory</span></span>
 * <p><i>This code is copyright the Big Faceless Organization. You're welcome to use, modify and distribute it in any form in your own projects, provided those projects continue to make use of the Big Faceless PDF library.</i></p>
 * @since 2.8
 */
public class FormTextWidgetFactory extends WidgetComponentFactory {

    /**
     * Create a new FormTextWidgetFactory.
     * @since 2.10.6
     */
    public FormTextWidgetFactory() {
        super("FormTextWidgetFactory");
    }

    public boolean matches(PDFAnnotation annot) {
        return annot instanceof WidgetAnnotation && ((WidgetAnnotation)annot).getField() instanceof FormText;
    }

    public JComponent createComponent(final PagePanel pagepanel, PDFAnnotation annot) {
        final WidgetAnnotation widget = (WidgetAnnotation)annot;
        final FormText field = (FormText)widget.getField();
        final DocumentPanel docpanel = pagepanel.getDocumentPanel();

        final JTextComponent comp;
        int type = field.getType();
        if (type==FormText.TYPE_MULTILINE) {
            comp = (JTextComponent)createComponent(pagepanel, annot, JTextArea.class);
            ((JTextArea)comp).setLineWrap(true);
        } else if (type==FormText.TYPE_PASSWORD) {
            comp = (JTextComponent)createComponent(pagepanel, annot, JPasswordField.class);
        } else {
            comp = (JTextComponent)createComponent(pagepanel, annot, JTextField.class);
        }
        comp.setText(field.getValue());

        comp.setDropTarget(new DropTarget() {
            public void drop(DropTargetDropEvent event) {
                Transferable transfer = event.getTransferable();
                DataFlavor textflavor = DataFlavor.stringFlavor;
                if (transfer.isDataFlavorSupported(textflavor)) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    try {
                        BufferedReader reader = new BufferedReader(textflavor.getReaderForText(transfer));
                        String s;
                        if (field.getType() == FormText.TYPE_MULTILINE) {
                            StringBuilder sb = new StringBuilder();
                            while ((s = reader.readLine()) != null) {
                                sb.append(s);
                                sb.append("\n");
                            }
                            s = sb.toString();
                        } else {
                            s = reader.readLine();
                        }
                        field.setValue(s);
                    } catch (Exception e) {
                        Util.displayThrowable(e, docpanel);
                    }
                } else {
                    event.rejectDrop();
                }
            }
        });

        Listener listener = new Listener(widget, comp, docpanel);
        if (comp instanceof JTextField) {
            ((JTextField)comp).addActionListener(listener);
        }
        comp.addFocusListener(listener);
        ((AbstractDocument)comp.getDocument()).setDocumentFilter(listener);
        return comp;
    }

    // Filter that ensures the columns are adhered to and handles Keystroke events
    // Run before the change is applied
    //
    private static class Listener extends DocumentFilter implements FocusListener, ActionListener {
        private final AbstractDocument document;
        private final FormText field;
        private final WidgetAnnotation widget;
        private final DocumentPanel docpanel;
        private final JTextComponent comp;
        private final JSManager js;
        private final int columns;

        Listener(WidgetAnnotation widget, JTextComponent comp, DocumentPanel docpanel) {
            this.widget = widget;
            this.field = (FormText)widget.getField();
            this.docpanel = docpanel;
            this.js = docpanel.getJSManager();
            this.comp = comp;
            document = (AbstractDocument)comp.getDocument();
            columns = field.getNumberOfCombs()==0 ? field.getMaxLength()==0 ? Integer.MAX_VALUE : field.getMaxLength() : field.getNumberOfCombs();
        }

        public void insertString(FilterBypass fb, int offset, String changeEx, AttributeSet attrs)
            throws BadLocationException
        {
            comp.putClientProperty("bfo.HasChanged", "true");
            StringBuilder change = new StringBuilder(changeEx);
            if (columns < fb.getDocument().getLength() + changeEx.length()) {
                comp.getToolkit().beep();
                change.setLength(columns - fb.getDocument().getLength());
            }
            JSEvent jsevent;
            if ((jsevent=js.runEventFieldKeystroke(docpanel, widget, 0, change.toString(), changeEx, !change.toString().equals(changeEx), false, false, offset, offset, false, comp.getText(), false)).getRc()) {
                fb.insertString(offset, jsevent.getChange(), attrs);
            }
        }

        public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
            throws BadLocationException
        {
            comp.putClientProperty("bfo.HasChanged", "true");
            JSEvent jsevent;
            if ((jsevent=js.runEventFieldKeystroke(docpanel, widget, 0, "", "", false, false, false, offset, offset+length, false, comp.getText(), false)).getRc()) {
                fb.remove(offset, length);
            }

        }

        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String changeEx, AttributeSet attrs)
            throws BadLocationException
        {
            comp.putClientProperty("bfo.HasChanged", "true");
            StringBuilder change = new StringBuilder(changeEx!=null ? changeEx : "");
            if (columns < fb.getDocument().getLength() - length + change.length()) {
                docpanel.getToolkit().beep();
                change.setLength(columns - fb.getDocument().getLength() + length);
            }
            JSEvent jsevent;
            if ((jsevent=js.runEventFieldKeystroke(docpanel, widget, 0, change.toString(), changeEx, !change.toString().equals(changeEx), false, false, offset, offset+length, false, comp.getText(), false)).getRc()) {
                fb.replace(offset, length, jsevent.getChange(), attrs);
            }
        }

        public void focusGained(FocusEvent event) {
            if (comp.getClientProperty("bfo.HasFocus")==null) {
                comp.putClientProperty("bfo.HasFocus", "true");
                js.runEventFieldFocus(docpanel, widget, false, false);
                float fontsize = widget.getTextStyle().getFontSize();
                if (fontsize == 0) {
                    fontsize = 12;
                }
                PagePanel pagepanel = (PagePanel)comp.getParent();
                fontsize = fontsize * pagepanel.getDPI() / 72;
                if (Math.abs(fontsize - comp.getFont().getSize2D()) > 0.01) {
                    comp.setFont(comp.getFont().deriveFont(fontsize));
                }

                document.setDocumentFilter(null);
                comp.setText(field.getValue());
                document.setDocumentFilter(this);
            }
        }

        public void focusLost(FocusEvent event) {
            if (!event.isTemporary() && comp.getClientProperty("bfo.HasFocus")!=null && comp.isValid()) {
                boolean ok = true;
                if (comp.getClientProperty("bfo.HasChanged")!=null) {
                    if (comp.getClientProperty("bfo.willCommitEvent")==null) {
                        JSEvent jsevent;
                        if ((jsevent=js.runEventFieldKeystroke(docpanel, widget, 1, "", "", false, false, false, -1, -1, false, comp.getText(), true)).getRc()) {
                            document.setDocumentFilter(null);
                            document.setDocumentFilter(this);
                        } else {
                            ok = false;
                            String oldValue = field.getValue();
                            comp.setText((oldValue == null) ? "" : oldValue);
                        }
                    }
                    if (ok) {
                        JSManager console = docpanel.getJSManager();
                        String value = comp.getText();
                        if (console.runEventFieldValidate(docpanel, widget, value, false, false, value, value, false).getRc()) {
                            field.setValue(value);
                            runOtherChange(docpanel, widget);
                        } else {
                            ok = false;
                        }
                    }
                    if (ok) {
                        js.runEventFieldFormat(docpanel, widget, 1, false);
                        js.runEventFieldBlur(docpanel, widget, false, false);
                    }
                } else {
                    js.runEventFieldBlur(docpanel, widget, false, false);
                }
                if (ok) {
                    comp.putClientProperty("bfo.HasFocus", null);
                    comp.putClientProperty("bfo.HasChanged", null);
                } else {
                    // Validation has failed. We need to refocus on the field,
                    // but if we do that immediately we may lose focus again -
                    // the reason is if a dialog was opened by JavaScript during
                    // validation, it may not have fired it's "window activation"
                    // event yet. When this fires it focuses on the last item in
                    // the PagePanel that had focus, which is not this field.
                    //
                    // Ugly fix - slight delay before refocusing. Best fix would
                    // be to somehow cause refocus on this field when the dialog
                    // (if any) is closed, but I'm not sure how that can be done
                    // as we don't know if the event will fail when a dialog
                    // is closed, only when the event handler returns.
                    //
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {}
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    comp.requestFocusInWindow();
                                }
                            });
                        }
                    }.start();
                }
            }
        }

        public void actionPerformed(ActionEvent event) {
            comp.putClientProperty("bfo.HasChanged", "true");
            if (js.runEventFieldKeystroke(docpanel, widget, 2, "", "", false, false, false, -1, -1, false, comp.getText(), true).getRc()) {
                comp.putClientProperty("bfo.willCommitEvent", event);
                comp.transferFocus();
            } else {
                comp.setText(field.getValue());
            }
        }
    }
}
