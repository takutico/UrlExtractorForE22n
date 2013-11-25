package es.typ.extractor;

import es.typ.util.UtilsHttp;
import es.typ.util.Utils;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import es.typ.swing.FramePrincipal;

/**
 * TODO:
 * 	multi-hilo
 * 	que la recoleccion de datos no sea en modo arbol, sino que cada url analizada se almacena en un array y los links en un hashset
 * 	ojo con la variable syncronizada, esta creando un embudo
 * 
 */
public class Extractor extends SwingWorker<Void, String>{
	
	// TODO meter estos datos en un properties para no tener que estar compilando por cada cambio
	// Parametros para establecer los datos a mostrar en consola
	public static final boolean MOSTRAR_CODIGO_HTML_CURL 	= false;
	public static final boolean MOSTRAR_TEXTO_EXTRAIDO_CURL = false;
	public static final boolean MOSTRAR_LINK_EXTRAIDO_CURL 	= false;
	public static final boolean MOSTRAR_MISMOS_DATOS_TABLA 	= true;
	// Numero maximo de intentos que se raliza en la conection
	public static final int 	NUM_MAX_INTENTO_RECOLECTOR = 7;
	// Num maximo de hilos
	private static final int 	NUM_MAX_THREAD = 20;
	// User-Agent
	public static final String MY_USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:11.0) Gecko/20100101 Firefox/11.0";
	
	public static final HashSet<String> 		 hsURLAllSite = new HashSet<String>();
	private static final HashSet<String> 		 hsTmp = new HashSet<String>();
	private static final HashSet<String> 		 hsUrlDescartadas = new HashSet<String>();
	private static final HashSet<String> 		 hsUnicos = new HashSet<String>();
	private static ArrayList<UrlBean> 			 alSinContenidoRepetidos = new ArrayList<UrlBean>();
	protected static ArrayList<UrlBean> 		 alSinContenidoRepetidosTmp = new ArrayList<UrlBean>();
	public static final HashMap<String, UrlBean> hmUrlBean = new HashMap<String, UrlBean>();
	// Para el calculo de totales
	private static long totalPalabras 	= 0;
	private static long totalUnicos 	= 0;
	private static long totalTitle 		= 0;
	// Strign que se utilizan para las url
	private static String urlBase 		= "http://www.top10garantizado.com";
	public static String urlBaseBase 	= "top10garantizado.com";
	
	protected static FilterBean filterBean;
	protected static ExecutorService execServ = Executors.newFixedThreadPool(NUM_MAX_THREAD);
	protected static Iterator<String> it;
	
	public static ArrayList<UrlBean> urlBeanAl = new ArrayList();
	
	
	public Extractor(FilterBean filterBean) {
		super();
		Extractor.filterBean = filterBean;
		// Se inicializan los datos
		urlBase = filterBean.getUrlStr();		
		clearData();
	}
	
	
	/**
	 * Funcion que se encarga de limpiar algunos datos
	 */
	private void clearData(){
		// Se limpian y se inicializan algunos datos 
		hsTmp.clear();
		hmUrlBean.clear();
		hsUrlDescartadas.clear();
		totalTitle = 0;
		totalUnicos = 0;
		totalPalabras = 0;
		// Se establecen algunos datos del frame
		FramePrincipal.taLog.setRows(0);
		AppendToLog("");
		FramePrincipal.taTotales.setText("");
		FramePrincipal.taUrlDescartadas.setText("");
		// Se estable el numero maximo de hilos
		execServ = Executors.newFixedThreadPool(NUM_MAX_THREAD);
		urlBeanAl = new ArrayList<UrlBean>();
	}
	
	
	/**
	 * Se encarga de coprubar si la url esta bien formada
	 * @return
	 */
	private boolean isUrlCorrecta(){
		// Se comprueba que la url este bien formada
		if(filterBean.getUrlStr() == null 
				|| filterBean.getUrlStr().equals("")
				|| filterBean.getUrlStr().equals("http://")
				|| filterBean.getUrlStr().equals("http://www")
				|| filterBean.getUrlStr().equals("http://www.")){
			try {
				new URL(filterBean.getUrlStr());
			} catch (MalformedURLException e) {
				AppendToLog("92 - URL mal formada. Inserte una URL válida");
				return false;
			}
			AppendToLog("Inserte una URL válida");
			return false;
		}
		return true;
	}
	
	
	/**
	 * Obtiene la url correcta.
	 * TODO se tendria que meter en UTILS
	 * @param con
	 * @return
	 */
	private String getCorrectUrl(HttpURLConnection con){
		try {
			HttpURLConnection.setFollowRedirects(true);
			return Utils.GetUrlNormalizada(con.getURL().toString());
		} catch (Exception e) {
			System.out.println("119 - " + e.toString());
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * Accion que se realiza tras pulsar el btn de analizar
	 * Obtiene todos los links de las url, almacena la informacion,...
	 */
	public void Analizar() {
		// Se limpian algunos datos
		hsURLAllSite.clear(); // Esta esta fuera de clearData, ya que en el re-analisis utiliza este hs
		alSinContenidoRepetidos.clear();
		clearData();
		// Se comprueba si es una url valida
		if(!isUrlCorrecta()) return;

		try {
			// Se elimina la "/" final y los espacios en blanco
			filterBean.setUrlStr(Utils.GetUrlNormalizada(filterBean.getUrlStr()));
			URL url = new URL(filterBean.getUrlStr());
			// Se establece la url base(el subdominio)
			if(url.getHost().startsWith("www.")){
				urlBaseBase = url.getHost().replaceFirst("www.", "");
			}else{
				urlBaseBase = url.getHost();
			}
		} catch (MalformedURLException e) {
			AppendToLog("111 - URL mal formada. Inserte una URL válida");
		}

		// Si se ha seleccionado analizar solo la url actual
		if(filterBean.isOnlyThisUrl()){
			analizeThis();
			return;
		}
		// Se ejecuta el primer recolector con ls urlBase
		recolectorUrl(urlBase);
		// En este punto ya se tienen todas las url del site en hsURLAllSite
		execServ.shutdown();
		try {
			execServ.awaitTermination(10, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Se hace una copia del hsFinished en hsTmp y se trata estos datos
		hsTmp.addAll(hsURLAllSite);
		// Se pasa el comparador de contenidos
		System.out.println("--->  Se pasa el comparador de contenidos");
		comparadorContenidos();
	}

	
	/**
	 * Analiza una sola url
	 */
	private void analizeThis(){
		try {
			URL url = new URL(filterBean.getUrlStr());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    con.setRequestProperty("User-Agent", MY_USER_AGENT);
		    con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		    
		    // x si viene un 404
		    if(UtilsHttp.IsHttpResponseERROR(filterBean.getUrlStr(), con)){
		    	System.out.println("url no valida = "+filterBean.getUrlStr());
		    	AppendToLog("url no valida = "+filterBean.getUrlStr());
		    	return;
		    }
		    
		    if(UtilsHttp.IsHttpResponseMoved(filterBean.getUrlStr(), con, 0)){
		    	return;
		    }
		    
		    // Esto es por si es un pdf o ms-word
		    if(con.getHeaderField("Content-Type").equals("application/pdf") || con.getHeaderField("Content-Type").equals("application/ms-word")){
		    	System.out.println("url no valida (Content-type: application/pdf or application/ms-word) = "+filterBean.getUrlStr());
		    	AppendToLog("url no valida (Content-type: application/pdf) = "+filterBean.getUrlStr());
		    	return;
		    }
		    // Se obtiene la url valida
		    String correctUrl = getCorrectUrl(con);
		    
		    // Se inserta la nueva url en el bean
		    filterBean.setUrlStr(correctUrl);
		    hsTmp.add(filterBean.getUrlStr());
			extractTextFromHs(hsTmp);
			// Se pasa el comparador de contenidos
			System.out.println("--->  Se pasa el comparador de contenidos");
			comparadorContenidos();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Funcion que se ejecuta tras pulsar el boton de re-analizar
	 */
	public void reAnalizar(){
		// Se comprueba que no se haya modificado la url
		if(!filterBean.getUrlStr().equals(urlBase)){
			AppendToLog("Ha modificado la URL, vuelva a analizar");
			return;
		}
		
		// Se limpian algunos datos
		hsTmp.clear();
		totalPalabras = 0;
		totalTitle = 0;
		FramePrincipal.taUrlDescartadas.setText("");
		// Se hace una copia del hsFinished en hsTmp y se trata estos datos 
		hsTmp.addAll(hsURLAllSite);
		
		// En el caso de haber insertado un filtrado por url las url se eliminan del hashSet
		if(filterBean.getUrlFilter() != null && !filterBean.getUrlFilter().equals("") && filterBean.getUrlFilter().length() > 0){
			filterBean.setUrlFilter(filterBean.getUrlFilter().replaceAll(" ", ""));
			// El filtrado por url puede venir separado por , o por ;
			String[] urlFilters = {filterBean.getUrlFilter()};
			if(filterBean.getUrlFilter().contains(",")){
				urlFilters = filterBean.getUrlFilter().split(",");
			} else if(filterBean.getUrlFilter().contains(";")){
				urlFilters = filterBean.getUrlFilter().split(";");
			}
			// Se obtienen las url del contra filtrado
			if(filterBean.getUrlNotFilter() != null && !filterBean.getUrlNotFilter().equals("") && filterBean.getUrlNotFilter().length() > 0){
				filterBean.setUrlNotFilter(filterBean.getUrlNotFilter().replaceAll(" ", ""));
				// El filtrado por url puede venir separado por , o por ;
				String[] urlNotFilters = {filterBean.getUrlNotFilter()};
				if(filterBean.getUrlNotFilter().contains(",")){
					urlNotFilters = filterBean.getUrlNotFilter().split(",");
				} else if(filterBean.getUrlFilter().contains(";")){
					urlNotFilters = filterBean.getUrlNotFilter().split(";");
				}
				// Se elimina del hasset las url que contengan una cadena introducida en urlfilter
				// xo si contiene una cadena de notFiltered no se elimina
				// ej: se eliminan todas las url que contenga "noticias" xo se mantienen las que contengan "noticias_2"
				for(String filter: urlFilters){
					for(String link: hsURLAllSite){
						// el link contiene la cadena a filtrar
						if(link.contains(filter)){
							for(String notFilter: urlNotFilters){
								// Si no contiene la cadena de notFiltered se borra del hashset
								if(!link.contains(notFilter)){
									hsUrlDescartadas.add(link);
									FramePrincipal.taUrlDescartadas.append(link+"\n");
									publish("url descartada: "+link);
									hsTmp.remove(link);
								}
							}
						}
					}
				}
			} else{
			// Se eliminan las url del hash set que contenga parte del string del filtrado
				for(String filter: urlFilters){
					for(String link: hsURLAllSite){
						if(link.contains(filter)){
							hsUrlDescartadas.add(link);
							FramePrincipal.taUrlDescartadas.append(link+"\n");
							hsTmp.remove(link);
						}
					}
				}
			}
		}
		
		// En este punto se tiene en hsTmp todas las url filtradas
		alSinContenidoRepetidosTmp = new ArrayList<UrlBean>();
		for(UrlBean bean: alSinContenidoRepetidos){
			if(hsTmp.contains(bean.getUrl())){
				alSinContenidoRepetidosTmp.add(bean);
			}
		}

		// Aqui se tiene un array de UrlBean filtrado
		extractTextFromUrlBeanAL(alSinContenidoRepetidosTmp);
		printTableAL(alSinContenidoRepetidosTmp);
		// Se pasa el extractor y contador de palabras
		//TYP Este es el original, lo malo es que realiza una conection mas
		//if(extractTextFromHs(hsTmp)) System.out.println("Los hilos se han finalizado correctamente");
	}
	
	
	/**
	 * Lanza el comparador de contenidor. Se va comprobando de una en una todos los body de los objetos.
	 */
	public static void comparadorContenidos(){
		try{
			ArrayList<UrlBean> alTmp = new ArrayList<UrlBean>();
			final ArrayList<UrlBean> alResult = new ArrayList<UrlBean>();
			alTmp.addAll(urlBeanAl);
			
			// Se comprueba/compara uno por uno, todos los elementos del array
			for(UrlBean bean : urlBeanAl){
				for(UrlBean beanTmp : alTmp){
					if(bean.getBodyNumPalabras() > 0){
						if(Utils.IsTextosBodyIguales(bean, beanTmp)){
							// Si los datos no son iguales y no se ha insertado el dato en el array
							if(!alResult.contains(beanTmp) && !alResult.contains(bean)){
								alResult.add(bean);
								System.out.println("se inserta - "+bean.getUrl());
							}
							break;
						}
					}
				}
			}
			// Se pinta en la tabla el resultado
			printTableAL(alResult);
			alSinContenidoRepetidos.addAll(alResult);
			alSinContenidoRepetidosTmp.addAll(alResult);
		}catch(Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Se encarga de extraer el texto del hs de links
	 * @param hs Aqui es donde se almacenan todos los link que se han ido analizando
	 * @return
	 */
	@Deprecated
	private boolean extractTextFromHs(HashSet<String> hs){
		AppendToLog("");
		boolean isFinished = false;
		// Se crea un threadpool para limitar la ejecucion de los hilos
		ExecutorService exec = Executors.newFixedThreadPool(NUM_MAX_THREAD);
		
		for(String link: hs){
			TextExtractorFromUrl txtExtract = new TextExtractorFromUrl();
			// Si la url termina con "/", esta se elimina
			if(link.endsWith("/")){
				link = link.substring(0, link.length()-1);
			}
					
			txtExtract.setLink(link);
			exec.execute(new Thread(txtExtract, "RecolectorUrl_"+urlBase));
		}

		// Con esto se indica que se han ejecutado todos los hilos
		exec.shutdown();
		
		// Esto detiene la ejecucion del programa hasta que se termine todo el trabajo 
		// o hasta que se se sobre pase el numero de seguntos establecido
		try {
			isFinished = exec.awaitTermination(10, TimeUnit.HOURS);
            System.out.println("All done: " + isFinished);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}
		return isFinished;
	}
	
	
	/**
	 * Realiza la extraccion del texto de los objetos que contiene un arraylist
	 * @param urlBeanAL
	 * @return
	 */
	private boolean extractTextFromUrlBeanAL(ArrayList<UrlBean> urlBeanAL){
		AppendToLog("");
		boolean isFinished = false;
		// Se crea un threadpool para limitar la ejecucion de los hilos
		ExecutorService exec = Executors.newFixedThreadPool(NUM_MAX_THREAD);
		
		for(UrlBean bean: urlBeanAL){
			TextExtractorFromUrl txtExtract = new TextExtractorFromUrl();
			txtExtract.setBean(bean);
			exec.execute(new Thread(txtExtract, "RecolectorUrl_"+urlBase));
		}

		// Con esto se indica que se han ejecutado todos los hilos
		exec.shutdown();
		
		// Esto detiene la ejecucion del programa hasta que se termine todo el trabajo 
		// o hasta que se se sobre pase el numero de seguntos establecido
		try {
			isFinished = exec.awaitTermination(10, TimeUnit.HOURS);
            System.out.println("All done: " + isFinished);
		} catch (InterruptedException e) {
            e.printStackTrace();
		}
		return isFinished;
	}
	
	
	private void extractTextFromURL(String url, HttpURLConnection con){
		AppendToLog("Extracting text from " + url);
		TextExtractorFromUrl txtExtract = new TextExtractorFromUrl();
		// Si la url termina con "/", esta se elimina
		url = Utils.GetUrlNormalizada(url);				
		txtExtract.setLink(url);
		execServ.execute(new Thread(txtExtract, "RecolectorUrl_"+urlBase));
	}
	
	
	/**
	 * Limpia las url segun los paremetros indroducidos por en los campos de filtrado
	 * @param urlStr
	 * @return boolean
	 */
	private boolean urlFilter(String urlStr){
		// Se elimina la ultima 
		if(urlStr.endsWith("/")){
			urlStr = urlStr.substring(0, urlStr.length()-1);
		}
		filterBean.setUrlStr(filterBean.getUrlStr().trim());

		// Se comprueba la url segun el filtro insertado
		if(filterBean.getUrlFilter() != null && !filterBean.getUrlFilter().equals("") && filterBean.getUrlFilter().length() > 0){
			filterBean.setUrlFilter(filterBean.getUrlFilter().replaceAll(" ", ""));
			// El filtrado por url puede venir separado por , o por ;
			String[] urlFilters = {filterBean.getUrlFilter()};
			if(filterBean.getUrlFilter().contains(",")){
				urlFilters = filterBean.getUrlFilter().split(",");
			} else if(filterBean.getUrlFilter().contains(";")){
				urlFilters = filterBean.getUrlFilter().split(";");
			}
			// Se obtienen las url del contra filtrado
			if(filterBean.getUrlNotFilter() != null && !filterBean.getUrlNotFilter().equals("") && filterBean.getUrlNotFilter().length() > 0){
				filterBean.setUrlNotFilter(filterBean.getUrlNotFilter().replaceAll(" ", ""));
				// El filtrado por url puede venir separado por , o por ;
				String[] urlNotFilters = {filterBean.getUrlNotFilter()};
				if(filterBean.getUrlNotFilter().contains(",")){
					urlNotFilters = filterBean.getUrlNotFilter().split(",");
				} else if(filterBean.getUrlFilter().contains(";")){
					urlNotFilters = filterBean.getUrlNotFilter().split(";");
				}
				// si la url contiene la cadena que se ha insertado en el filtro se sale del metodo
				for(String filter: urlFilters){
					if(urlStr.contains(filter)){
						for(String notFilter: urlNotFilters){
							if(!urlStr.contains(notFilter)){
								return false;
							}
						}
					}
				}
			} else{
				// si la url contiene la cadena que se ha insertado en el filtro se sale del metodo
				for(String filter: urlFilters){
					if(urlStr.contains(filter)){
						return false;
					}
				}
			}
		}
		return true;
	}
	

	/**
	 * Se encarga de recolectar las url dentro del html y las almacena en el hsURLAllSite.
	 * hsFinished evita las url duplicadas
	 * 
	 * @param urlSt
	 */
	private void recolectorUrl(String urlStr){
		try{
			// Se comprueba si la url coincide con los parametros del filtrado
			if(!urlFilter(urlStr)) return;
			// Se limpian las url
			urlStr = Utils.GetUrlNormalizada(urlStr);
			
			// Se obtiene la connection
			HttpURLConnection con = UtilsHttp.GetConnection(urlStr, filterBean);
			if(con == null) return;
			
		    // Se obtiene la url valida, esto es por si viene un 302 en el response
			urlStr = getCorrectUrl(con);
			
			if(!hsURLAllSite.contains(urlStr)){
				AddElementToHsFinished(urlStr);
				// se obtiene una lista de todos los link que contiene la pagina
				List<String> links = UtilsHttp.ExtractLinks(con, 0, filterBean.isTratarComoScript());
				// Se extraen los textos de la uzrl actual
				extractTextFromURL(urlStr, con);
			    // De este modo se eliminan todos los links duplicados
		        HashSet<String> hs = new HashSet<String>();
		        hs.addAll(links);
		        links.clear();
		        links.addAll(hs);

		        publish(urlStr); // Esto lo muestra por el log
						        
			    for (String link : links) {
			    	if(!hsURLAllSite.contains(Utils.GetUrlNormalizada(link)))
			    		recolectorUrl(link);
			    }
			}
		} catch(Exception e){
			System.out.println("recolectorUrl: "+urlStr+"\n"+e.toString());
			e.printStackTrace();
		}
	}
	
	public static synchronized void AddElementToHsFinished(String url){
		Extractor.hsURLAllSite.add(url);
	}
	
	public static synchronized void AddElementsToHsFinished(List<String> urls){
		Extractor.hsURLAllSite.addAll(urls);
	}

	public static synchronized boolean containIntoHsFinished(String url){
		return Extractor.hsURLAllSite.contains(url);
	}
	
	private static void printTableAL(final ArrayList<UrlBean> alResult){
		new Thread(new Runnable() {
            public void run() {
            	int i = 0;
            	for(final UrlBean beanFinal : alResult){
	           		String[] datos = {++i+"", beanFinal.getUrl(), beanFinal.getBodyNumPalabras()+""};
	           		//hsFinished.add(beanFinal.getUrl());
	           		hsTmp.add(beanFinal.getUrl());
	           		FramePrincipal.modeloTblResultSinContenidoDuplicado.addRow(datos);
            	}
            }
        },"threadTaLog.append").start();
	}

	
	/**
	 * Se encarga de calcular los totales
	 */
	public static void CalcularTotales() {
		// No me gusta modificar el swing desde aki, xo en este proyecto es para ayer. como siempre
		BigDecimal bdPrecio = new BigDecimal(0);
		try{
			bdPrecio = new BigDecimal(filterBean.getPrecio()+"");
		} catch(Exception e){
			FramePrincipal.taTotales.setText("Total url: "+hsTmp.size()+"\nTitulo: "+totalTitle+"\nContenido: "+totalPalabras+"\nPrecio Body: Formato de precio no valido");
			return;
		}
		int totalPalabrasSinDuplicados = 0;
		for(UrlBean bean : alSinContenidoRepetidosTmp){
			totalPalabrasSinDuplicados += bean.getBodyNumPalabras();
		}
		BigDecimal bdTotalPalabras = new BigDecimal(totalPalabras);
		BigDecimal bdTotalPalabrasSinDuplicados = new BigDecimal(totalPalabrasSinDuplicados);
		FramePrincipal.taTotales.setText("Total url analizadas: "+hsURLAllSite.size()
										+"\nContenido de todas las url: "+totalPalabras
										+"\nPrecio Body: "+(bdTotalPalabras.multiply(bdPrecio))
										+"\n\nTotal url SIN contenido duplicado: "+alSinContenidoRepetidosTmp.size()
										+"\nContenido SIN contenido duplicado: "+totalPalabrasSinDuplicados
										+"\nPrecio Body SIN contenido duplicado: "+(bdTotalPalabrasSinDuplicados.multiply(bdPrecio))
										);
	}

	public static void AppendToLog(String txt){
		final String txtLog = txt;
		new Thread(new Runnable() {	
			@Override
			public void run() {
				FramePrincipal.taLog.append(txtLog+"\n");
			}
		}).start();
	}
	
	public static void addHsUnicos(HashSet<String> hs){
		hsUnicos.addAll(hs);
	}

	@Override
	protected Void doInBackground() throws Exception {
    	if(isCancelled()){
    		this.cancel(true);
    		return null;
    	}

    	FramePrincipal.taTotales.setBackground(Color.WHITE);

		//while (!isCancelled()) {
        	switch (filterBean.getAccion()) {
			case FilterBean.ACCION_ANALISIS:
					Analizar();
					break;
			case FilterBean.ACCION_RE_ANALISIS:
					reAnalizar();
					break;
			default:
				Analizar();
				break;
        }
        return null;
	}

	@Override
	protected void process(List<String> chunks) {
		String chunk = chunks.get(chunks.size()-1);
		FramePrincipal.taLog.append(chunk+"\n");
		FramePrincipal.taLog.setRows(FramePrincipal.taLog.getRows() + 1);
	}

	@Override
	protected void done() {
		AppendToLog("Recolección finalizada");
		CalcularTotales();
		FramePrincipal.lblImg.setVisible(true);
	}
	
	
	/**
	 * Se encarga de guardar los datos dentro de un fichero. En el fichero se almacenan los datos para poner montar un insert facilmente
	 * INSERT INTO easy.sitemaps_93(idsitemaps, webs_id, url, tipos_urls_id, fecha_alta, fecha_baja, total_palabras, rutalocal) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
	 * @param path ruta en donde se va a guardar el archivo
	 * @param descripcion descripcion que se ha introducido dentro del frame
	 */
	public static void SaveDataToFile(String path, String descripcion){
		File fileSiteMaps = null;
		PrintWriter pw = null;
		try {
			String fileName = path + File.separator + urlBaseBase + ".csv";
			System.out.println(fileName);
			fileSiteMaps = new File(fileName);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			if(fileSiteMaps.exists()){
				fileSiteMaps.delete();
				fileSiteMaps.createNewFile();
			}
			pw = new PrintWriter(fileSiteMaps);
			pw.println(urlBase);
			pw.print(descripcion);
			for(UrlBean bean : alSinContenidoRepetidosTmp){
				pw.print("\n"+"'"+bean.getUrl()+"',1,'"+sdf.format(new Date())+"',"+"null,"+bean.getBodyNumPalabras()+",null");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(pw != null){
				pw.close();
			}
		}
		
	}
	
	public static void SaveTmpDataToFile(String path, String descripcion){
		comparadorContenidos();
	}
	
	
	/**
	 * Se obtiene un String de totales a mostrar en la pantalla
	 * @return String
	 */
	public String getTotales(){
		return "Total url: "+hsTmp.size()+"\nTitulo: "+totalTitle+"\nContenido: "+totalPalabras+"\nPrecio Body: "+totalPalabras*filterBean.getPrecio();
	}
	public static long getTotalPalabras() {
		return totalPalabras;
	}
	public static void setTotalPalabras(long totalPalabras) {
		Extractor.totalPalabras = totalPalabras;
	}
	public static long getTotalUnicos() {
		return totalUnicos;
	}
	public static void setTotalUnicos(long totalUnicos) {
		Extractor.totalUnicos = totalUnicos;
	}
	public static long getTotalTitle() {
		return totalTitle;
	}
	public static void setTotalTitle(long totalTitle) {
		Extractor.totalTitle = totalTitle;
	}
	public static HashSet<String> getHsfinished() {
		return hsURLAllSite;
	}	
	public static HashSet<String> getTmp() {
		return hsTmp;
	}
	
}
