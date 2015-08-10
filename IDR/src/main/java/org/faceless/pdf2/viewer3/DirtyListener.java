// $Id: DirtyListener.java 20664 2015-01-21 17:06:05Z mike $

package org.faceless.pdf2.viewer3;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.faceless.pdf2.Form;
import org.faceless.pdf2.FormElement;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFAnnotation;
import org.faceless.pdf2.PDFPage;

/**
 * Listener to mark document panel dirty when any document changes occur.
 */
class DirtyListener implements PropertyChangeListener {

    private final DocumentPanel docpanel;
    private PDF pdf;
    private Set<PDFPage> pages = new HashSet<PDFPage>();
    private Set<PDFAnnotation> annots = new HashSet<PDFAnnotation>();

    DirtyListener(DocumentPanel docpanel) {
        this.docpanel = docpanel;
    }

    void bind(PDF pdf) {
        if (this.pdf != null) {
            throw new IllegalStateException("Listener already bound");
        }
        this.pdf = pdf;
        int n = pdf.getNumberOfPages();
        boolean complete = true;
        for (int i=0;i<n;i++) {
            if (docpanel.getLinearizedSupport().isPageLoaded(i)) {
                bind(pdf.getPage(i));
            } else {
                complete = false;
            }
        }
        pdf.addPropertyChangeListener(this);
        docpanel.getLinearizedSupport().invokeOnDocumentLoad(new Runnable() {
            public void run() {
                Form form = DirtyListener.this.pdf.getForm();
                if (form != null) {
                    for (FormElement field : form.getElements().values()) {
                        field.addPropertyChangeListener(DirtyListener.this);
                    }
                }
            }
        });
    }

    private void bind(PDFPage page) {
        page.addPropertyChangeListener(this);
        pages.add(page);
        List<PDFAnnotation> pageannots = page.getAnnotations();
        for (int j = 0; j < pageannots.size(); j++) {
            PDFAnnotation annot = pageannots.get(j);
            annot.addPropertyChangeListener(this);
            annots.add(annot);
        }
    }

    void unbind(PDF pdf) {
        if (pdf != this.pdf) {
            throw new IllegalArgumentException("Listener bound to another PDF");
        }
        for (Iterator<PDFPage> i = pages.iterator(); i.hasNext(); ) {
            PDFPage page = i.next();
            page.removePropertyChangeListener(this);
        }
        pages.clear();
        for (Iterator<PDFAnnotation> i = annots.iterator(); i.hasNext(); ) {
            PDFAnnotation annot = i.next();
            annot.removePropertyChangeListener(this);
        }
        annots.clear();
        pdf.removePropertyChangeListener(this);
        Form form = pdf.getForm();
        if (form != null) {
            for (FormElement field : form.getElements().values()) {
                field.removePropertyChangeListener(this);
            }
        }
        this.pdf = null;
    }

    public void propertyChange(final PropertyChangeEvent e) {
        boolean dirty = true;
        DocumentViewport viewport = docpanel.getViewport();
        if (e.getSource() == pdf) {
            String name = e.getPropertyName();
            if ("pageLoaded".equals(name)) {
                int index = ((Integer) e.getNewValue()).intValue();
                if (index >= 0) {
                    bind(pdf.getPage(index));
                }
                dirty = false;
            } else if ("pages".equals(name)) { // Special case - catch pages being deleted
                // Ensure all pages are loaded
                for (int i=0;i<pdf.getNumberOfPages();i++) {
                    PDFPage page = pdf.getPage(i);
                    page.addPropertyChangeListener(this);
                }
                PDFPage curpage = docpanel.getPage();
                if (curpage == null) {
                    curpage = viewport.getRenderingPage();
                }
                if (curpage != null && !pdf.getPages().contains(curpage)) {
                    int pagenumber = Math.max(0, docpanel.getLastPageNumber());
                    int max = pdf.getNumberOfPages();
                    while (pagenumber >= max) {
                        pagenumber--;
                    }
                    if (pagenumber < 0) {
                        throw new IllegalStateException("Cannot display a PDF with no pages");
                    }
                    docpanel.setPage(pdf.getPage(pagenumber));
                    docpanel.raiseDocumentPanelEvent(DocumentPanelEvent.createRedrawn(docpanel));
                }
            }
        }
        if (dirty) {
            docpanel.setDirty(true);
        }
        ((PropertyChangeListener)viewport).propertyChange(e);
    }

}
