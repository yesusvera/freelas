package br.com.rsm.model;

import java.io.Serializable;

public class SupportMessage implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	//private EnumSupportSubject subject = getSubject().CONFORMIDADE;
	private Integer subject; 
	
	private String body;

	public SupportMessage(){}
	
//	public SupportMessage(Integer id, EnumSupportSubject subject, String body){
	public SupportMessage(Integer id, Integer subject, String body){
		this.id = id;
		this.subject = subject;
		this.body = body;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSubject() {
		return subject;
	}

	public void setSubject(Integer subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	

}
