package es.typ.extractor;

import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ExtractText {
	
	/**TODO: utilizar el siguiente array para la comprobacion de palabras restringidas*/
	private String[] palabrasRestringidas = {
			"\n"       , "\r\n"    , "\n\r"     , "&OElig;"  , "&oelig;" , "&Scaron;" , "&scaron;" , "&Yuml;"   , "&fnof;"   , "&circ;"  , 
			"&tilde;"  , "&ensp;"  , "&emsp;"   ,
            "&thinsp;" , "&zwnj;"  , "&zwj;"    , "&lrm;"    , "&rlm;"    , "&ndash;"  , "&mdash;" , "&lsquo;"  , "&rsquo;"  , "&sbquo;",
            "&ldquo;"  , "&rdquo;" , "&bdquo;"  , "&dagger;" , "&Dagger;" , "&bull;"   , "&hellip;", "&permil;" , "&prime;"  , "&Prime;",
            "&lsaquo;" , "&rsaquo;", "&oline;"  , "&euro;"   , "&trade;"  , "&larr;"   , "&uarr;"  , "&rarr;"   , "&darr;"   , "&harr;" ,
            "&crarr;"  , "&lceil;" , "&rceil;"  , "&lfloor;" , "&rfloor;" , "&loz;"    , "&spades;", "&clubs;"  , "&hearts;" , "&diams;",
            "&Alpha;"  , "&Beta;"  , "&Gamma;"  , "&Delta;"  , "&Epsilon;", "&Zeta;"   , "&Eta;"   , "&Theta;"  , "&Iota;"   , "&Kappa;",
            "&Lambda;" , "&Mu;"    , "&Nu;"     , "&Xi;"     , "&Omicron;", "&Pi;"     , "&Rho;"   , "&Sigma;"  , "&Tau;"    , "&Upsilon;",
            "&Phi;"    , "&Chi;"   , "&Psi;"    , "&Omega;"  , "&alpha;"  , "&beta;"   , "&gamma;" , "&delta;"  , "&epsilon;", "&zeta;",
            "&eta;"    , "&theta;" , "&iota;"   , "&kappa;"  , "&lambda;" , "&mu;"     , "&nu;"    , "&xi;"     , "&omicron;", "&pi;",
            "&rho;"    , "&sigmaf;", "&sigma;"  , "&tau;"    , "&upsilon;", "&phi;"    , "&chi;"   , "&psi;"    , "&omega;"  , "&thetasym;",
            "&upsih;"  , "&piv;"   , "&forall;" , "&part;"   , "&exist;"  , "&empty;"  , "&nabla;" , "&isin;"   , "&notin;"  , "&ni;",
            "&prod;"   , "&sum;"   , "&minus;"  , "&lowast;" , "&radic;"  , "&prop;"   , "&infin;" , "&ang;"    , "&and;"    , "&or;",
            "&cap;"    , "&cup;"   , "&int;"    , "&there4;" , "&sim;"    , "&cong;"   , "&asymp;" , "&ne;"     , "&equiv;"  , "&le;",
            "&ge;"     , "&sub;"   , "&sup;"    , "&nsub;"   , "&sube;"   , "&supe;"   , "&oplus;" , "&otimes;" , "&perp;"   , "&sdot;",
            "&nbsp;"   , "&iexcl;" , "&cent;"   , "&pound;"  , "&curren;" , "&yen;"    , "&brvbar;", "&sect;"   , "&uml;"       , "&copy;",
            "&ordf;"   , "&laquo;" , "&not;"    , "&shy;"    , "&reg;"    , "&macr;"   , "&deg;"   , "&plusmn;" , "&sup2;"   , "&sup3;",
            "&acute;"  , "&micro;" , "&para;"   , "&middot;" , "&cedil;"  , "&sup1;"   , "&ordm;"  , "&raquo;"  , "&frac14;" , "&frac12;",
            "&frac34;" , "&iquest;", "&times;"  , "&divide;" , "."        , ","        , ";"       , "-"        , "_"        , "\""       ,
            "\""       , "!"       , "?"        , "$"        , "%"        , "&"        , "("       ,
            ")"        , "="       , "["        , "]"        , "{"        , "}"        , " "       , ""};

	
	/**
	 * Extrae el texto de un bean, esto se utliza principalmente en el re-analisis. En esta funcion tb se eliminan los datos del filtrado.
	 * @param bean bean con los datos
	 * @param filterBean
	 * @return
	 */
	public UrlBean extractTextFromUrlBean(UrlBean bean, FilterBean filterBean){
		UrlBean newBean = new UrlBean();
		//Config.LoggerProvider = LoggerProvider.DISABLED;
		try{
		    Source source = new Source(bean.getHtmlCode());
			source.fullSequentialParse(); // Call fullSequentialParse manually as most of the source will be parsed.
			
			// START pa eliminar parte del html
			if(!filterBean.isCountExcluded()){ // Esto es para que lo cuente en la primera url
				if(!filterBean.getContentCodeFilter_0().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_0());
				}
				if(!filterBean.getContentCodeFilter_1().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_1());
				}
				if(!filterBean.getContentCodeFilter_2().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_2());
				}
				if(!filterBean.getContentCodeFilter_3().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_3());
				}
			} else{
				filterBean.setCountExcluded(false);
			}
			// END pa intentar eliminar parte del html
			
			TextExtractor textExtractor = new TextExtractor(source) {
				public boolean excludeElement(StartTag startTag) {
					Config.LoggerProvider = LoggerProvider.DISABLED;
					return startTag.getName()==HTMLElementName.TITLE || startTag.getName()==HTMLElementName.OPTION;
				}
			};
			String body = textExtractor.setIncludeAttributes(false).toString();
			bean.setBodyNumPalabras(body==null ? 0 : wordcount(body));

			// Se guarda en un hashSet todas las palabras para evitar las palabras duplicadas
			String[] unicos = body.split(" ");
			ArrayList<String> alUnicos = new ArrayList<String>();
			// Si el texto introducido es de un solo caracter y no es una lera, no se cuenta
			for(String cad: unicos){
				if(cad.length() == 1){
					if((cad.charAt(0) > 64 && cad.charAt(0) < 91) || (cad.charAt(0) > 96 && cad.charAt(0) < 123))
						alUnicos.add(cad);
				} else{
					alUnicos.add(cad);
				}
			}
			
			newBean.setUrl(bean.getUrl());
			newBean.setTitle(alUnicos.size());

		} catch (Exception e){
			System.out.println("84 - " + e.toString());
		}
		return newBean;
	}
	
	
	/**
	 *  Extrae el texto de una url.
	 * @param url la direccion para la extraccion del texto
	 * @param filterBean 
	 * @param count numero maximo de intentos para la extraccion de los datos
	 * @return
	 */
	public UrlBean extractText(String url, FilterBean filterBean, int count){
		UrlBean urlBean = new UrlBean();
		urlBean.setUrl(url);

		if(count >= Extractor.NUM_MAX_INTENTO_RECOLECTOR){
			urlBean.setTitle(0);
			urlBean.setUnicos(0);
			return urlBean;
		}

		//Config.LoggerProvider = LoggerProvider.DISABLED;
		try{
			String sourceUrlString=url;
	
			if (sourceUrlString.indexOf(':')==-1) sourceUrlString="file:"+sourceUrlString;
			MicrosoftConditionalCommentTagTypes.register();
			PHPTagTypes.register();
			PHPTagTypes.PHP_SHORT.deregister(); // remove PHP short tags for this example otherwise they override processing instructions
			MasonTagTypes.register();
			
			URL urlConn = new URL(sourceUrlString);
		    URLConnection conn = urlConn.openConnection();
		    //conn.setConnectTimeout(1000);
		    //conn.setReadTimeout(1000);
		    conn.setRequestProperty("User-Agent", Extractor.MY_USER_AGENT);
// StartEsto es para hacer un post
		    /* Esto es por si a la pagina se le cargan los datos mediante javascript*/
		    if(filterBean.isTratarComoScript()){
		    	conn.setDoOutput(true);
			    OutputStreamWriter out = new OutputStreamWriter(
			    conn.getOutputStream());
			    
			    String[] parametrosPost = null;
			    if(filterBean.getPostParamsScript().contains(",")){
			    	parametrosPost = filterBean.getPostParamsScript().split(",");
				} else if(filterBean.getPostParamsScript().contains(";")){
					parametrosPost = filterBean.getPostParamsScript().split(";");
				};
			    
				if(parametrosPost != null){
				    for(String param: parametrosPost){
				    	out.write(param);
				    }
			    out.close();
				}
		    }
// End post
		    //String aceptEncoding = conn.getRequestProperty("Accept-Encoding");
		    //conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
		    Source source = new Source(conn);
		    if(Extractor.MOSTRAR_CODIGO_HTML_CURL) System.out.println(source); // Con esto se imprime el html
			source.fullSequentialParse(); // Call fullSequentialParse manually as most of the source will be parsed.
			
			urlBean.setHtmlCode(source.toString());
			// START pa eliminar parte del html
			if(!filterBean.isCountExcluded()){ // Esto es para que lo cuente en la primera url
				/**TODO: hay que comprobar si en la primera URL tienen los Strings*/
				if(!filterBean.getContentCodeFilter_0().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_0());
				}
				if(!filterBean.getContentCodeFilter_1().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_1());
				}
				if(!filterBean.getContentCodeFilter_2().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_2());
				}
				if(!filterBean.getContentCodeFilter_3().equals("")){
					source = removeText(source, filterBean.getContentCodeFilter_3());
				}
			} else{
				filterBean.setCountExcluded(false);
			}
			// END pa intentar eliminar parte del html
			TextExtractor textExtractor;
			//Config.LoggerProvider = LoggerProvider.DISABLED;
			textExtractor = new TextExtractor(source) {
				public boolean excludeElement(StartTag startTag) {
					Config.LoggerProvider = LoggerProvider.DISABLED;
					return startTag.getName()==HTMLElementName.TITLE || startTag.getName()==HTMLElementName.OPTION;
				}
			};
			String body = textExtractor.setIncludeAttributes(false).toString();
			urlBean.setBodyNumPalabras(body==null ? 0 : wordcount(body));

			// Se guarda el texto extraido en una sola linea
			body = body.toString().trim().replaceAll("\\s+", " ");
			urlBean.setBodyTxt(body);
			
			if(Extractor.MOSTRAR_TEXTO_EXTRAIDO_CURL) System.out.println(body); // Con esto se muestra el texto extraido
			
			// Se guarda en un hashSet todas las palabras para evitar las palabras duplicadas
			String[] unicos = body.split(" ");
			HashSet<String> hsUnicos = new HashSet<String>();
			ArrayList<String> alUnicos = new ArrayList<String>();
			
			// Si el texto introducido es de un solo caracter y no es una lera, no se cuenta
			for(String cad: unicos){
				if(cad.length() == 1){
					if((cad.charAt(0) > 64 && cad.charAt(0) < 91) || (cad.charAt(0) > 96 && cad.charAt(0) < 123))
						hsUnicos.add(cad);
						alUnicos.add(cad);
				} else{
					hsUnicos.add(cad);
					alUnicos.add(cad);
				}
			}
			
			//Extractor.addHsUnicos(hsUnicos);
			urlBean.setTitle(alUnicos.size());
			urlBean.setUnicos(hsUnicos.size());

		} catch (Exception e){
			System.out.println("180 - " + e.toString());
			e.printStackTrace();
			//extractText(url, filterBean, count++);
			
		}
		return urlBean;
	}
	
	/**
	 * Elimina una cadena dentro de un una url
	 * 
	 * @param source Objeto Source de donde se quiere extraer la cadena
	 * @param strToRemove String que se quiere extraer
	 * @return
	 */
	private Source removeText(Source source, String strToRemove){
		// Se normaliza el html
		String cadena = source.toString().trim().replaceAll("\\s+", " ");
		cadena = cadena.toString().trim().replaceAll(">\\s+<", "><");
		// Se normaliza la cadena
		String cadena2 = strToRemove.trim().replaceAll("\\s+", " ");
		cadena2 = cadena2.toString().trim().replaceAll(">\\s+<", "><");
		// Se reemplaza la cadena
		String result = cadena.replace(cadena2, "");
		// Se reestablece el source al nuevo String
		return new Source(result);
	}
	
	
	/**
	 * cuenta la palabras de un string, las palabras se cuentan por espacios 
	 * @param line string a leer
	 * @return
	 */
	private static long wordcount(String line){
		   String trim = line.trim();
		   if (trim.isEmpty()) return 0;
		   return trim.split("\\s+").length;
	}
	
	/*
	 * Estas son algunas de las fucniones para futuras versiones. Con esto se pueden filtrar los textos que se quieren extraer 
	 */

	private static String getBody(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}
	
	private static String getTitle(Source source) {
		Element titleElement=source.getFirstElement(HTMLElementName.TITLE);
		if (titleElement==null) return null;
		// TITLE element never contains other tags so just decode it collapsing whitespace:
		return CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
	}

	private static String getMetaValue(Source source, String key) {
		for (int pos=0; pos<source.length();) {
			StartTag startTag=source.getNextStartTag(pos,"name",key,false);
			if (startTag==null) return null;
			if (startTag.getName()==HTMLElementName.META)
				return startTag.getAttributeValue("content"); // Attribute values are automatically decoded
			pos=startTag.getEnd();
		}
		return null;
	}
	
}
