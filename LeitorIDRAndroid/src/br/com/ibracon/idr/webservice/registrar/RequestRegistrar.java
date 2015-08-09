package br.com.ibracon.idr.webservice.registrar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import br.com.ibracon.idr.webservice.RequestWS;

public class RequestRegistrar extends RequestWS {
	
	private String registro = "";
	private String cliente = "";
	private String endereco= "";
	private String numero= "";
	private String complemento= "";
	private String bairro= "";
	private String cidade= "";
	private String uf= "";
	private String cep= "";
	private String email= "";
	private String senha= "";
	private String serial= "";
	private String macadress= "";
	private String ip= "";
	private String documento= "";
	private String telefone= "";
	private String dispositivo= "";
	private String associado= ""; //S ou N
	
	public String getRegistro() {
		return registro;
	}
	public void setRegistro(String registro) {
		this.registro = registro;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getMacadress() {
		return macadress;
	}
	public void setMacadress(String macadress) {
		this.macadress = macadress;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getDispositivo() {
		return dispositivo;
	}
	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}
	
	public String getAssociado() {
		return associado;
	}
	public void setAssociado(String associado) {
		this.associado = associado;
	}
	@SuppressWarnings("deprecation")
	@Override
	public Properties getParameters() {
		Properties parameters = new Properties();
		try {
		parameters.setProperty("registro", URLEncoder.encode(registro,"ISO-8859-1"));
		parameters.setProperty("cliente", URLEncoder.encode(cliente,"ISO-8859-1"));
		parameters.setProperty("endereco", URLEncoder.encode(endereco,"ISO-8859-1"));
		parameters.setProperty("numero", URLEncoder.encode(numero,"ISO-8859-1"));
		parameters.setProperty("complemento", URLEncoder.encode(complemento,"ISO-8859-1"));
		parameters.setProperty("bairro", URLEncoder.encode(bairro,"ISO-8859-1"));
		parameters.setProperty("cidade", URLEncoder.encode(cidade,"ISO-8859-1"));
		parameters.setProperty("uf", URLEncoder.encode(uf,"ISO-8859-1"));
		parameters.setProperty("cep", URLEncoder.encode(cep,"ISO-8859-1"));
		parameters.setProperty("email", URLEncoder.encode(email,"ISO-8859-1"));
		parameters.setProperty("senha", URLEncoder.encode(senha,"ISO-8859-1"));
		parameters.setProperty("serial", URLEncoder.encode(serial,"ISO-8859-1"));
		parameters.setProperty("macadress", URLEncoder.encode(macadress,"ISO-8859-1"));
		parameters.setProperty("ip", URLEncoder.encode(ip,"ISO-8859-1"));
		parameters.setProperty("documento", URLEncoder.encode(documento,"ISO-8859-1"));
		parameters.setProperty("telefone", URLEncoder.encode(telefone,"ISO-8859-1"));
		parameters.setProperty("dispositivo", URLEncoder.encode(dispositivo,"ISO-8859-1"));
		parameters.setProperty("associado", URLEncoder.encode(associado,"ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return parameters;
	}
}