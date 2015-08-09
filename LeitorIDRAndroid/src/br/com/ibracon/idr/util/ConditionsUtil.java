package br.com.ibracon.idr.util;

/**
 * @author yvera
 * Classe quem contem métodos úiteis para expresões condicionais.
 */
public class ConditionsUtil {

	/**
	 * Checks if is null.
	 *
	 * @param objetos the objetos
	 * @return true, if is null
	 * Para validar se um ou mais objetos tem o valor null
	 */
	public static boolean allNull(Object... objetos){
		for(Object obj: objetos){
			if(obj != null){
				return false;
			}
		}
		return true;
	}
	

	public static boolean isNull(Object obj){
		return allNull(obj);
	}

	public static boolean allNotNull(Object... objetos){
		for(Object obj: objetos){
			if(obj == null){
				return false;
			}
		}
		return true;
	}

	
}
