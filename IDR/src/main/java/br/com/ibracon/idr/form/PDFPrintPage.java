
package br.com.ibracon.idr.form;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

public class PDFPrintPage implements Printable {
	static Logger logger = Logger.getLogger(PDFPrintPage.class);
	
	
    private PDFFile file;
    private PrinterJob pjob;
    private JDialog pd;
    private JLabel pagenumlabel;
    private JButton cancel;

    public PDFPrintPage(PDFFile file) {
        this.file = file;
    }

    public void printFile(String filename, boolean setupPaper) throws IOException {
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        PDFFile pdfFile = new PDFFile(bb); // Create PDF Print Page

        PDFPrintPage pages = new PDFPrintPage(pdfFile);

        // Create Print Job.
        // We set the margins to 0, on the default 8.5 x 11 paper

        PrinterJob pjob = PrinterJob.getPrinterJob();
        PageFormat pfDefault = PrinterJob.getPrinterJob().defaultPage();
        Paper defaultPaper = new Paper();
        defaultPaper.setImageableArea(0, 0, defaultPaper.getWidth(), defaultPaper.getHeight());
        pfDefault.setPaper(defaultPaper);
        if (setupPaper) {
            pfDefault = PrinterJob.getPrinterJob().pageDialog(pfDefault);
        }
        pjob.setJobName(file.getName());
        if (pjob.printDialog()) {
            // validate the page against the chosen printer to correct
            // paper settings and margins
            pfDefault = pjob.validatePage(pfDefault);
            Book book = new Book();

            book.append(pages, pfDefault, pdfFile.getNumPages());
            pjob.setPageable(book);

            try {
                pjob.print();
            } catch (PrinterException exc) {
                logger.debug(exc);
            }
        }
    }

    /**
     * Generates the status dialog with cancel button.
     */
    private void createPrintDialog() {
        pd = new JDialog((Frame) null, "Printing...", false);
        Container top = pd.getContentPane();
        Box lines = Box.createVerticalBox();
        Box line = Box.createHorizontalBox();
        line.add(new JLabel("Now printing: "));
        JLabel title = new JLabel("file.pdf");
        line.add(title);
        lines.add(line);

        line = Box.createHorizontalBox();
        line.add(Box.createHorizontalStrut(10));
        line.add(new JLabel("page "));
        pagenumlabel = new JLabel("1");
        line.add(pagenumlabel);
        line.add(new JLabel(" of "));
        JLabel totalpages = new JLabel(String.valueOf(file.getNumPages()));
        line.add(totalpages);
        lines.add(line);

        top.add(lines, BorderLayout.CENTER);

        Box cancelbox = Box.createHorizontalBox();
        cancelbox.add(Box.createHorizontalGlue());
        cancel = new JButton(new AbstractAction("Cancel") {

            public void actionPerformed(ActionEvent evt) {
                doCancel();
            }
        });
        cancelbox.add(cancel);
        top.add(cancelbox, BorderLayout.SOUTH);
    }

    /**
     * Show the progress dialog for this print job
     * @param pjob the PrinterJob representing the print job
     */
    public void show(PrinterJob pjob) {
        this.pjob = pjob;
        if (pd == null) {
            createPrintDialog();
        }
        pd.pack();
        pd.setVisible(true);
    }

    /**
     * Close the progress dialog.  Don't use this method to cancel
     * the print job; use {@link #doCancel doCancel} instead.
     */
    public void hide() {
        pd.dispose();
    }

    /**
     * Cancel the print job.  Disables the cancel button, as it might
     * take a while for the cancel to take effect.
     */
    public void doCancel() {
        cancel.setEnabled(false);
        pjob.cancel();
    }

    // from Printable interface:  prints a single page, given a Graphics
    // to draw into, the page format, and the page number.
    public int print(Graphics g, PageFormat format, int index)
            throws PrinterException {
        int pagenum = index + 1;

        // don't bother if the page number is out of range.
        if ((pagenum >= 1) && (pagenum <= file.getNumPages())) {

            // update the page number in the progress dialog
            if (pagenumlabel != null) {
                pagenumlabel.setText(String.valueOf(pagenum));
            }

            // fit the PDFPage into the printing area
            Graphics2D g2 = (Graphics2D) g;
            PDFPage page = file.getPage(pagenum);
            double pwidth = format.getImageableWidth();
            double pheight = format.getImageableHeight();

            double aspect = page.getAspectRatio();

            // handle page orientation matching
            double paperaspect = pwidth / pheight;
            if (paperaspect < 1.0) {
                switch (format.getOrientation()) {
                    case PageFormat.REVERSE_LANDSCAPE:
                    case PageFormat.LANDSCAPE:
                        format.setOrientation(PageFormat.PORTRAIT);
                        break;
                    case PageFormat.PORTRAIT:
                        format.setOrientation(PageFormat.LANDSCAPE);
                        break;
                }
                pwidth = format.getImageableWidth();
                pheight = format.getImageableHeight();
                paperaspect = pwidth / pheight;
            }

            Rectangle imgbounds;
            int width;
            int height;
            if (aspect > paperaspect) {
                // paper is too tall / pdfpage is too wide
                height = (int) (pwidth / aspect);
                width = (int) pwidth;
            } else {
                // paper is too wide / pdfpage is too tall
                width = (int) (pheight * aspect);
                height = (int) pheight;
            }
            imgbounds = new Rectangle((int) format.getImageableX(),
                    (int) format.getImageableY(),
                    width, height);

            // render the page
            PDFRenderer pgs =
                    new PDFRenderer(page, g2, imgbounds, null, null);
            try {
                page.waitForFinish();
                pgs.run();
            } catch (InterruptedException ie) {
            }
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }
}
