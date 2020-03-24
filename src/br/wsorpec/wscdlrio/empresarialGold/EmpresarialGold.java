package br.wsorpec.wscdlrio.empresarialGold;

public class EmpresarialGold {

	private String codigo;
	private String senha;
	private String cnpj;
	private String cmc7;
	private String banco;
	private String agencia;
	private String numConta;
	private String dvConta;
	private String numCheque;
	private String dvCheque;
	private String qtdeCheque;
	private String chequeOrigem;	
	private String dataCheque;
	private String quadroSocial;
	private String quadroSocialRestricao;
	private String participacoes;
	private String participacoesRestricao;
	private String decisao;
	private String faturamentoPresumido;
	private String limiteCredito;
	private String anvisa;
	private String empresaMesmoEndereco;
	private String folhaCheque;
	private String mensagemRetorno;
	private byte statusRetorno;
	private SPCAXML spcaxml;
	private String xml;

	public EmpresarialGold() {
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

	public String getCmc7() {
		return cmc7;
	}

	public void setCmc7(String cmc7) {
		this.cmc7 = cmc7;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getNumConta() {
		return numConta;
	}

	public void setNumConta(String numConta) {
		this.numConta = numConta;
	}

	public String getDvConta() {
		return dvConta;
	}

	public void setDvConta(String dvConta) {
		this.dvConta = dvConta;
	}

	public String getNumCheque() {
		return numCheque;
	}

	public void setNumCheque(String numCheque) {
		this.numCheque = numCheque;
	}

	public String getDvCheque() {
		return dvCheque;
	}

	public void setDvCheque(String dvCheque) {
		this.dvCheque = dvCheque;
	}

	public String getQtdeCheque() {
		return qtdeCheque;
	}

	public void setQtdeCheque(String qtdeCheque) {
		this.qtdeCheque = qtdeCheque;
	}

	public String getChequeOrigem() {
		return chequeOrigem;
	}

	public void setChequeOrigem(String chequeOrigem) {
		this.chequeOrigem = chequeOrigem;
	}

	public String getDataCheque() {
		return dataCheque;
	}

	public void setDataCheque(String dataCheque) {
		this.dataCheque = dataCheque;
	}

	public String getQuadroSocial() {
		return quadroSocial;
	}

	public void setQuadroSocial(String quadroSocial) {
		this.quadroSocial = quadroSocial;
	}

	public String getQuadroSocialRestricao() {
		return quadroSocialRestricao;
	}

	public void setQuadroSocialRestricao(String quadroSocialRestricao) {
		this.quadroSocialRestricao = quadroSocialRestricao;
	}

	public String getParticipacoes() {
		return participacoes;
	}

	public void setParticipacoes(String participacoes) {
		this.participacoes = participacoes;
	}

	public String getParticipacoesRestricao() {
		return participacoesRestricao;
	}

	public void setParticipacoesRestricao(String participacoesRestricao) {
		this.participacoesRestricao = participacoesRestricao;
	}

	public String getDecisao() {
		return decisao;
	}

	public void setDecisao(String decisao) {
		this.decisao = decisao;
	}

	public String getFaturamentoPresumido() {
		return faturamentoPresumido;
	}

	public void setFaturamentoPresumido(String faturamentoPresumido) {
		this.faturamentoPresumido = faturamentoPresumido;
	}

	public String getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(String limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	public String getAnvisa() {
		return anvisa;
	}

	public void setAnvisa(String anvisa) {
		this.anvisa = anvisa;
	}

	public String getEmpresaMesmoEndereco() {
		return empresaMesmoEndereco;
	}

	public void setEmpresaMesmoEndereco(String empresaMesmoEndereco) {
		this.empresaMesmoEndereco = empresaMesmoEndereco;
	}

	public String getFolhaCheque() {
		return folhaCheque;
	}

	public void setFolhaCheque(String folhaCheque) {
		this.folhaCheque = folhaCheque;
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
