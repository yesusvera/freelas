package br.com.iejb.sgi.customSearch;

import java.util.HashMap;

/**
 * @author eccardoso
 *
 */
public class ExecuteCustomSearch {
	
	/**
	 * Construtor privado
	 */
	private ExecuteCustomSearch() {
	}
	
	/**
	 * @param valores
	 * @param hql
	 * @param alias
	 * @param field
	 * @param resultStr
	 */
	public static void execute(HashMap<String, Object> valores, StringBuilder hql, String alias, java.lang.reflect.Field field, String resultStr) {
		CustomSearch customSearch = field.getAnnotation(CustomSearch.class);
		if((customSearch != null && customSearch.like())){
			hql.append(" and upper("+alias+"."+field.getName()+") like :"+field.getName());						
			if(customSearch.direction().equals(EnumCustomDirection.LEFT)){									
				valores.put(field.getName(),"%".concat(resultStr.trim().toUpperCase()));
			}else if(customSearch.direction().equals(EnumCustomDirection.RIGHT)){
				valores.put(field.getName(),resultStr.trim().toUpperCase().concat("%"));
			}else{
				valores.put(field.getName(),"%".concat(resultStr.trim().toUpperCase().concat("%")));
			}								
		}else{
			hql.append(" and "+alias+"."+field.getName()+" = :"+field.getName());						
			valores.put(field.getName(),resultStr);
		}
	}

}
