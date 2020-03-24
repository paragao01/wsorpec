package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.spcnetNacionalCnpj.ExecutaSpcNetNacionalCnpj;
import br.wsorpec.wscdlrio.spcnetNacionalCnpj.SpcNetNacionalCnpj;

public class ServicoSpcNetNacionalCnpj {

	/*
	 * Servico SpcNetNacionalCnpj - Obrigatorio informar todos os parametros
	 * 
	 */
	public static SpcNetNacionalCnpj spcnetNacCnpj(String codigo, String senha, String cnpj) {
		
		SpcNetNacionalCnpj spcnetNacCnpj = new SpcNetNacionalCnpj();
		ExecutaSpcNetNacionalCnpj executor = new ExecutaSpcNetNacionalCnpj();

		//Gravar os parametros da consulta na Entidade
		spcnetNacCnpj.setCodigo(codigo);
		spcnetNacCnpj.setSenha(senha);
		spcnetNacCnpj.setCnpj(cnpj);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(spcnetNacCnpj, false);

	}

	public static String respostaXML(String codigo, String senha, String cnpj, String ip) {
		String respostaXML = "";
		SpcNetNacionalCnpj spcnetNacCnpj = new SpcNetNacionalCnpj();
		ExecutaSpcNetNacionalCnpj executor = new ExecutaSpcNetNacionalCnpj();

		//Gravar os parametros da consulta na Entidade
		spcnetNacCnpj.setCodigo(codigo);
		spcnetNacCnpj.setSenha(senha);
		spcnetNacCnpj.setCnpj(cnpj);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		spcnetNacCnpj = executor.executar(spcnetNacCnpj, true);
		
		respostaXML = spcnetNacCnpj.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ></S-CNPJ><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO:</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CNPJ>", cnpj, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)spcnetNacCnpj.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", spcnetNacCnpj.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "47", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		spcnetNacCnpj.setXml(respostaXML);
		
		return spcnetNacCnpj.getXml();
	}
/*
	public static void main(String[] args) {
		SpcNetNacionalCnpj spcnetNacCnpj = new SpcNetNacionalCnpj();
		
		//spcnetNacCnpj = spcnetNacCnpj("00001", "08493296", "33014556000196");
		
		System.out.println(respostaXML("00001", "08493296", "33014556000196"));
		
		if(spcnetNacCnpj.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			//System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111TOTAL());
			//System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111PRIMEIRA());
			//System.out.println(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111ULTIMA());				
		}
		System.out.println(spcnetNacCnpj.getStatusRetorno());
		System.out.println(spcnetNacCnpj.getMensagemRetorno());
		
		
	}*/
}
