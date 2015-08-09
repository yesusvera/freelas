package br.com.ibracon.idr.form.bo;

/*
 * This class is part of the book "iText in Action - 2nd Edition"
 * written by Bruno Lowagie (ISBN: 9781935182610)
 * For more info, go to: http://itextpdf.com/examples/
 * This example only works with the AGPL version of iText.
 */


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import br.com.ibracon.idr.form.model.itext.MyTextRenderListener;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RenderListener;

public class ParsingPdfBO {

    /** The resulting PDF. */
    public static final String PDF = "/Users/yesus/Desktop/EXTRACAO/IFRS2013_TESTE_EXTRACAO.pdf";
    /** A possible resulting after parsing the PDF. */
    public static final String TEXT1 = "/Users/yesus/Desktop/EXTRACAO/resultado1.txt";
    /** A possible resulting after parsing the PDF. */
    public static final String TEXT2 = "/Users/yesus/Documents/Trabalho/CODETEC/final/resultado2.txt";
    /** A possible resulting after parsing the PDF. */
    public static final String TEXT3 = "/Users/yesus/Documents/Trabalho/CODETEC/final/resultado3.txt";
    
    /** PAGINAS **/
    public static final String PATH_RESULTADO = "/Users/yesus/Desktop/EXTRACAO/";
    
    /**
     * Generates a PDF file with the text 'Hello World'
     * @throws DocumentException 
     * @throws IOException 
     */
    public void createPdf(String filename) throws DocumentException, IOException {
        // step 1
    	Document document = new Document();
        // step 2
    	PdfWriter writer
          = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
    	document.open();
    	// step 4
        // we add the text to the direct content, but not in the right order
        PdfContentByte cb = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont();
        cb.beginText();
        cb.setFontAndSize(bf, 12);
        cb.moveText(88.66f, 367); 
        cb.showText("ld");
        cb.moveText(-22f, 0); 
        cb.showText("Wor");
        cb.moveText(-15.33f, 0); 
        cb.showText("llo");
        cb.moveText(-15.33f, 0); 
        cb.showText("He");
        cb.endText();
        // we also add text in a form XObject
        PdfTemplate tmp = cb.createTemplate(250, 25);
        tmp.beginText();
        tmp.setFontAndSize(bf, 12);
        tmp.moveText(0, 7);
        tmp.showText("Hello People");
        tmp.endText();
        cb.addTemplate(tmp, 36, 343);
        // step 5
        document.close();
    }
    
    /**
     * Parses the PDF using PRTokeniser
     * @param src  the path to the original PDF file
     * @param dest the path to the resulting text file
     * @throws IOException
     */
    public void parsePdf(String src, String dest) throws IOException {
        PdfReader reader = new PdfReader(src);
        // we can inspect the syntax of the imported page
        byte[] streamBytes = reader.getPageContent(1);
        PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(streamBytes)));
        PrintWriter out = new PrintWriter(new FileOutputStream(dest));
        while (tokenizer.nextToken()) {
            if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) {
                out.println(tokenizer.getStringValue());
            }
        }
        out.flush();
        out.close();
        reader.close();
    }
    
    /**
     * Extracts text from a PDF document.
     * @param src  the original PDF document
     * @param dest the resulting text file
     * @throws IOException
     */
    public void extractText(String src, String dest, int pagina) throws IOException {
        PrintWriter out = new PrintWriter(new FileOutputStream(dest));
        PdfReader reader = new PdfReader(src);
        RenderListener listener = new MyTextRenderListener(out);
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
        
        PdfDictionary pageDic = reader.getPageN(pagina);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pagina), resourcesDic);

        out.flush();
        out.close();
        reader.close();
    }
    
    public void extractText(String src, String dest) throws IOException {
       extractText(src, dest, 1);
    }

    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException 
     * @throws IOException
     */
    public static void main(String[] args) throws DocumentException, IOException {
//         ParsingPdfBO pbo = new ParsingPdfBO();
//         pbo.parsePdf(PDF, TEXT1);
    	
    	
        FileOutputStream out = new FileOutputStream("/Users/yesus/Desktop/EXTRACAO/EXTRACAO_TEXTO_COMPLETO.txt");
        PdfReader reader = new PdfReader(PDF);
        int numeroPaginas = reader.getNumberOfPages();
        
       	long tempoIni = System.currentTimeMillis();
        
        
        System.out.println("TOTAL PAGINAS -> " + numeroPaginas);
        
        byte[] part1 = "--IN--PAG--".getBytes();
        byte[] part2 = "--PROC".getBytes();
        
        for (int i = 1; i <= numeroPaginas; i++) {
        	//System.out.println("EXTRAINDO PAGINA ----> " + i);
            
        	out.write(part1);
        	out.write((i+"").getBytes());
        	out.write(part2);
        	
        	try {
				out.write(PdfTextExtractor.getTextFromPage(reader, i)
						.getBytes());
			} catch (Exception e) {
			}
		}
        
        System.out.println("Tempo decorrido--> " + (System.currentTimeMillis() - tempoIni)/1000);
        
        out.flush();
        out.close();
        reader.close();
    }
}
