package br.com.ibracon.idr.form.indexador;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
import org.apache.tika.Tika;
 
public class Indexador {
  private static Logger logger = Logger.getLogger(Indexador.class);
  //{1}
  private String diretorioDosIndices = "/Users/yesus/Desktop/EXTRACAO/INDICES/";
  //{2}
  private String diretorioParaIndexar = "/Users/yesus/Desktop/EXTRACAO/";
  //{3}
  private IndexWriter writer;
  //{4}
  private Tika tika;
 
  public static void main(String[] args) {
    Indexador indexador = new Indexador();
    indexador.indexaArquivosDoDiretorio();
  }
 
  public void indexaArquivosDoDiretorio() {
    try {
      File diretorio = new File(diretorioDosIndices);
      apagaIndices(diretorio);
      //{5}
      Directory d = new SimpleFSDirectory(diretorio);
      logger.info("Diretório do índice: " + diretorioDosIndices);
      //{6}
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
      //{7}
      IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
          analyzer);
      //{8}
      writer = new IndexWriter(d, config);
      long inicio = System.currentTimeMillis();
      indexaArquivosDoDiretorio(new File(diretorioParaIndexar));
      //{12}
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
 
  public void indexaArquivosDoDiretorio(File raiz) {
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
    for (File arquivo : raiz.listFiles(filtro)) {
      if (arquivo.isFile()) {
        StringBuffer msg = new StringBuffer();
        msg.append("Indexando o arquivo ");
        msg.append(arquivo.getAbsoluteFile());
        msg.append(", ");
        msg.append(arquivo.length() / 1000);
        msg.append("kb");
        logger.info(msg);
        try {
          //{9}
          String textoExtraido = getTika().parseToString(arquivo);
          indexaArquivo(arquivo, textoExtraido);
        } catch (Exception e) {
          logger.error(e);
        }
      } else {
        indexaArquivosDoDiretorio(arquivo);
      }
    }
  }
 
  private void indexaArquivo(File arquivo, String textoExtraido) {
    SimpleDateFormat formatador = new SimpleDateFormat("yyyyMMdd");
    String ultimaModificacao = formatador.format(arquivo.lastModified());
    //{10}
    Document documento = new Document();
    documento.add(new Field("UltimaModificacao", ultimaModificacao,
        Field.Store.YES, Field.Index.NOT_ANALYZED));
    documento.add(new Field("Caminho", arquivo.getAbsolutePath(),
        Field.Store.YES, Field.Index.NOT_ANALYZED));
    documento.add(new Field("Texto", textoExtraido, Field.Store.YES,
        Field.Index.ANALYZED));
    try {
      //{11}
      getWriter().addDocument(documento);
    } catch (IOException e) {
      logger.error(e);
    }
  }
  
  
 
  public Tika getTika() {
    if (tika == null) {
      tika = new Tika();
    }
    return tika;
  }
 
  public IndexWriter getWriter() {
    return writer;
  }
}
