package es.typ.extractor;

public class FilterBean {

	public static final int ACCION_ANALISIS = 0;
	public static final int ACCION_RE_ANALISIS = 1;

	private String urlStr = "";
	private String urlFilter = "";
	private String urlNotFilter = "";
	private String classFilter = "";
	private String idFilter = "";
	private String contentCodeFilter_0 = "";
	private String contentCodeFilter_1 = "";
	private String contentCodeFilter_2 = "";
	private String contentCodeFilter_3 = "";
	private boolean showBodyText = false;
	private float precio;
	private boolean countMeta = false;
	private boolean countSelect = false;
	private boolean countTitle = false;
	private int accion = 0;
	private boolean countExcluded = true;
	private boolean onlyThisUrl = false;
	private boolean tratarComoScript = false;
	private String postParamsScript = "";
	
	
	/** Constructors **/
	public FilterBean() {
		super();
		urlStr = "";
		urlFilter = "";
		contentCodeFilter_0 = "";
		contentCodeFilter_1 = "";
		classFilter = "";
		idFilter = "";
		tratarComoScript = false;
		postParamsScript = "";
	}
	
	
	/** Getters y Setters **/
	public String getContentCodeFilter_0() {
		return contentCodeFilter_0;
	}
	public void setContentCodeFilter_0(String contentCodeFilter_0) {
		this.contentCodeFilter_0 = contentCodeFilter_0;
	}
	public String getContentCodeFilter_1() {
		return contentCodeFilter_1;
	}
	public void setContentCodeFilter_1(String contentCodeFilter_1) {
		this.contentCodeFilter_1 = contentCodeFilter_1;
	}
	public String getUrlStr() {
		return urlStr;
	}
	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}
	public String getUrlFilter() {
		return urlFilter;
	}
	public void setUrlFilter(String urlFilter) {
		this.urlFilter = urlFilter;
	}
	public boolean isCountMeta() {
		return countMeta;
	}
	public void setCountMeta(boolean countMeta) {
		this.countMeta = countMeta;
	}
	public boolean isCountSelect() {
		return countSelect;
	}
	public void setCountSelect(boolean countSelect) {
		this.countSelect = countSelect;
	}
	public boolean isCountTitle() {
		return countTitle;
	}
	public void setCountTitle(boolean countTitle) {
		this.countTitle = countTitle;
	}
	public float getPrecio() {
		return precio;
	}
	public void setPrecio(float precio) {
		this.precio = precio;
	}
	public boolean isShowBodyText() {
		return showBodyText;
	}
	public void setShowBodyText(boolean showBodyText) {
		this.showBodyText = showBodyText;
	}
	public String getClassFilter() {
		return classFilter;
	}
	public void setClassFilter(String classFilter) {
		this.classFilter = classFilter;
	}
	public String getIdFilter() {
		return idFilter;
	}
	public void setIdFilter(String idFilter) {
		this.idFilter = idFilter;
	}
	public String getContentCodeFilter_2() {
		return contentCodeFilter_2;
	}
	public void setContentCodeFilter_2(String contentCodeFilter_2) {
		this.contentCodeFilter_2 = contentCodeFilter_2;
	}
	public String getContentCodeFilter_3() {
		return contentCodeFilter_3;
	}
	public void setContentCodeFilter_3(String contentCodeFilter_3) {
		this.contentCodeFilter_3 = contentCodeFilter_3;
	}
	public String getUrlNotFilter() {
		return urlNotFilter;
	}
	public void setUrlNotFilter(String urlNotFilter) {
		this.urlNotFilter = urlNotFilter;
	}
	public int getAccion() {
		return accion;
	}
	public void setAccion(int accion) {
		this.accion = accion;
	}
	public boolean isCountExcluded() {
		return countExcluded;
	}
	public void setCountExcluded(boolean countExcluded) {
		this.countExcluded = countExcluded;
	}
	public boolean isOnlyThisUrl() {
		return onlyThisUrl;
	}
	public void setOnlyThisUrl(boolean onlyThisUrl) {
		this.onlyThisUrl = onlyThisUrl;
	}
	public boolean isTratarComoScript() {
		return tratarComoScript;
	}
	public void setTratarComoScript(boolean tratarComoScript) {
		this.tratarComoScript = tratarComoScript;
	}
	public String getPostParamsScript() {
		return postParamsScript;
	}
	public void setPostParamsScript(String postParamsScript) {
		this.postParamsScript = postParamsScript;
	}
}
