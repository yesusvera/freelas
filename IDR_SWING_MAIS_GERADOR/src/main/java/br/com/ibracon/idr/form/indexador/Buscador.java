package br.com.ibracon.idr.form.indexador;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import br.com.ibracon.idr.form.model.ItemResultado;

public class Buscador {
	private static Logger logger = Logger.getLogger(Buscador.class);

	// public void buscaComParser(String parametro) {
	// try {
	// Directory diretorio = new SimpleFSDirectory(new File(diretorioDoIndice));
	// //{1}
	// IndexReader leitor = IndexReader.open(diretorio);
	// //{2}
	// IndexSearcher buscador = new IndexSearcher(leitor);
	// Analyzer analisador = new StandardAnalyzer(Version.LUCENE_36);
	// //{3}
	// QueryParser parser = new QueryParser(Version.LUCENE_36, "Texto",
	// analisador);
	// Query consulta = parser.parse(parametro);
	// long inicio = System.currentTimeMillis();
	// //{4}
	// TopDocs resultado = buscador.search(consulta, 100);
	// long fim = System.currentTimeMillis();
	// int totalDeOcorrencias = resultado.totalHits;
	// logger.info("Total de documentos encontrados:" + totalDeOcorrencias);
	// logger.info("Tempo total para busca: " + (fim - inicio) + "ms");
	// //{5}
	// for (ScoreDoc sd : resultado.scoreDocs) {
	// Document documento = buscador.doc(sd.doc);
	// logger.info("Caminho:" + documento.get("Caminho"));
	// logger.info("Última modificação:" + documento.get("UltimaModificacao"));
	// logger.info("Score:" + sd.score);
	// logger.info("ShardIndex:" + sd.shardIndex);
	// logger.info("--------");
	// }
	// buscador.close();
	// } catch (Exception e) {
	// logger.error(e);
	// }
	// }

	public ArrayList<ItemResultado> buscaComParser(String diretorioDosIndices,
			String textoPesquisado) {
		return buscaComParser(diretorioDosIndices, textoPesquisado, false);
	}

	public ArrayList<ItemResultado> buscaComParser(String diretorioDosIndices,
			String textoPesquisado, boolean imprimeLog) {
		logger.info("Buscando em " + diretorioDosIndices + " o texto "
				+ textoPesquisado);
		try {
			Directory diretorio = new SimpleFSDirectory(new File(
					diretorioDosIndices));

			IndexReader leitor = IndexReader.open(diretorio);

			IndexSearcher buscador = new IndexSearcher(leitor);
			Analyzer analisador = new StandardAnalyzer(Version.LUCENE_36);

			QueryParser parser = new QueryParser(Version.LUCENE_36, "Texto",
					analisador);
			Query consulta = parser.parse(textoPesquisado);
			long inicio = System.currentTimeMillis();

			TopDocs resultado = buscador.search(consulta, 100);
			long fim = System.currentTimeMillis();
			int totalDeOcorrencias = resultado.totalHits;
			logger.info("Total de documentos encontrados:" + totalDeOcorrencias);
			logger.info("Tempo total para busca: " + (fim - inicio) + "ms");

			ArrayList<ItemResultado> listaResultado = new ArrayList<>();

			for (ScoreDoc sd : resultado.scoreDocs) {
				Document documento = buscador.doc(sd.doc);

				String texto = documento.get("Texto");
				String textoOriginal = texto;
			
				try {
					if (textoPesquisado.indexOf(" ") > -1) {
						textoPesquisado = textoPesquisado.split(" ")[0];
					}
				} catch (Exception e) {	}
				
				String numeroPagina = documento.get("NumeroPagina");
				

				
				// PEGANDO APENAS UMA PARTE DO TEXTO
				if (texto.length() > 40) {
					int posInicial = texto.toLowerCase().indexOf(
							textoPesquisado.toLowerCase());

					int posFinal = 0;

					try {
						if ((posInicial + 40) < (texto.length() - 1)) {
							posFinal = posInicial + 40;
							texto = texto.substring(posInicial, posFinal)
									+ "...";
						} else {
							texto = texto.substring(posInicial) + "...";
						}
						// texto = texto.replaceAll("(?i)" + textoPesquisado,
						// "<b>" + textoPesquisado + "</b>");
					} catch (StringIndexOutOfBoundsException e) {
						logger.error("Erro ao fazer subtring ( " + posInicial
								+ " , " + posFinal + ")");
					}
				}

				ItemResultado itemR = new ItemResultado(new Integer(
						numeroPagina), texto);
				
				
				//DESCOBRINDO OCORRENCIAS EM PERCENTUAL
				/**@author yesus*/
				int indiceUltOcorr = 0;
				while(true){
					int indiceAtualOcorr = textoOriginal.toLowerCase().indexOf(textoPesquisado.toLowerCase(), indiceUltOcorr);
					
					if(indiceAtualOcorr>-1){
						indiceUltOcorr = indiceAtualOcorr+1;
						
						byte perc = (byte)((100*indiceAtualOcorr)/textoOriginal.length());
						
						itemR.getOcorrenciasPerc().add(perc);
					}else{
						break;
					}
				}
				
				listaResultado.add(itemR);

				if (imprimeLog) {
					logger.info("************************ NumeroPagina:"
							+ numeroPagina + "***************************");
					logger.info("NumeroPagina:" + numeroPagina);
					logger.info("Score:" + sd.score);
					logger.info("ShardIndex:" + sd.shardIndex);
					logger.info("texto:" + texto);
					logger.info("*************************************************************************************");
				}
			}
			buscador.close();

			return listaResultado;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public static void main(String[] args) {
		Buscador b = new Buscador();
		b.buscaComParser(JOptionPane.showInputDialog("Diretorio índice"),
				JOptionPane.showInputDialog("Parâmetros"));
	}
}
