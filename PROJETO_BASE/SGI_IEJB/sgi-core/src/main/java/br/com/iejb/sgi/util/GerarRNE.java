package br.com.iejb.sgi.util;

import java.util.Random;


//TODO: Auto-generated Javadoc
/**
* @author YESUS CASTILLO
*/
public class GerarRNE {

	/**
	 * Valida.
	 *
	 * @param rne the rne
	 * @return true, if successful
	 */
	public boolean valida(String rne){
		String tabelaAlfa [] = {"1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"}; 
		int ind = 0;
		int quoc = 0;
		int rest = 0;
		int digit = 0;
		int result = 0;
		String v8 = "";
		
		
		if(rne.length() != 8){
			return false;
		}
		
		char numero []= rne.substring(1, 7).toCharArray();
		
		if(isNumero(rne.charAt(0))){
			return false;
		}
		for (int i = 1; i <= 6; i++) {
			if(!isNumero(rne.charAt(i))){
				return false;
			}
		}
		
		for(int i = 0; i < tabelaAlfa.length; i++){
			if(rne.substring(0, 1).equals(tabelaAlfa[i])){
				ind = i + 1;
				break;
			}
		}
		result += ind * 8;
		int controle = 7; 
		for(int i = 0; i < numero.length; i++){
			result += Integer.parseInt(Character.toString(numero[i])) * controle;
			controle--;
		}
		quoc = result / 37;
		rest = result - (quoc * 37);
		if(rest == 0 || rest == 1){
			v8 =  "0";
		}else{
			digit = 37 - rest;
			v8 = tabelaAlfa[digit-1];
		}
		if(!v8.equals(rne.substring(7))){
			return false;
		}
		return true;   
	}
	
	/**
	 * Gets the rne.
	 *
	 * @return the rne
	 */
	public String getRne(){
		String rne = "";
		String tabelaAlfa [] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		String tabelaNumerica [] = {"1","2","3","4","5","6","7","8","9"};
		Random r = new Random(); 
		rne = tabelaAlfa[r.nextInt(26)] ;
		for (int i = 0; i < 6; i++) {
			rne += tabelaNumerica[r.nextInt(9)] ;
		}
		rne += tabelaAlfa[r.nextInt(26)] ;
		return rne;
	}
	
	/**
	 * Checks if is numero.
	 *
	 * @param valor the valor
	 * @return true, if is numero
	 */
	private boolean isNumero(char valor){
		boolean resposta = false;
		char numeros [] = {'1','2','3','4','5','6','7','8','9','0'};
		for (int i = 0; i < numeros.length; i++) {
			if(valor == numeros[i]){
				resposta = true;
				break;
			}
		}
		return resposta;
	}
	
	/**
	 * Aleatorio.
	 */
	public static void aleatorio(){
		Random r = new Random();
		int valor = 0;
		for (int i = 0; i < 100000; i++) {
			valor = r.nextInt(37);
			if(valor > 37) {
				System.out.println(valor);
			}
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		for(int i = 0 ; i < 1000; i++){
			System.out.println(new GerarRNE().getRne());
		}
	}
}
