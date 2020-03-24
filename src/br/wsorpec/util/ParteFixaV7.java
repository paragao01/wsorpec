package br.wsorpec.util;

import br.wsorpec.util.Formatador;;


public class ParteFixaV7 {

	private String transacao;
	private String versao;
	private String entidade;
	private String firma;
	private String senha;
	private String praca;
	private String via;
	private String terminal;
	private String reservado;
	private String str;
	
	public ParteFixaV7() {
	}

	/**
	 * @return the transacao
	 */
	public String getTransacao() {
		return transacao;
	}

	/**
	 * @param transacao the transacao to set
	 */
	public void setTransacao(String transacao) {
		this.transacao = transacao;
	}

	/**
	 * @return the versao
	 */
	public String getVersao() {
		versao = "007";
		return versao;
	}

	public String getEntidade() {
		entidade = Formatador.formataCampo(entidade, 10);
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public String getFirma() {
		firma = Formatador.formataCampo(firma, 10);		
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

	public String getSenha() {
		senha = Formatador.formataCampo(senha, 10);						
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * @return the praca
	 */
	public String getPraca() {
		praca = Formatador.formataCampo(praca, 8);
		return praca;
	}

	/**
	 * @return the via
	 */
	public String getVia() {
		via = "025";
		return via;
	}

	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		terminal = Formatador.formataCampo("ORPECREDIT", 10);		
		return terminal;
	}

	/**
	 * @return the reservado
	 */
	public String getReservado() {
		reservado = Formatador.formataCampo(reservado, 69);
		return reservado;
	}

	public String getStr(){
		str = getTransacao()+
			  getVersao()+
			  getEntidade()+
			  getFirma()+
			  getSenha()+
			  getPraca()+
			  getVia()+
			  getTerminal()+
			  getReservado();
		return str;
	}
	
}
