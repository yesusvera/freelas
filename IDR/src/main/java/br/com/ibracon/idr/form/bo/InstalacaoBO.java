package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.form.util.FileUtil;


/**
 * The Class InstalacaoBO.
 * 
 * @author yesus
 */
public class InstalacaoBO {

	static Logger logger = Logger.getLogger(InstalacaoBO.class);
	
	/**
	 * Instalar.
	 */
	public void instalar() {
		instalar(true);
		logger.info("Configurando a pasta de instalação");
	}
	
	

	/**
	 * Criar diretorio instalacao.
	 * 
	 * @param copiarArquivos
	 *            the copiar arquivos
	 * @return true, if successful
	 */
	public void instalar(boolean copiarArquivos) {
		logger.info("Criando as pastas necessárias");
		File diretorioInstalacao = getDiretorioInstalacao();

		if (diretorioInstalacao == null || !diretorioInstalacao.exists()
				|| !diretorioInstalacao.isDirectory()) {
			diretorioInstalacao.mkdir();
			logger.info("Diretório de instalação criado");
		}
		
		getDiretorioIndiceBaixados();
		
		if (copiarArquivos) {
			copiarArquivos();
			logger.info("Arquivos necessários copiados.");
		}
	}

	public static File getDiretorioBaixados(){
		logger.info("Pegando o diretório baixados.");
		File dirBaixados = new File(getPathInstalacao() + File.separator + "BAIXADOS");
		dirBaixados.mkdirs();
		return dirBaixados;
	}
	
	public static File getDiretorioIndiceBaixados(){
		logger.info("Pegando o diretório de índice dos baixados.");
		File dirBaixados = new File(getPathInstalacao() + File.separator + "BAIXADOS"  + File.separator +"INDICE");
		if(!dirBaixados.exists() || !dirBaixados.isDirectory()){
			dirBaixados.mkdirs();
		}
		return dirBaixados;
	}
	
	public static String getPathDiretorioBaixados(){
		return getPathInstalacao() + File.separator + "BAIXADOS";
	}
	
	/**
	 * Gets the diretorio instalacao.
	 * 
	 * @return the diretorio instalacao
	 */
	public File getDiretorioInstalacao() {
		String dirInstalacao = getPathInstalacao();
		return new File(dirInstalacao);
	}

	/**
	 * Gets the path instalacao.
	 * 
	 * @return the path instalacao
	 */
	public static String getPathInstalacao() {
		return System.getProperty("user.home") + File.separator + "IDR_IBRACON";
	}

	/**
	 * Copiar properties dir instalacao.
	 */
	public void copiarArquivos() {
//		FileUtil.copiar(FormPrincipal.class.getResourceAsStream("configuracoes/ibracon.pdf"), getPathInstalacao()
//				+ File.separator + "ibracon.pdf");
//		logger.info("Copiando arquivo ibracon.pdf");

		String pathNotas = getPathInstalacao() + File.separator + "nota.properties";
		String pathProxyTmp =  getPathInstalacao() + File.separator + "proxy.tmp.properties";
		String pathProxy =  getPathInstalacao() + File.separator + "proxy.properties";
		
		if(!new File(pathNotas).exists()){
			FileUtil.copiar(FormPrincipal.class.getResourceAsStream("configuracoes/nota.properties"),pathNotas );
			logger.info("Copiando arquivo nota.properties");
		}
		if(!new File(pathProxy).exists()){
			FileUtil.copiar(FormPrincipal.class.getResourceAsStream("configuracoes/proxy.properties"),pathProxyTmp);
			logger.info("Copiando arquivo proxy.properties");
			
			FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
			try {
				cripto.criptografa(new
				 FileInputStream(pathProxyTmp),
				 new FileOutputStream(pathProxy));
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		//remover proxytmp
		new File(pathProxyTmp).delete();
	}

	
	public static void excluirPastaTemp(){
		// EXCLUI A PASTA TEMP
		File pastaTemp = new File(getPathInstalacao() + File.separator + ".tmp");
		FileUtil.deleteDir(pastaTemp);
//		logger.info("Excluindo a pasta temp.");
	}
}
