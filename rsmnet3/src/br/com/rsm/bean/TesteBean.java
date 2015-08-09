package br.com.rsm.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="teste")
@SessionScoped
public class TesteBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void print(){
//		System.out.println("Funcionou");
	}
}
