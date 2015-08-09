package br.com.rsm.enumeradores;

import java.io.Serializable;


public enum EnumImg implements Serializable {

	BLANK_ICON ("/templates/backend/arvore/0.jpg"),
	CREATED_ICON ("/templates/backend/arvore/new_male_created.png"),	
	ACTIVATED_ICON ("/templates/backend/arvore/new_male.png"),	
	SUSPENDED_ICON ("/templates/backend/arvore/new_male_suspended.png"),	
	DEACTIVATED_ICON ("/templates/backend/arvore/new_male_deactivated.png");	
	
	private EnumImg(String uri){
		setUri(uri);
	}
	
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
		
}
