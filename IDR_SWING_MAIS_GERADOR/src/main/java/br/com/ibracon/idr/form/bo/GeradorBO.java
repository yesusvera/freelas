package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.form.indexador.IndexadorPDF;
import br.com.ibracon.idr.form.model.LivroIDR;
import br.com.ibracon.idr.form.util.FileUtil;
import br.com.ibracon.idr.form.util.ZipUtils;
import br.com.ibracon.idr.gerador.GeradorIDR;

public class GeradorBO {
	
	public static final String INDICE_XML = "indice.xml";
	public static final String FOTO_PNG = "foto.png";
	public static final String LIVRO_PDF = "livro.pdf";
	public static final String INDEXACAO_ZIP = "indexacao.zip";

	public void salvarLivroIDR(LivroIDR livroIDR, String caminhoDiretorioSalvar, GeradorIDR frame, boolean gerarIndexacao) throws IOException{
		
		//CRIAR PASTA INSTALACAO
		InstalacaoBO instalacaoBO = new InstalacaoBO();
		
		frame.jp.setPercentual(5);
		
		instalacaoBO.instalar(false);

		//CRIAR PASTA E COPIAR OS ARQUIVOS
		File pastaLivro = new File(instalacaoBO.getDiretorioInstalacao() + File.separator + "gerador" + File.separator + livroIDR.getCodigoLivro());
		if(pastaLivro.exists()){
			pastaLivro.delete();
		}
		frame.jp.setPercentual(20);
		frame.jp.setTexto("Criando pasta de gerador...");
		pastaLivro.mkdirs();
		
		if(pastaLivro.exists()){
			
			//Copiar Foto
			if(livroIDR.getFotoFile()!=null && livroIDR.getFotoFile().exists()){
				frame.jp.setPercentual(30);
				frame.jp.setTexto("Copiando arquivo de foto...");
				frame.repaint();
				FileUtil.copiar(livroIDR.getFotoFile().getAbsolutePath(), pastaLivro.getAbsolutePath()+ File.separator + FOTO_PNG);
			}
			
			//Copiar Xml índice
			if(livroIDR.getIndiceXmlFile()!=null && livroIDR.getIndiceXmlFile().exists()){
				frame.jp.setPercentual(40);
				frame.jp.setTexto("Copiando arquivo xml de índice...");
				frame.repaint();	
				FileUtil.copiar(livroIDR.getIndiceXmlFile().getAbsolutePath(), pastaLivro.getAbsolutePath()+ File.separator + INDICE_XML);
			}
			
			//Copiar PDF
			if(livroIDR.getPdfFile()!=null && livroIDR.getPdfFile().exists()){
				frame.jp.setPercentual(50);
				frame.jp.setTexto("Copiando arquivo de pdf..");
				FileUtil.copiar(livroIDR.getPdfFile().getAbsolutePath(), pastaLivro.getAbsolutePath()+ File.separator + LIVRO_PDF);
			}
			
			//*** INICIO INDEXACAO COM LUCENE ***//
			if(gerarIndexacao){
				String pathIndice = pastaLivro.getAbsolutePath() + File.separator + "indexacao";
				IndexadorPDF indexadorPDF = new IndexadorPDF(pathIndice, frame.jp);
				indexadorPDF.indexarArquivoByteArray(new FileInputStream(livroIDR.getPdfFile()));
				
				File pathIndiceFile = new File(pathIndice);
				File pathIndiceFileZip = new File(pathIndice + ".zip");
				
				ZipUtils.compress(pathIndiceFile, pathIndiceFileZip);  
				
				if(pathIndiceFileZip.exists()){
					FileUtil.deleteDir(pathIndiceFile);
				}
			}
			//************ FIM INDEXACAO *******//
				
				
			//Criar xml com outras informações
				/*TODO FALTANDO IMPLEMENTACAO*/
				
				
			//FAZ A COMPRESSAO ZIP
			frame.jp.setPercentual(70);
			frame.jp.setTexto("Fazendo compressão da pasta...");
			frame.repaint();
			
			File livroIdrZip = new File(pastaLivro.getAbsolutePath()+".zip");  
			
			ZipUtils.compress(pastaLivro, livroIdrZip);  
			File livroIdrCripto = new File(pastaLivro.getAbsolutePath()+".idr");  
			
			//CRIPTOGRAFA
			try {
				
				frame.jp.setPercentual(85);
				frame.jp.setTexto("Gerando *.idr");
				frame.repaint();
				
				FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
				cripto.criptografa(new FileInputStream(livroIdrZip),
						new FileOutputStream(livroIdrCripto));
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			
			//EXCLUI A PASTA E O ZIP
			frame.jp.setPercentual(90);
			frame.jp.setTexto("Excluindo os arquivos temporários");
			frame.repaint();
			
			if(livroIdrZip.exists()){
				livroIdrZip.delete();
			}
			
			if(pastaLivro.exists()){
				FileUtil.deleteDir(pastaLivro);
			}
			
			//COPIA O ARQUIVO .IDR PARA O CAMINHO ESCOLHIDO PARA SALVAR
			File livroIdrSalvo = new File(caminhoDiretorioSalvar + File.separator + livroIdrCripto.getName());
				
			frame.jp.setPercentual(100);
			frame.jp.setTexto("Arquivo criado" + livroIdrSalvo.getAbsolutePath());
			
			if(livroIdrCripto.exists()){
				FileUtil.copiar(livroIdrCripto.getAbsolutePath(), livroIdrSalvo.getAbsolutePath());
				livroIdrCripto.delete();
			}
		}
	}

}
