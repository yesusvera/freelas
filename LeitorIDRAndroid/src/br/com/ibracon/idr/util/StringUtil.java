package br.com.ibracon.idr.util;

public class StringUtil {

	public static String limitaStringReticencias(String str, int qtdeCaracteres){
		if(str.length()>qtdeCaracteres){
			return str.substring(0, qtdeCaracteres) + "...";
		}else{
			return str;
		}
	}
}
