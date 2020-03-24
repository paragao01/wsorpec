package br.wsorpec.crediOnline.relatorioCNPJ;

public class RelatorioCNPJ {

	private String codigo;
	private String senha;
	private String cnpj;
	private String mensagemRetorno;
	private int statusRetorno;
	private CrediOnLineRelatorioCNPJ credionlineRelatorioCnpj;
	private CrediOnLineRestritivoCNPJ credionlineRestritivoCnpj;
	private String json;

	public RelatorioCNPJ() {
		if (credionlineRelatorioCnpj == null) {
			credionlineRelatorioCnpj = new CrediOnLineRelatorioCNPJ();
		}
		if (credionlineRestritivoCnpj == null) {
			credionlineRestritivoCnpj = new CrediOnLineRestritivoCNPJ();
		}		
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getMensagemRetorno() {
		return mensagemRetorno;
	}

	public void setMensagemRetorno(String mensagemRetorno) {
		this.mensagemRetorno = mensagemRetorno;
	}

	public int getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(int statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	public CrediOnLineRelatorioCNPJ getCredionlineRelatorioCnpj() {
		return credionlineRelatorioCnpj;
	}

	public void setCredionlineRelatorioCnpj(CrediOnLineRelatorioCNPJ credionlineRelatorioCnpj) {
		this.credionlineRelatorioCnpj = credionlineRelatorioCnpj;
	}
	
	public CrediOnLineRestritivoCNPJ getCredionlineRestritivoCnpj() {
		return credionlineRestritivoCnpj;
	}

	public void setCredionlineRestritivoCnpj(CrediOnLineRestritivoCNPJ credionlineRestritivoCnpj) {
		this.credionlineRestritivoCnpj = credionlineRestritivoCnpj;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
