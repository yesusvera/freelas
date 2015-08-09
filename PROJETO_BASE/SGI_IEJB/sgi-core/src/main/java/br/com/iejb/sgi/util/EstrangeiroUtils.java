package br.com.iejb.sgi.util;

/**
 * 
 * @author Yesus Castillo Vera
 * yvera
 * 
 * STEFANINI LTDA
 *
 */
public class EstrangeiroUtils {
	public static boolean isEmpty(String x){
		if(x==null) return true;
		if(x.trim().length()==0) return true;
		return false;
	}
	
	public static boolean notIsEmpty(String x){
		return !isEmpty(x);
	}
}
