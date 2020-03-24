package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.spcnetEstadualCnpj.ExecutaSpcNetEstadualCnpj;
import br.wsorpec.wscdlrio.spcnetEstadualCnpj.SpcNetEstadualCnpj;

public class ServicoSpcNetEstadualCnpj {

	/*
	 * Servico SpcNetRegionalCnpj - Obrigatorio informar todos os parametros
	 * 
	 */
	public static SpcNetEstadualCnpj spcnetEstCnpj(String codigo, String senha, String cnpj, String uf) {
		
		SpcNetEstadualCnpj spcnetEstCnpj = new SpcNetEstadualCnpj();
		ExecutaSpcNetEstadualCnpj executor = new ExecutaSpcNetEstadualCnpj();

		//Gravar os parametros da consulta na Entidade
		spcnetEstCnpj.setCodigo(codigo);
		spcnetEstCnpj.setSenha(senha);
		spcnetEstCnpj.setCnpj(cnpj);
		spcnetEstCnpj.setUf(uf);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(spcnetEstCnpj, false);

	}

	public static String respostaXML(String codigo, String senha, String cnpj, String uf, String ip) {
		String respostaXML = "";
		SpcNetEstadualCnpj spcnetEstCnpj = new SpcNetEstadualCnpj();
		ExecutaSpcNetEstadualCnpj executor = new ExecutaSpcNetEstadualCnpj();

		//Gravar os parametros da consulta na Entidade
		spcnetEstCnpj.setCodigo(codigo);
		spcnetEstCnpj.setSenha(senha);
		spcnetEstCnpj.setCnpj(cnpj);
		spcnetEstCnpj.setUf(uf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		spcnetEstCnpj = executor.executar(spcnetEstCnpj, true);
		
		respostaXML = spcnetEstCnpj.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   spcnetEstCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ></S-CNPJ><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CNPJ>", cnpj, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)spcnetEstCnpj.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", spcnetEstCnpj.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "40", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		spcnetEstCnpj.setXml(respostaXML);
		
		return spcnetEstCnpj.getXml();
	}
/*
	public static void main(String[] args) {
		SpcNetEstadualCnpj spcnetEstCnpj = new SpcNetEstadualCnpj();
		
		//spcnetEstCnpj = spcnetEstCnpj("00001", "08493296", "33014556000196", "SP");

		System.out.println(respostaXML("00001", "08493296", "33014556000196", "SP", "127.0.0.1"));

		if(spcnetEstCnpj.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			//System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111TOTAL());
			//System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111PRIMEIRA());
			//System.out.println(spcnetEstCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111ULTIMA());				
		}
		System.out.println(spcnetEstCnpj.getStatusRetorno());
		System.out.println(spcnetEstCnpj.getMensagemRetorno());
	}*/
}
