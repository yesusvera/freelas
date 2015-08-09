package br.com.ibracon.idr.form.bo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.form.model.LivroIDR;
import br.com.ibracon.idr.form.util.FileUtil;
import br.com.ibracon.idr.form.util.ZipUtils;

public class LivroIdrBO {
	static Logger logger = Logger.getLogger(LivroIdrBO.class);
	
	private InstalacaoBO instalacaoBO = new InstalacaoBO();

//	public LivroIDR getLivroIDR(File arquivoLivroIDR) throws IOException,
//			InvalidKeyException, NoSuchAlgorithmException,
//			NoSuchPaddingException, InvalidAlgorithmParameterException {
//		LivroIDR livroIDR = null;
//
//		// DELETA E CRIA A PASTA TEMP DO IBRACON
//		File pastaTemp = new File(instalacaoBO.getPathInstalacao()
//				+ File.separator + ".tmp" + File.separator + ".dll"
//				+ File.separator + ".ext" + File.separator + ".xml"
//				+ File.separator + ".tmp" + File.separator + ".xds");
//		FileUtil.deleteDir(pastaTemp);
//		pastaTemp.mkdirs();
//
//		if (pastaTemp.exists()) {
//			File arquivoZip = new File(pastaTemp.getAbsolutePath()
//					+ File.separator
//					+ arquivoLivroIDR.getName().replace(".idr", ".zip"));
//			// DESCRIPTOGRAFA O IDR PARA ZIP
//			FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
//			cripto.descriptografa(new FileInputStream(arquivoLivroIDR),
//					new FileOutputStream(arquivoZip));
//
//			ZipUtils.extract(arquivoZip, pastaTemp);
//
//			// MONTA O LIVRO TODO faltando restante das informações do livro
//			livroIDR = new LivroIDR();
//			String pathPastaOculta = pastaTemp + File.separator
//					+ arquivoZip.getName().replace(".zip", "") + File.separator;
//			livroIDR.setFotoFile(new File(pathPastaOculta + geradorBO.FOTO_PNG));
//			livroIDR.setIndiceXmlFile(new File(pathPastaOculta
//					+ geradorBO.INDICE_XML));
//			livroIDR.setPdfFile(new File(pathPastaOculta + geradorBO.LIVRO_PDF));
//		}
//		return livroIDR;
//	}

	public LivroIDR getLivroIDRArrayBytes(File arquivoLivroIDR)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {
		
		logger.info("Pegando array de bytes do Livro Idr");
		
		LivroIDR livroIDR = null;

		// DESCRIPTOGRAFA O IDR PARA ZIP
		FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
		byte[] arrayBytesDescriptografado = cripto.getArrayBytesDescriptografado(new FileInputStream(arquivoLivroIDR));

		// DESCOMPACTA O ARQUIVO
		livroIDR = ZipUtils.extractArrayBytes(arrayBytesDescriptografado);
		return livroIDR;
	}
	
	public void copiaExtraiZipIndexacao(String nomeArquivo, LivroIDR livroIDR){
		//Copiando e extraindo o zip de indexação
		if(livroIDR.getIndexacaoZipByteArray()!=null){
			String pathIndexacaoZip = InstalacaoBO.getPathInstalacao() + File.separator + nomeArquivo + File.separator + "indexacao.zip";
			
			File fileIndexacaoZip  = new File(pathIndexacaoZip);
			FileUtil.copiar(new ByteArrayInputStream(livroIDR.getIndexacaoZipByteArray()), 
										pathIndexacaoZip);
			
			try {
				ZipUtils.extract(fileIndexacaoZip, new File(InstalacaoBO.getPathInstalacao() + File.separator + nomeArquivo + File.separator));
				
				fileIndexacaoZip.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}