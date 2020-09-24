package ar.com.eurekaconsulting.elementControl.model;

public class Element implements Comparable<Element>{
	
	private Integer clientId;
	private String code;
	private String service;
	private String description;
	private Long previousValue;
	private Integer maxDifference;
	private Long actualValue;
	private New novedad;
	private Integer saveOrder;
	private boolean debt;
	private String usuario;
	
	public Element(Integer clienteId, String code, String service, String descripcion, Long previousValue, Integer maxDifference,boolean debt, String usuario) {
		this.setClientId(clienteId);
		this.setCode(code);
		this.setService(service);
		this.setDescription(descripcion);
		this.setPreviousValue(previousValue);
		this.setActualValue(0L);
		this.setMaxDifference(maxDifference);
		this.setSaveOrder(-1);
		this.setDebt(debt);
		this.setUsuario(usuario);
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getPreviousValue() {
		return previousValue;
	}
	public void setPreviousValue(Long previousValue) {
		this.previousValue = previousValue;
	}
	public Long getActualValue() {
		return actualValue;
	}
	public void setActualValue(Long actualValue) {
		this.actualValue = actualValue;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public New getNovedad() {
		return novedad;
	}

	public void setNovedad(New novedad) {
		this.novedad = novedad;
	}
	
	public boolean isRelevado(){
		return this.actualValue != 0L; //|| this.novedad != null;
	}

	public Integer getMaxDifference() {
		return maxDifference;
	}

	public void setMaxDifference(Integer maxDifference) {
		this.maxDifference = maxDifference;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	@Override
	public int compareTo(Element anotherElement) {
		return this.getCode().compareTo(anotherElement.getCode());
	}

	public Integer getSaveOrder() {
		return saveOrder;
	}

	public void setSaveOrder(Integer saveOrder) {
		this.saveOrder = saveOrder;
	}

	public boolean isDebt() {
		return debt;
	}

	public void setDebt(boolean debt) {
		this.debt = debt;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
