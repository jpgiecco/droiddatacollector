package ar.com.eurekaconsulting.elementControl.model;

public class New {
	
	private Integer code;
	
	private String description;

	public New(Integer code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.getCode() + " - " + this.getDescription(); 
	}

}
