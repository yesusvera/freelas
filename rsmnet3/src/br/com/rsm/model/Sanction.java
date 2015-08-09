package br.com.rsm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author yesus
 * Usada para armazenar sanções feitas pelo administrador do sistema.
 */

public class Sanction implements Serializable{
	
	private static final long serialVersionUID = 7875877956827371929L;
	
	
	private Long id;
	private String userId;
	private String description;
	private Timestamp date; 
	private Long operator;
	private String operation;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Long getOperator() {
		return operator;
	}
	public void setOperator(Long operator) {
		this.operator = operator;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
}
