package br.com.eudiamante.util;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class GeradorNomeUtil {

	private static ArrayList<String> listaNomes;
	private static ArrayList<String> listaUFs;

	public static ArrayList<String> getListaNomes() {
		if (listaNomes != null && !listaNomes.isEmpty()) {
			return listaNomes;
		} else {
			try {
				ResourceBundle rb = ResourceBundle
						.getBundle("br.com.eudiamante.util.gerador");
				String nomes = rb.getString("nomes");
				// String estados = rb.getString("estados");
				if (nomes != null) {
					String arrNomes[] = nomes.split(",");
					if (arrNomes.length > 1) {
						listaNomes = new ArrayList<String>();
						for (int i = 0; i < arrNomes.length; i++) {
							listaNomes.add(arrNomes[i]);
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listaNomes;
		}
	}
	
	public static ArrayList<String> getListaEstados() {
		if (listaUFs != null && !listaUFs.isEmpty()) {
			return listaUFs;
		} else {
			try {
				ResourceBundle rb = ResourceBundle
						.getBundle("br.com.eudiamante.util.gerador");
				String estados = rb.getString("estados");
				// String estados = rb.getString("estados");
				if (estados != null) {
					String arrEstados[] = estados.split(",");
					if (arrEstados.length > 1) {
						listaUFs = new ArrayList<String>();
						for (int i = 0; i < arrEstados.length; i++) {
							listaUFs.add(arrEstados[i]);
						}
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listaUFs;
		}
	}
	
	public static String getNomeRandomico(){
		if(getListaNomes()!=null){
			int i = (int) Math.round(    Math.random()*(listaNomes.size()-1)   );
			return getListaNomes().get(i);
		}
		return null;
	}
	

	public static String getUFRandomico(){
		if(getListaEstados()!=null){
			int i = (int) Math.round(    Math.random()*(listaUFs.size()-1)   );
			return getListaEstados().get(i);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println();
	}
}