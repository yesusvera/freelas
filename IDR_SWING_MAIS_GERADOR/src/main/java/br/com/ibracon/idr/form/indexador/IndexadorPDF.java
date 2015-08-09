package br.com.ibracon.idr.form.indexador;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import br.com.ibracon.idr.form.modal.JanelaProgresso;

public class IndexadorPDF {
	private static Logger logger = Logger.getLogger(IndexadorPDF.class);

	private String pathDiretorioIndice;
	private IndexWriter writer;

	private JanelaProgresso jp;

	public IndexadorPDF(String pathDiretorioIndices, JanelaProgresso jp) {
		this.pathDiretorioIndice = pathDiretorioIndices;
		this.jp = jp;
	}

	public void indexarArquivoByteArray(FileInputStream arquivoPDFInputStream) {
		try {
			File diretorio = new File(pathDiretorioIndice);

			if (!diretorio.exists()) {
				diretorio.mkdirs();
			}

			apagaIndices(diretorio);
			Directory d = new SimpleFSDirectory(diretorio);
			logger.info("Diretório do índice: " + pathDiretorioIndice);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
					analyzer);
			writer = new IndexWriter(d, config);
			long inicio = System.currentTimeMillis();
			indexarInputStream(arquivoPDFInputStream);
			writer.commit();
			writer.close();
			long fim = System.currentTimeMillis();
			logger.info("Tempo para indexar: " + ((fim - inicio) / 1000) + "s");
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private void apagaIndices(File diretorio) {
		if (diretorio.exists()) {
			File arquivos[] = diretorio.listFiles();
			for (File arquivo : arquivos) {
				arquivo.delete();
			}
		}
	}

	public void indexarInputStream(InputStream arquivoInputStream) {
		FilenameFilter filtro = new FilenameFilter() {
			public boolean accept(File arquivo, String nome) {
				if (nome.toLowerCase().endsWith(".pdf")
						|| nome.toLowerCase().endsWith(".odt")
						|| nome.toLowerCase().endsWith(".doc")
						|| nome.toLowerCase().endsWith(".docx")
						|| nome.toLowerCase().endsWith(".ppt")
						|| nome.toLowerCase().endsWith(".pptx")
						|| nome.toLowerCase().endsWith(".xls")
						|| nome.toLowerCase().endsWith(".txt")
						|| nome.toLowerCase().endsWith(".rtf")) {
					return true;
				}
				return false;
			}
		};
		StringBuffer msg = new StringBuffer();
		msg.append("Indexando o arquivo ");
		logger.info(msg);
		try {

			PDDocument pddocument = PDDocument.load(arquivoInputStream);
			PDFTextStripper textStripper = new PDFTextStripper();

			int numeroPaginas = pddocument.getNumberOfPages();

			for (int i = 1; i <= numeroPaginas; i++) {
				String mensagem = "       indexando página --> " + i
						+ "         ";
				logger.info(mensagem);
				jp.setTexto(mensagem);
				textStripper.setStartPage(i);
				textStripper.setEndPage(i);
				String textoExtraido = textStripper.getText(pddocument);
				indexaArquivo(textoExtraido, i);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void indexaArquivo(String textoExtraido, int pagina) {
		// SimpleDateFormat formatador = new SimpleDateFormat("yyyyMMdd");

		Document documento = new Document();
		// documento.add(new Field("UltimaModificacao", ultimaModificacao,
		// Field.Store.YES, Field.Index.NOT_ANALYZED));
		documento.add(new Field("NumeroPagina", String.valueOf(pagina),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		documento.add(new Field("Texto", textoExtraido, Field.Store.YES,
				Field.Index.ANALYZED));
		try {
			// {11}
			getWriter().addDocument(documento);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public IndexWriter getWriter() {
		return writer;
	}
}