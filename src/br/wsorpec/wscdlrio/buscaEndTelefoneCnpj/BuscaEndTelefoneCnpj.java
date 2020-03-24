package br.wsorpec.wscdlrio.buscaEndTelefoneCnpj;

public class BuscaEndTelefoneCnpj {

	private String codigo;
	private String senha;
	private String cnpj;
	private String mensagemRetorno;
	private byte statusRetorno;
	private SPCAXML spcaxml;
	private String xml;

	public BuscaEndTelefoneCnpj() {
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
