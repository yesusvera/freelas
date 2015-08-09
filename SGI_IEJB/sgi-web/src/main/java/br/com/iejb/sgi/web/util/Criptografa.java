package br.com.iejb.sgi.web.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografa {
	
	 public static String sha1(String input) {
	        MessageDigest mDigest = null;
			try {
				mDigest = MessageDigest.getInstance("SHA1");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
	        byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
	    }
}
