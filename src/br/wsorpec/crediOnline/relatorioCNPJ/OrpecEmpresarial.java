package br.wsorpec.crediOnline.relatorioCNPJ;

public class OrpecEmpresarial {

	private String codigo;
	private String senha;
	private String cnpj;
	private String mensagemRetorno;
	private byte statusRetorno;
	private CrediOnLineRelatorioCNPJ credonlineRelatorioCnpj;

	public OrpecEmpresarial() {
		if (credonlineRelatorioCnpj == null) {
			credonlineRelatorioCnpj = new CrediOnLineRelatorioCNPJ();
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

	public byte getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(byte statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	public CrediOnLineRelatorioCNPJ getCredonlineRelatorioCnpj() {
		return credonlineRelatorioCnpj;
	}

	public void setCredonlineRelatorioCnpj(CrediOnLineRelatorioCNPJ credonlineRelatorioCnpj) {
		this.credonlineRelatorioCnpj = credonlineRelatorioCnpj;
	}


}
