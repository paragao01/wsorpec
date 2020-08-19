package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.empresarialGold.EmpresarialGold;
import br.wsorpec.wscdlrio.empresarialGold.ExecutaEmpresarialGold;

public class ServicoEmpresarialGold {

	/*
	 * Servico DefineRisco - Obrigatorio informar todos os parametros
	 * 
	 */
	public static EmpresarialGold empresarialGold(String codigo, 
									String senha, 
									String cnpj,
									String banco,
									String agencia,
									String numConta,
									String dvConta,
									String numCheque,
									String dvCheque,
									String cmc7,
									String chequeOrigem,
									String qtdeCheque,
									String dataCheque,
									String quadroSocial,
									String quadroSocialRestricao,
									String participacoes,
									String participacoesRestricao,
									String decisao,
									String faturamentoPresumido,
									String limiteCredito,
									String anvisa,
									String empresaMesmoEndereco,
									String cheque) {
		
		EmpresarialGold empGold = new EmpresarialGold();
		ExecutaEmpresarialGold executor = new ExecutaEmpresarialGold();

		//Gravar os parametros da consulta na Entidade
		empGold.setCodigo(codigo);
		empGold.setSenha(senha);
		empGold.setCnpj(cnpj);
		empGold.setBanco(banco);
		empGold.setAgencia(agencia);
		empGold.setNumConta(numConta);
		empGold.setDvConta(dvConta);
		empGold.setNumCheque(numCheque);
		empGold.setDvCheque(dvCheque);
		empGold.setCmc7(cmc7);
		empGold.setChequeOrigem(chequeOrigem);
		empGold.setQtdeCheque(qtdeCheque);
		empGold.setDataCheque(dataCheque);		
		empGold.setQuadroSocial(quadroSocial);
		empGold.setQuadroSocialRestricao(quadroSocialRestricao);
		empGold.setParticipacoes(participacoes);
		empGold.setParticipacoesRestricao(participacoesRestricao);
		empGold.setDecisao(decisao);
		empGold.setFaturamentoPresumido(faturamentoPresumido);
		empGold.setLimiteCredito(limiteCredito);
		empGold.setAnvisa(anvisa);
		empGold.setEmpresaMesmoEndereco(empresaMesmoEndereco);
		empGold.setFolhaCheque(cheque);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(empGold, false);

	}

	public static String respostaXML(String codigo, 
									 String senha, 
									 String cnpj,
									 String banco,
									 String agencia,
									 String numConta,
									 String dvConta,
									 String numCheque,
									 String dvCheque,
									 String cmc7,
									 String chequeOrigem, 
									 String qtdeCheque,
									 String dataCheque,									 
									 String quadroSocial,
									 String quadroSocialRestricao,
									 String participacoes,
									 String participacoesRestricao,
									 String decisao,
									 String faturamentoPresumido,
									 String limiteCredito,
									 String anvisa,
									 String empresaMesmoEndereco,
									 String cheque,
									 String ip) {
		String respostaXML = "";
		EmpresarialGold empGold = new EmpresarialGold();
		ExecutaEmpresarialGold executor = new ExecutaEmpresarialGold();

		//Gravar os parametros da consulta na Entidade
		empGold.setCodigo(codigo);
		empGold.setSenha(senha);
		empGold.setCnpj(cnpj);
		empGold.setBanco(banco);
		empGold.setAgencia(agencia);
		empGold.setNumConta(numConta);
		empGold.setDvConta(dvConta);
		empGold.setNumCheque(numCheque);
		empGold.setDvCheque(dvCheque);
		empGold.setCmc7(cmc7);
		empGold.setChequeOrigem(chequeOrigem);
		empGold.setQtdeCheque(qtdeCheque);
		empGold.setDataCheque(dataCheque);
		empGold.setQuadroSocial(quadroSocial);
		empGold.setQuadroSocialRestricao(quadroSocialRestricao);
		empGold.setParticipacoes(participacoes);
		empGold.setParticipacoesRestricao(participacoesRestricao);
		empGold.setDecisao(decisao);
		empGold.setFaturamentoPresumido(faturamentoPresumido);
		empGold.setLimiteCredito(limiteCredito);
		empGold.setAnvisa(anvisa);
		empGold.setEmpresaMesmoEndereco(empresaMesmoEndereco);
		empGold.setFolhaCheque(cheque);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		empGold = executor.executar(empGold, true);
		
		respostaXML = empGold.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>74</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ>"+cnpj+"</S-CNPJ><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>"+ip+"</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO:</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CNPJ>", cnpj, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)empGold.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", empGold.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "74", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		empGold.setXml(respostaXML);
		
		return empGold.getXml();
	}
/*
	public static void main(String[] args) {
		EmpresarialGold empGold = new EmpresarialGold();
				
		//empGold = empresarialGold("00001","08493296","33014556000196","N","S","N","S","S","S","S","S","S","N");

		System.out.println(respostaXML("00001", 
									   "08493296", 
									   "33014556000196",
									   "237",
									   "0212",
									   "1",
									   "9",
									   "25",
									   "6",
									   "",
									   "D",
									   "1",
									   "22082018",
									   "N",
									   "S",
									   "N",
									   "S",
									   "N",
									   "N",
									   "N",
									   "N",
									   "N",
									   "N", "129.0.0.1"));
		
		if(empGold.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
		}
		System.out.println(empGold.getStatusRetorno());
		System.out.println(empGold.getMensagemRetorno());
		
	}*/
}
