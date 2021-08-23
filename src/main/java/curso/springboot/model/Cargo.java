package curso.springboot.model;

import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredConstructor;

public enum Cargo {
	
	JUNIOR("Júnior"),
	PLENO("Pleno"),
	SENIOR("Sênior"),
	MASTER("Master");
	
	private String nome;
	private String valor;
	
	private Cargo(String nome) {
		this.nome = nome;
	}
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getValor() {
		return valor = this.name();
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	

}
