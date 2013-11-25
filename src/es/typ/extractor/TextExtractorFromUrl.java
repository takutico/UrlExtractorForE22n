package es.typ.extractor;

import java.net.URLConnection;

import es.typ.swing.FramePrincipal;


public class TextExtractorFromUrl implements Runnable {
	
	private String link = "";
	private URLConnection con = null;
	private UrlBean bean = new UrlBean();
	

	public void setLink(String link) {
		this.link = link;
	}
	
	public void setURLConnection(URLConnection con){
		this.con = con;
	}
	public void setBean(UrlBean bean){
		this.bean = bean;
	}


	@Override
	public void run() {
		ExtractText et = new ExtractText();
		//final UrlBean bean;
		//if(con == null){
		if(bean.getHtmlCode() == null || bean.getHtmlCode().length() == 0){
			// Esto es es cuando no se tiene el html en memoria
			bean = et.extractText(link, Extractor.filterBean, 0);
			Extractor.urlBeanAl.add(bean);
			// Al mismo tiempo que se van obtieniendo los datos se van mostrando por pantalla
			new Thread(new Runnable() {
	            public void run() { 
	           		String[] datos = {bean.getUrl(), bean.getBodyNumPalabras()+""};
	           		FramePrincipal.modeloTblResult.addRow(datos);
	            }
	        },"threadTaLog.append").start();
		} else{
			// Esto es cuando se hace un re-analisis, es decir, cuando se tiene el html dentro del objeto
			bean = et.extractTextFromUrlBean(bean, Extractor.filterBean);
			//Extractor.alSinContenidoRepetidosTmp.add(bean);
		}
		
		if(Extractor.MOSTRAR_MISMOS_DATOS_TABLA)
			System.out.println(bean.getUrl() +"\t"+ bean.getTitle()+"\t"+bean.getBodyNumPalabras());
		Extractor.hmUrlBean.put(bean.getUrl(), bean);
		
		Extractor.setTotalPalabras(Extractor.getTotalPalabras() + bean.getBodyNumPalabras());
		Extractor.setTotalUnicos(Extractor.getTotalUnicos() + bean.getUnicos());
		Extractor.setTotalTitle(Extractor.getTotalTitle() + bean.getTitle());

	}
}
