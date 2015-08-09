package br.com.iejb.sgi.web.util.relatorio;

public enum Formato {
	PDF("PDF", ".pdf"), RTF("RTF", ".rtf"), DOCX("DocX", ".docx"), ODT("ODT", ".odt");
	
	private String nome;
	private String extensao;
	
	private Formato(String nome, String extensao) {
		this.nome = nome;
		this.extensao = extensao;
	}

	public String getNome() {
		return nome;
	}

	public String getExtensao() {
		return extensao;
	}
	
}
