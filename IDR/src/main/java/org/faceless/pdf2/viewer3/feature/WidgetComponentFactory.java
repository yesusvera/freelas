// $Id: WidgetComponentFactory.java 19623 2014-07-11 15:17:50Z mike $

package org.faceless.pdf2.viewer3.feature;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import org.faceless.pdf2.Event;
import org.faceless.pdf2.FormCheckbox;
import org.faceless.pdf2.FormElement;
import org.faceless.pdf2.FormRadioButton;
import org.faceless.pdf2.PDFAnnotation;
import org.faceless.pdf2.WidgetAnnotation;
import org.faceless.pdf2.viewer3.AnnotationComponentFactory;
import org.faceless.pdf2.viewer3.DocumentPanel;
import org.faceless.pdf2.viewer3.DocumentPanelEvent;
import org.faceless.pdf2.viewer3.DocumentPanelListener;
import org.faceless.pdf2.viewer3.JSManager;
import org.faceless.pdf2.viewer3.PagePanel;

/**
 * A subclass of AnnotationComponentFactory for form fields. Package private as we may
 * be tinkering with this interface.
 * @since 2.11.2
 */
abstract class WidgetComponentFactory extends AnnotationComponentFactory
{
    private static final CancelAction CANCELACTION = new CancelAction();

    WidgetComponentFactory(String name) {
        super(name);
    }

    /**
     * Called by FormXXXWidgetFactory classes when the field is created, this keeps track
     * of which fields need to be recalculated.
     */
    @SuppressWarnings("unchecked") static void createOtherChange(DocumentPanel docpanel, FormElement field) {
        if (field.getAction(Event.OTHERCHANGE)!=null) {
            Collection<FormElement> c = (Collection<FormElement>)docpanel.getClientProperty("field.otherchange");
            if (c == null) {
                c = new LinkedHashSet<FormElement>();
                docpanel.putClientProperty("field.otherchange", c);
            }
            c.add(field);
        }
    }

    /**
     * Called by FormXXXWidgetFactory classes after their value has been updated, this
     * runs any calculation scripts that need to be run
     */
    @SuppressWarnings("unchecked")
    static void runOtherChange(DocumentPanel docpanel, WidgetAnnotation annot) {
        Collection<FormElement> others = (Collection<FormElement>)docpanel.getClientProperty("field.otherchange");
        if (others != null) {
            JSManager console = docpanel.getJSManager();
            for (Iterator<FormElement> i = others.iterator();i.hasNext();) {
                FormElement other = i.next();
                console.runEventFieldCalculate(docpanel, other.getAnnotation(0), annot);
            }
        }
    }

    /**
     * A shorthand method to determine whether a Widget is read-only.
     * This is a property derived from both the Widget and the current
     * state of the DocumentPanel it's dispalyed in.
     */
    static boolean isWidgetReadOnly(WidgetAnnotation widget, DocumentPanel docpanel) {
        FormElement field = widget.getField();
        return (field != null && field.isReadOnly()) || (docpanel != null && !docpanel.hasPermission("FormFill"));
    }

    JComponent createComponent(final PagePanel pagepanel, PDFAnnotation annot, final Class type) {
        JComponent comp;
        final WidgetAnnotation widget = (WidgetAnnotation)annot;
        final FormElement field = widget.getField();
        final DocumentPanel docpanel = pagepanel.getDocumentPanel();
        final JSManager js = docpanel.getJSManager();

        if (type==null || type==JPanel.class || type==JComponent.class) {
            comp = super.createComponent(pagepanel, annot);
            if (!isWidgetReadOnly(widget, docpanel)) {
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else if (type==JTextField.class) {
            comp = new JTextField() {
                public void paintComponent(Graphics g) {
                    AnnotationComponentFactory.paintComponent(this, this.ui, g);
                    paintComponentAnnotations(this, g);
                }
            };
        } else if (type==JPasswordField.class) {
            comp = new JPasswordField() {
                public void paintComponent(Graphics g) {
                    AnnotationComponentFactory.paintComponent(this, this.ui, g);
                    paintComponentAnnotations(this, g);
                }
            };
        } else if (type==JTextArea.class) {
            comp = new JTextArea() {
                public void paintComponent(Graphics g) {
                    AnnotationComponentFactory.paintComponent(this, this.ui, g);
                    paintComponentAnnotations(this, g);
                }
            };
        } else if (type==JList.class) {
            comp = new JList() {
                public void paintComponent(Graphics g) {
                    AnnotationComponentFactory.paintComponent(this, this.ui, g);
                    paintComponentAnnotations(this, g);
                }
            };
        } else {
            throw new IllegalArgumentException("Unknown type "+type);
        }
        createOtherChange(docpanel, field);
        comp.setOpaque(false);
        comp.setBorder(AnnotationComponentFactory.FOCUSBORDER);
        comp.setFocusable(!isWidgetReadOnly(widget, docpanel));
        if (docpanel.getViewer() != null && !"false".equals(getFeatureProperty(docpanel.getViewer(), "toolTip"))) {
            comp.setToolTipText(field.getDescription()!=null ? field.getDescription() : field.getForm().getName(field));
        }

        comp.addMouseListener(new MouseListener() {
            public void mouseEntered(MouseEvent event) {
                if (!isWidgetReadOnly(widget, docpanel)) {
                    js.runEventFieldMouseEnter(docpanel, widget, event);
                }
            }
            public void mouseExited(MouseEvent event) {
                if (!isWidgetReadOnly(widget, docpanel)) {
                    js.runEventFieldMouseExit(docpanel, widget, event);
                }
            }
            public void mousePressed(MouseEvent event) {
                if (!isWidgetReadOnly(widget, docpanel)) {
                    js.runEventFieldMouseDown(docpanel, widget, event);
                }
            }
            public void mouseReleased(MouseEvent event) {
                if (!isWidgetReadOnly(widget, docpanel) && !(field instanceof FormRadioButton || field instanceof FormCheckbox)) {
                    js.runEventFieldMouseUp(docpanel, widget, event);
                }
            }
            public void mouseClicked(MouseEvent event) {
            }
        });

        final JComponent fcomp = comp;
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("readOnly")) {
                    fcomp.setFocusable(!isWidgetReadOnly(widget, docpanel));
                }
            }
        };
        field.addPropertyChangeListener(pcl);
        comp.putClientProperty("bfo.fieldPCL", pcl);

        docpanel.addDocumentPanelListener(new DocumentPanelListener() {
            public void documentUpdated(DocumentPanelEvent event) {
                boolean readonly = isWidgetReadOnly(widget, docpanel);
                if (type==null || type==JPanel.class || type==JComponent.class) {
                    fcomp.setCursor(Cursor.getPredefinedCursor(readonly ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
                }
                fcomp.setFocusable(!readonly);
            }
        });

        if (comp instanceof JTextComponent) {
            InputMap im = comp.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap am = comp.getActionMap();
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
            am.put("cancel", CANCELACTION);
        }
        return comp;
    }

    private static class CancelAction extends AbstractAction {
        public void actionPerformed(ActionEvent event) {
            JTextComponent comp = (JTextComponent)event.getSource();
            WidgetAnnotation annot = (WidgetAnnotation)comp.getClientProperty("pdf.annotation");
            if (annot!=null) {
                AbstractDocument document = (AbstractDocument)comp.getDocument();
                DocumentFilter filter = document.getDocumentFilter();
                InputVerifier verifier = comp.getInputVerifier();
                if (comp.getClientProperty("bfo.HasChanged")!=null) {
                    comp.putClientProperty("bfo.HasChanged", null);
                }
                document.setDocumentFilter(null);
                String val = annot.getField().getValue();
                comp.setText(val==null ? "" : val);
                document.setDocumentFilter(filter);
                comp.setInputVerifier(verifier);
                comp.transferFocusUpCycle();
            }
        }
    }
}
