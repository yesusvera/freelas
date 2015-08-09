package br.com.ibracon.idr.webservice.registrarLivro;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("livro")
public class LivroRegistrado {
	private String codigolivro;
	private String status;

	public String getCodigolivro() {
		return codigolivro;
	}

	public void setCodigolivro(String codigolivro) {
		this.codigolivro = codigolivro;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "LivroRegistrado [codigolivro=" + codigolivro + ", status="
				+ status + "]";
	}

}
