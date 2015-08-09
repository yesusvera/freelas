package br.com.rsm.util;

import java.util.HashMap;
import java.util.Map;

public class IdUtil {

	private static Map<Long, String> domain = new HashMap<Long, String>();
	private static char [] novoId = new char []{'9', '9', '9', '9', '9'}; 
	
	public static Map<Long, String> getDomain() {
		return domain;
	}
	public static void setDomain(Map<Long, String> domain) {
		IdUtil.domain = domain;
	}
	
	
	
	
	
	public static void main(String[] args) {
		//String alfabeto = "ABCDEFGHIJKLMNPQRSTUVWXYZ1234567890";
		
		init();
		for(Long i=0L; i <= 52521875L; i++){
			transformar(i, novoId);
			System.out.println("Id="+i+"New Id: "+novoId[4]+" "+ novoId[3] +" "+ novoId[2] +" "+novoId[1] +" "+novoId[0]);
		}
		
		
	}
	private static void transformar(Long id, char [] novoId) {
		
//		int unidade = (int) (id % 35);
//		int dezena = (int) (id / 35L);
		//while ((quoeficiente) <= 35L ){
//		if(id <= 35L) {
//			novoId[0] = domain.get(new Long((id % 35))).charAt(0);  //A para B, B para C
//			novoId[1] = domain.get(new Long((id / 35))).charAt(0);  //A para B, B para C
//		} else 
		if(id <= 1224L){
			novoId[0] = domain.get(new Long((id % 35))).charAt(0);  //A para B, B para C
			novoId[1] = domain.get(new Long((id / 35))).charAt(0);  //A para B, B para C
		}else if(id <= 42874L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			
			novoId[0] = domain.get(new Long((aux1b))).charAt(0); 
			novoId[1] = domain.get(new Long((aux2b))).charAt(0); 
			novoId[2] = domain.get(new Long((aux2))).charAt(0);
		}else if(id <= 1500624L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			Long aux3 = aux2 / 35L;
			Long aux3b = aux2 % 35L;
			
			novoId[0] = domain.get(new Long((aux1b))).charAt(0); 
			novoId[1] = domain.get(new Long((aux2b))).charAt(0); 
			novoId[2] = domain.get(new Long((aux3b))).charAt(0);
			novoId[3] = domain.get(new Long((aux3))).charAt(0);
		}else if(id <= 52521874L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			Long aux3 = aux2 / 35L;
			Long aux3b = aux2 % 35L;
			Long aux4 = aux3 / 35L;
			Long aux4b = aux3 % 35L;
			
			novoId[0] = domain.get(new Long((aux1b))).charAt(0); 
			novoId[1] = domain.get(new Long((aux2b))).charAt(0); 
			novoId[2] = domain.get(new Long((aux3b))).charAt(0);
			novoId[3] = domain.get(new Long((aux4b))).charAt(0);
			novoId[4] = domain.get(new Long((aux4))).charAt(0);
		}
		
			
		//}
		//transformar(quoeficiente, novoId, nivel++);
//		if(id == 35){
//			System.out.println("Entrou");
//		}
//		if(domain.get(resto).equalsIgnoreCase(MAX_VAL)){
//			novoId[0] = domain.get(MIN_VAL).charAt(0);
//			novoId[1] = novoId[1];
//		}
//		if(unidade <= Long.parseLong(MAX_VAL)){ //unidade menor ou igual a 34
//			novoId[0] = domain.get(new Long(unidade)).charAt(0);  //A para B, B para C
//		} else { //if(unidade > Long.parseLong(MAX_VAL)) 
//			novoId[0] = domain.get(Long.parseLong(MIN_VAL)).charAt(0);
//			
//		}
//			
//		if(dezena <= Long.parseLong(MAX_VAL)){ //unidade menor ou igual a 34
//			novoId[1] = domain.get(new Long(dezena)).charAt(0);  //A para B, B para C
//		} else { //if(dezena > Long.parseLong(MAX_VAL)) 
//			//novoId[0] = domain.get(Long.parseLong(MIN_VAL)).charAt(0);
//			novoId[1] = domain.get(Long.parseLong(MIN_VAL)).charAt(0);
//		}
		
			
		
		
		//domain.get(++id);
		
		//System.out.println("Id: "+id+" Dezena:"+dezena+" Unidade: "+unidade+" New Id: "+novoId[2] +""+novoId[1] +""+novoId[0]);
		//unidade++;
		
	}
	
	
//	} else {
//	novoId[0] = domain.get(resto).charAt(0);
//	novoId[1] = domain.get(quociente).charAt(0);
//	System.out.println("Id: "+id+" Quociente:"+quociente+" Resto: "+resto+" New Id: "+novoId[2] +""+novoId[1] +""+novoId[0]);	

	private static void init() {
		
		getDomain().put(0L, "9");
		getDomain().put(1L, "Y");
		getDomain().put(2L, "V");
		getDomain().put(3L, "D");
		getDomain().put(4L, "E");
		getDomain().put(5L, "6");
		getDomain().put(6L, "F");
		getDomain().put(7L, "G");
		getDomain().put(8L, "I");
		getDomain().put(9L, "4");
		getDomain().put(10L, "J");
		getDomain().put(11L, "K");
		getDomain().put(12L, "7");
		getDomain().put(13L, "L");
		getDomain().put(14L, "B");
		getDomain().put(15L, "M");
		getDomain().put(16L, "3");
		getDomain().put(17L, "P");
		getDomain().put(18L, "Q");
		getDomain().put(19L, "R");
		getDomain().put(20L, "C");
		getDomain().put(21L, "U");
		getDomain().put(22L, "0");
		getDomain().put(23L, "S");
		getDomain().put(24L, "T");
		getDomain().put(25L, "W");
		getDomain().put(26L, "5");
		getDomain().put(27L, "X");
		getDomain().put(28L, "H");
		getDomain().put(29L, "Z");
		getDomain().put(30L, "1");
		getDomain().put(31L, "A");
		getDomain().put(32L, "2");
		getDomain().put(33L, "N");
		getDomain().put(34L, "8");
		
		
	}


}
	
	
	//char [] arrayAlfabeto = alfabeto.toCharArray();
//	char [] arrayId = id.toCharArray();
	
//	for(int i=0;i < arrayAlfabeto.length; i++){
//		System.out.println("index:"+i+" char:"+arrayAlfabeto[i]);
//	}
	
//	for(int i=0; i < arrayId.length; i++ ){
//		System.out.println("index:"+i+" char:"+arrayId[i]);
//	}


