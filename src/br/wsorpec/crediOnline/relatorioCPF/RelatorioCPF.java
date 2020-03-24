package br.wsorpec.crediOnline.relatorioCPF;

public class RelatorioCPF {

	private String codigo;
	private String senha;
	private String cpf;
	private String mensagemRetorno;
	private int statusRetorno;
	private CrediOnLineRelatorioCPF credionlineRelatorioCpf;
	private CrediOnLineRestritivoCPF credionlineRestritivoCpf;
	private CrediOnLineChequeCPF credionlineChequeCpf;
	
	private String json;

	public RelatorioCPF() {
		if (credionlineRelatorioCpf == null) {
			credionlineRelatorioCpf = new CrediOnLineRelatorioCPF();
		}
		if (credionlineRestritivoCpf == null) {
			credionlineRestritivoCpf = new CrediOnLineRestritivoCPF();
		}
		if (credionlineChequeCpf == null) {
			credionlineChequeCpf = new CrediOnLineChequeCPF();
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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
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

	public CrediOnLineRelatorioCPF getCredionlineRelatorioCpf() {
		return credionlineRelatorioCpf;
	}

	public void setCredionlineRelatorioCpf(CrediOnLineRelatorioCPF credionlineRelatorioCpf) {
		this.credionlineRelatorioCpf = credionlineRelatorioCpf;
	}

	public CrediOnLineRestritivoCPF getCredionlineRestritivoCpf() {
		return credionlineRestritivoCpf;
	}

	public void setCredionlineRestritivoCpf(CrediOnLineRestritivoCPF credionlineRestritivoCpf) {
		this.credionlineRestritivoCpf = credionlineRestritivoCpf;
	}

	public CrediOnLineChequeCPF getCredionlineChequeCpf() {
		return credionlineChequeCpf;
	}

	public void setCredionlineChequeCpf(CrediOnLineChequeCPF credionlineChequeCpf) {
		this.credionlineChequeCpf = credionlineChequeCpf;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
