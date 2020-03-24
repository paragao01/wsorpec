package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.spcnetEstadualCpf.ExecutaSpcNetEstadualCpf;
import br.wsorpec.wscdlrio.spcnetEstadualCpf.SpcNetEstadualCpf;

public class ServicoSpcNetEstadualCpf {

	/*
	 * Servico SpcNetRegionalCpf - Obrigatorio informar todos os parametros
	 * 
	 */
	public static SpcNetEstadualCpf spcnetEstCpf(String codigo, String senha, String cpf, String uf) {
		
		SpcNetEstadualCpf spcnetEstCpf = new SpcNetEstadualCpf();
		ExecutaSpcNetEstadualCpf executor = new ExecutaSpcNetEstadualCpf();

		//Gravar os parametros da consulta na Entidade
		spcnetEstCpf.setCodigo(codigo);
		spcnetEstCpf.setSenha(senha);
		spcnetEstCpf.setCpf(cpf);
		spcnetEstCpf.setUf(uf);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(spcnetEstCpf, false);

	}

	public static String respostaXML(String codigo, String senha, String cpf, String uf, String ip) {
		String respostaXML = "";
		SpcNetEstadualCpf spcnetEstCpf = new SpcNetEstadualCpf();
		ExecutaSpcNetEstadualCpf executor = new ExecutaSpcNetEstadualCpf();

		//Gravar os parametros da consulta na Entidade
		spcnetEstCpf.setCodigo(codigo);
		spcnetEstCpf.setSenha(senha);
		spcnetEstCpf.setCpf(cpf);
		spcnetEstCpf.setUf(uf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		spcnetEstCpf = executor.executar(spcnetEstCpf, true);
		
		respostaXML = spcnetEstCpf.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   spcnetEstCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CPF>12312312387</S-CPF><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO: TRANSACAO BLOQUEADA PARA ESTA SENHA(277)</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CPF>", cpf, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)spcnetEstCpf.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", spcnetEstCpf.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "40", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		spcnetEstCpf.setXml(respostaXML);
		
		return spcnetEstCpf.getXml();
	}
/*
	public static void main(String[] args) {
		SpcNetEstadualCpf spcnetEstCpf = new SpcNetEstadualCpf();
		
		//spcnetEstCpf = spcnetEstCpf("00001", "08493296", "12312312387", "SP");
		
		System.out.println(respostaXML("00001", "08493296", "12312312387", "SP", "127.0.0.1"));
		
		if(spcnetEstCpf.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111TOTAL());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111PRIMEIRA());
			System.out.println(spcnetEstCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111ULTIMA());				
		}else {
			System.out.println(spcnetEstCpf.getStatusRetorno());
			System.out.println(spcnetEstCpf.getMensagemRetorno());
		}
	}*/
}
