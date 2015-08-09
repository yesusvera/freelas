package br.com.iejb.sgi.util.cripto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * classe responsável pela criptografia de informações textuais
 * @author YESUS CASTILLO 
 *
 */
public class Sha1Cripto {

    /**
     * @param args
     * @throws NoSuchAlgorithmException 
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(sha1("123456"));
    }
     
    public static String sha1(String input){
        MessageDigest mDigest;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(input.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
         
		return input;
    }
}
