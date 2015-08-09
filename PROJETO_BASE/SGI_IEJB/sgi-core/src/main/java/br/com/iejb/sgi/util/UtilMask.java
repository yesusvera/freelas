package br.com.iejb.sgi.util;

/**
 * 
 * @author Paulo Henrique
 * 
 * **/


public final class UtilMask {

	private UtilMask() {
	}

	/**
	 * Adiciona máscara para CPF.
	 * 
	 * @param value
	 * @return
	 */
	public static String getMaskCpf(String cpf) {
		return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
				+ cpf.substring(6, 9) + "-" + cpf.substring(9);
	}

	/**
	 * Adiciona máscara para CPF.
	 * 
	 * @param value
	 * @return
	 */
	public static String removeMask(String cpf) {

		String str = cpf;
		while (str.indexOf("-") != -1) {
			if (str.indexOf("-") != 0) {
				str = str.substring(0, str.indexOf("-"))
						+ str.substring(str.indexOf("-") + 1);
			} else {
				str = str.substring(str.indexOf("-") + 1);
			}
		}
		while (str.indexOf(".") != -1) {
			if (str.indexOf(".") != 0) {
				str = str.substring(0, str.indexOf("."))
						+ str.substring(str.indexOf(".") + 1);
			} else {
				str = str.substring(str.indexOf(".") + 1);
			}
		}
		return str;
	}

}
