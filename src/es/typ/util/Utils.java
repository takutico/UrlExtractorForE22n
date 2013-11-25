package es.typ.util;

import es.typ.extractor.UrlBean;

public class Utils {
	
	
	/**
	 * Comprueba los textos de los body de cada Bean
	 * 
	 * @param bean1
	 * @param bean2
	 * @return boolean
	 */
	public static boolean IsTextosBodyIguales(UrlBean bean1, UrlBean bean2){
		try{
			long uno = 0;
			long dos = 0;
			
			// Esto es que son la misma url
			if(bean1.getUrl().equals(bean2.getUrl())){
				return true;
			}
			// 
			if(bean1.getBodyNumPalabras() == 0 || (bean1.getBodyNumPalabras() != bean2.getBodyNumPalabras())){
				return false;
			} else{
				if(bean1.getBodyTxt().length() == bean2.getBodyTxt().length()){
					for(int i = 0; i <= bean1.getBodyTxt().length() - 1; i++){
						uno += bean1.getBodyTxt().charAt(i);
						dos += bean2.getBodyTxt().charAt(i);
					}
				} else{
					return false;
				}
			}
			if(uno == dos){ // Es el mismo contenido
				return true;
			}
		} catch(StringIndexOutOfBoundsException e){
			e.printStackTrace();
			return false;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	
	/**
	 * Funcion que elimina "/" final y los espacios
	 * @param urlStr
	 * @return con la url correcta
	 */
	public static String GetUrlNormalizada(String urlStr){
		// Si la url introducida termina en '/', esta se elimina
		if(urlStr.endsWith("/")){
			urlStr = urlStr.substring(0, urlStr.length()-1);
		}
		// Se eliminan los espacios en blanco
		return urlStr.trim();
	}

}
