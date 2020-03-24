package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.spcnetNacionalCpf.ExecutaSpcNetNacionalCpf;
import br.wsorpec.wscdlrio.spcnetNacionalCpf.SpcNetNacionalCpf;

public class ServicoSpcNetNacionalCpf {

	/*
	 * Servico SpcNetNacionalCpf - Obrigatorio informar todos os parametros
	 * 
	 */
	public static SpcNetNacionalCpf spcnetNacCpf(String codigo, String senha, String cpf) {
		
		SpcNetNacionalCpf spcnetRegCpf = new SpcNetNacionalCpf();
		ExecutaSpcNetNacionalCpf executor = new ExecutaSpcNetNacionalCpf();

		//Gravar os parametros da consulta na Entidade
		spcnetRegCpf.setCodigo(codigo);
		spcnetRegCpf.setSenha(senha);
		spcnetRegCpf.setCpf(cpf);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(spcnetRegCpf, false);

	}

	public static String respostaXML(String codigo, String senha, String cpf, String ip) {
		String respostaXML = "";
		SpcNetNacionalCpf spcnetRegCpf = new SpcNetNacionalCpf();
		ExecutaSpcNetNacionalCpf executor = new ExecutaSpcNetNacionalCpf();

		//Gravar os parametros da consulta na Entidade
		spcnetRegCpf.setCodigo(codigo);
		spcnetRegCpf.setSenha(senha);
		spcnetRegCpf.setCpf(cpf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		spcnetRegCpf = executor.executar(spcnetRegCpf, true);
		
		respostaXML = spcnetRegCpf.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   spcnetRegCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CPF>12312312387</S-CPF><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO: TRANSACAO BLOQUEADA PARA ESTA SENHA(277)</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CPF>", cpf, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)spcnetRegCpf.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", spcnetRegCpf.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "24", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		spcnetRegCpf.setXml(respostaXML);
		
		return spcnetRegCpf.getXml();
	}
	/*
	public static void main(String[] args) {
		SpcNetNacionalCpf spcnetRegCpf = new SpcNetNacionalCpf();
		
		//spcnetRegCpf = spcnetNacCpf("00001", "08493296", "12312312387");
		
		System.out.println(respostaXML("00001", "08493296", "12312312387"));

		if(spcnetRegCpf.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111TOTAL());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111PRIMEIRA());
			System.out.println(spcnetRegCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPNET().getNET111CONSULTAANT().getNET111RESUMO().getNET111ULTIMA());				
		}else{
			System.out.println(spcnetRegCpf.getStatusRetorno());
			System.out.println(spcnetRegCpf.getMensagemRetorno());
		}
	}*/
}
