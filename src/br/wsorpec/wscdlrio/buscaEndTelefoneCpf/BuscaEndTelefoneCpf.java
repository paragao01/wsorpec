package br.wsorpec.wscdlrio.buscaEndTelefoneCpf;

public class BuscaEndTelefoneCpf {

	private String codigo;
	private String senha;
	private String cpf;
	private String mensagemRetorno;
	private byte statusRetorno;
	private SPCAXML spcaxml;
	private String xml;

	public BuscaEndTelefoneCpf() {
		if (spcaxml == null) {
			spcaxml = new SPCAXML();
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

	public byte getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(byte statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	public SPCAXML getSpcaxml() {
		return spcaxml;
	}

	public void setSpcaxml(SPCAXML spcaxml) {
		this.spcaxml = spcaxml;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}
