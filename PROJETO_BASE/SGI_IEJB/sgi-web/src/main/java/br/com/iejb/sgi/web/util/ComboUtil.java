package br.com.iejb.sgi.web.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import br.com.iejb.sgi.customSearch.labelSelectOneMenu;

public class ComboUtil {


	/**
	 * @author YESUS CASTILLO VERA
	 * @param lista
	 * @return
	 */
	public static <T> List<SelectItem> converterListaObjParaSelectItem(List<T> lista){
		List<SelectItem> lstRet = new ArrayList<SelectItem>();
		
		if(lista==null || lista.size()==0){
			return lstRet;
		}
		
		for(T obj : lista){
			Class classe = obj.getClass();
			java.lang.reflect.Field[] fields = classe.getDeclaredFields();		  
			
			boolean comLabel = false;
			
			for (java.lang.reflect.Field field : fields) {
				labelSelectOneMenu labelSelectOneMenu = field.getAnnotation(labelSelectOneMenu.class);
				
				if(labelSelectOneMenu!=null && field.getType().equals(String.class) ){
					try {
						String methodName = "get"+ StringUtils.capitalize(field.getName());
						
						Object result = obterRetornoMetodo(obj, classe, methodName);
						
						lstRet.add(new SelectItem(obj, (String)result));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					comLabel = true;
					break;
				}
			}
			
			if(!comLabel){
				lstRet.add(new SelectItem(obj));
			}
		}
		
		return lstRet;
	}
	
	private static Object obterRetornoMetodo(Object obj, Class classe, String methodName) throws NoSuchMethodException,
	IllegalAccessException, InvocationTargetException {
		Method m = classe.getMethod(methodName);
		Object result = m.invoke(obj);
		return result;
	}
}
