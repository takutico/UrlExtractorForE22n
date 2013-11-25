package es.typ.extractor;

public class UrlBean {
	
	private String url;
	private boolean isRead;
	private long title;
	private long description;
	private long keywords;
	private long bodyNumPalabras;
	private String bodyTxt;
	private long unicos;
	private String htmlCode;
	private int numElementsImg;
	private int numElementsFlash;
	private int numElementsPdf;
	
	
	/** Contructors **/
	public UrlBean() {
		super();
		this.url = "";
		this.isRead = false;
		this.title = 0;
		this.description = 0;
		this.keywords = 0;
		this.bodyNumPalabras = 0;
		this.bodyTxt = "";
		this.unicos = 0;
		this.htmlCode = "";
		this.numElementsImg = 0;
		this.numElementsFlash = 0;
		this.numElementsPdf = 0;
	}
	
	
	/** Getters and Setters **/
	public long getUnicos() {
		return unicos;
	}
	public void setUnicos(long unicos) {
		this.unicos = unicos;
	}
	public String getBodyTxt() {
		return bodyTxt;
	}
	public void setBodyTxt(String bodyTxt) {
		this.bodyTxt = bodyTxt;
	}
	public long getBodyNumPalabras() {
		return bodyNumPalabras;
	}
	public void setBodyNumPalabras(long bodyNumPalabras) {
		this.bodyNumPalabras = bodyNumPalabras;
	}
	public long getTitle() {
		return title;
	}
	public void setTitle(long title) {
		this.title = title;
	}
	public long getDescription() {
		return description;
	}
	public void setDescription(long description) {
		this.description = description;
	}
	public long getKeywords() {
		return keywords;
	}
	public void setKeywords(long keywords) {
		this.keywords = keywords;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getHtmlCode() {
		return htmlCode;
	}
	public void setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
	}
	public int getNumElementsImg() {
		return numElementsImg;
	}
	public void setNumElementsImg(int numElementsImg) {
		this.numElementsImg = numElementsImg;
	}
	public int getNumElementsFlash() {
		return numElementsFlash;
	}
	public void setNumElementsFlash(int numElementsFlash) {
		this.numElementsFlash = numElementsFlash;
	}
	public int getNumElementsPdf() {
		return numElementsPdf;
	}
	public void setNumElementsPdf(int numElementsPdf) {
		this.numElementsPdf = numElementsPdf;
	}
}
