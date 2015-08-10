package br.com.ibracon.idr.form.criptografia;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
//
//import org.apache.commons.codec.binary.Base64;
//import sun.misc.BASE64Encoder;

import org.apache.log4j.Logger;

public class FileCrypt {

	static Logger logger = Logger.getLogger(FileCrypt.class);
		
	public static final String CHAVE_LIVRO_IDR = "IBRACON_IDR_2013_PASS_LEITOR";

	public static final int CRIPTOGRAFAR = 0;
	public static final int DESCRIPTOGRAFAR = 1;

	private static byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8,
			(byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

	private static int iteration = 5;
	static final int TAMANHO_BUFFER = 32768; // 32kb
	byte[] buf = new byte[TAMANHO_BUFFER];

	SecretKey key = null;

	public FileCrypt(String senha) {
		try {
			// Cria a chave criptografica baseada na senha passada
			KeySpec keySpec = new PBEKeySpec(senha.toCharArray(), salt,
					iteration);
			// Transforma a senha String em uma chave opaca
			key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
					.generateSecret(keySpec);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public FileCrypt() {
		super();
	}

	public Boolean criptografa(InputStream in, OutputStream out)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		try {

			Cipher cifra = Cipher.getInstance(key.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iteration);
			cifra.init(Cipher.ENCRYPT_MODE, key, paramSpec);

			out = new CipherOutputStream(out, cifra);

			int numRead = 0;

			while ((numRead = in.read(buf, 0, TAMANHO_BUFFER)) >= 0) {
				out.write(buf, 0, numRead);
			}

			out.close();

			in.close();

			return true;

		} catch (Exception e) {
			logger.debug("Erro ao ler os bytes. Motivo: "
					+ e.getMessage());

			return false;
		}
	}

	public Boolean descriptografa(InputStream in, OutputStream out)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {

		try {

			Cipher cifra = Cipher.getInstance(key.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iteration);
			cifra.init(Cipher.DECRYPT_MODE, key, paramSpec);

			out = new CipherOutputStream(out, cifra);

			int numRead = 0;

			while ((numRead = in.read(buf, 0, TAMANHO_BUFFER)) >= 0) {
				out.write(buf, 0, numRead);
			}

			out.close();

			in.close();

			return true;

		} catch (Exception e) {
			logger.debug("Erro ao ler os bytes. Motivo: "
					+ e.getMessage());
			return false;
		}
	}

	public byte[] getArrayBytesDescriptografado(FileInputStream in)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {

		try {
			Cipher cifra = Cipher.getInstance(key.getAlgorithm());
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
					iteration);
			cifra.init(Cipher.DECRYPT_MODE, key, paramSpec);
			
			CipherInputStream cipherInputStream = new CipherInputStream(in, cifra);

			try {
				ByteArrayOutputStream byteArrayStream= new ByteArrayOutputStream();
				int bytesread = 0;
				byte[] tbuff = new byte[1024];
				while ((bytesread = cipherInputStream.read(tbuff)) != -1) {
					byteArrayStream.write(tbuff, 0, bytesread);
				}
				return byteArrayStream.toByteArray();
			} catch (IOException e) {
				if (cipherInputStream != null) {
					try {
						cipherInputStream.close();
					} catch (IOException e2) {
					}
				}
				return null;
			}
		} catch (Exception e) {
			logger.debug("Erro ao ler os bytes. Motivo: "
					+ e.getMessage());
			return null;
		}
	}

	/*
	 * public void trataDiretorio(String diretorio, int operacao) throws
	 * InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
	 * InvalidAlgorithmParameterException { File dir = new File(diretorio);
	 * if(dir.isDirectory()) { File [] arquivos = dir.listFiles(); for (int i =
	 * 0; i < arquivos.length; i++) { if(arquivos<i>.isFile()) { if(operacao ==
	 * FileCrypt.CRIPTOGRAFAR) {
	 * this.criptografa(arquivos<i>.getAbsolutePath()); } else if(operacao ==
	 * FileCrypt.DESCRIPTOGRAFAR) {
	 * this.descriptografa(arquivos<i>.getAbsolutePath()); } else { throw new
	 * IllegalArgumentException("Invalid Operation."); } } else
	 * if(arquivos<i>.isDirectory()) {
	 * trataDiretorio(arquivos<i>.getAbsolutePath(), operacao); } } } }
	 */

	// public static String encriptaSenha(String senha) {
	// try {
	// MessageDigest digest = MessageDigest.getInstance("MD5");
	// digest.update(senha.getBytes());
	// BASE64Encoder encoder = new BASE64Encoder();
	// return encoder.encode(digest.digest());
	// return new Base64().encode(digest.digest());
	// } catch (NoSuchAlgorithmException ns) {
	// ns.printStackTrace();
	// return senha;
	// }
	// }

	public static void main(String[] args) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, FileNotFoundException {
		FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
		// cripto.criptografa(new
		// FileInputStream("/Users/yesus/Desktop/LIVRO.zip"),
		// new FileOutputStream("/Users/yesus/Desktop/LIVRO_CRIPTO.idr"));

		cripto.descriptografa(new FileInputStream(
				"/Users/yesus/IDR_IBRACON/responseEstanteXML.cript"), new FileOutputStream(
						"/Users/yesus/IDR_IBRACON/responseEstanteXML.xml"));
	}
}