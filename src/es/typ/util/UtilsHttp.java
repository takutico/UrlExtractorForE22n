package es.typ.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Source;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.typ.extractor.Extractor;
import es.typ.extractor.FilterBean;

public class UtilsHttp {

	/**
	 * Se comprueba que el httpResponse devuelva OK
	 * 	Ejemplos: 	HTTP_OK 			= HTTP Status-Code 200: OK.
	 * 				HTTP_BAD_REQUEST 	= HTTP Status-Code 400: Bad Request.
	 * 				HTTP_UNAUTHORIZED 	= HTTP Status-Code 401: Unauthorized.
	 * 				HTTP_FORBIDDEN 		= HTTP Status-Code 403: Forbidden.
	 * 				HTTP_NOT_FOUND		= HTTP Status-Code 404: Not Found.
	 * 				HTTP_INTERNAL_ERROR = HTTP Status-Code 500: Internal Server Error.
	 * 				HTTP_NOT_IMPLEMENTED= HTTP Status-Code 501: Not Implemented.
	 * 				HTTP_BAD_GATEWAY 	= HTTP Status-Code 502: Bad Gateway.
	 * 				HTTP_UNAVAILABLE 	= HTTP Status-Code 503: Service Unavailable.
	 * 
	 * @param urlStr
	 * @return
	 */
	public static boolean IsHttpResponseERROR(String urlStr, HttpURLConnection con){
		try {
			HttpURLConnection.setFollowRedirects(false);
		    return con.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN 
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_NOT_IMPLEMENTED
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND
		    		|| con.getResponseCode() == HttpURLConnection.HTTP_BAD_GATEWAY;
		    
		} catch (UnknownHostException e) {
			System.out.println("523 - No se ha podido reconocer el host de la url");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("467 - MalformedURLException - "+urlStr);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Se comprueba que se responde un 301 0 302. En el caso de que se obtenga esta respuesta se devuelve true
	 * 				HTTP_MOVED_PERM 	= HTTP Status-Code 301: Moved Permanently.
	 * 				HTTP_MOVED_TEMP 	= HTTP Status-Code 302: Temporary Redirect.
	 * 
	 * @param urlStr no se utiliza
	 * @param con
	 * @param count contador para el numero de intentos de connection
	 * @return boolean
	 */
	public static boolean IsHttpResponseMoved(String urlStr, HttpURLConnection con, int count){
		try {
			HttpURLConnection.setFollowRedirects(false);
		    
			if((con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM 
		    || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
		    && count <= Extractor.NUM_MAX_INTENTO_RECOLECTOR){
		    	//IsHttpResponseMoved(urlStr, con, count++);
		    	return true;
		    }
		} catch (MalformedURLException e) {
			System.out.println("489 - MalformedURLException - "+con.getURL().toString());
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * Se obtiene la url a la que se es redireccionado 
	 * @param urlStr
	 * @param con
	 * @return url
	 */
	@Deprecated public static String getUrlHttpResponseMoved(String urlStr, HttpURLConnection con){
		HttpURLConnection.setFollowRedirects(true);
		urlStr = con.getHeaderField("Location");
		HttpURLConnection.setFollowRedirects(false);
	    
		return urlStr;
	}
	
	
	/**
	 * Se encanga de extraer los links de una connection
	 * @param con la connection para obtener los links
	 * @param contador se utiliza para un numero maximo de intento de lectura de la connection
	 * @param isConnected no lo recuerdo
	 * @return
	 */
	public static List<String> ExtractLinks(HttpURLConnection con, int contador, boolean isConnected) {
	    ArrayList<String> result = new ArrayList<String>();
	       
	    // No se permite un numero maximo de SocketTimeoutException
	    if(contador >= Extractor.NUM_MAX_INTENTO_RECOLECTOR) return result;
	    
		try {
			String urlObtenidas = "";
			
			Document doc;
			// ANYAPA feo de cojones
			if(isConnected){
				Source source = new Source(con);
				doc = Jsoup.parse(source.toString());
			} else{
				doc = Jsoup.connect(con.getURL().toString()).get();				
			}
			//Document doc = Jsoup.parse(source.toString());
			Elements links = doc.select("a[href]");
		    for (Element link : links) {
		    	if(!link.attr("abs:href").isEmpty() && !link.attr("abs:href").contains("javascript:")){
			    	if(link.attr("abs:href").contains(Extractor.urlBaseBase)){
			    		if(!(link.attr("abs:href").contains("#")
			    				|| link.attr("abs:href").toLowerCase().startsWith("mailto:") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".zip") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".rar") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".dwg") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".jpg") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".jpeg") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".gif") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".png") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".xls") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".doc") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".xdoc") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".txt") 
			    				|| link.attr("abs:href").toLowerCase().endsWith(".pdf"))){
			    			result.add(link.attr("abs:href"));
			    			urlObtenidas += "; "+link.attr("abs:href");
			    		}
			    	}		
			    }
		    }
		    
		    if(Extractor.MOSTRAR_LINK_EXTRAIDO_CURL) System.out.println(urlObtenidas);
		    
		} catch (SocketTimeoutException se){
			//se.printStackTrace();
			System.out.println("598 - SocketTimeoutException, intento "+contador+" de "+con.getURL().getHost());
			ExtractLinks(con, contador++, isConnected);
			
		} catch (HttpStatusException he){
			//he.printStackTrace();
			Extractor.hsURLAllSite.remove(he.getUrl());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		//finally{
			return result;
		//}
		
	}	
	

	/**
	 * Se obtienen el objeto connection. En el caso de que el content-type venga como excel o word
	 * 	devuelve null. Si se obtiene un 404 se devuelve null.
	 * @param  urlStr
	 * @param  filterBean el filtrado realizado en el frame
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection GetConnection(String urlStr, FilterBean filterBean) throws IOException{
		
		// Se establece la conexion
		URL url = new URL(urlStr);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestProperty("User-Agent", Extractor.MY_USER_AGENT);
	    
	    /* Esto es por si a la pagina se le cargan los datos mediante javascript*/
	    if(filterBean.isTratarComoScript()){
	    	con.setDoOutput(true);
		    OutputStreamWriter out = new OutputStreamWriter(
		    con.getOutputStream());
		    
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
	    
	    // Esto es por si es un pdf o ms-word
	    if(con.getHeaderField("Content-Type").equals("application/pdf") || con.getHeaderField("Content-Type").equals("application/ms-word")){
	    	System.out.println("url no valida (Content-type: application/pdf or application/ms-word) = "+filterBean.getUrlStr());
	    	Extractor.AppendToLog("url no valida (Content-type: application/pdf) = "+filterBean.getUrlStr());
	    	return null;
	    }
	    
	    //TODO aki se tendria que comprobar esta parte
	    //String aceptEncoding = conn.getRequestProperty("Accept-Encoding");
	    //conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
	    
	    // Si la url da un 404, etc... en el response se sale
	    if(UtilsHttp.IsHttpResponseERROR(filterBean.getUrlStr(), con)){
	    	System.out.println("url no valida = "+filterBean.getUrlStr());
	    	Extractor.AppendToLog("url no valida = "+filterBean.getUrlStr());
	    	return null;
	    }
		    
	    return con;
	}
	


}
