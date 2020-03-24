package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.buscaEndTelefoneCnpj.BuscaEndTelefoneCnpj;
import br.wsorpec.wscdlrio.buscaEndTelefoneCnpj.ExecutaBuscaEndTelefoneCnpj;

public class ServicoBuscaEndTelefoneCnpj {

	/*
	 * Servico DefineCadastro - Obrigatorio informar todos os parametros
	 * 
	 */
	public static BuscaEndTelefoneCnpj buscaEndTelefoneCnpj(String codigo, String senha, String cnpj) {
		
		BuscaEndTelefoneCnpj buscaCnpj = new BuscaEndTelefoneCnpj();
		ExecutaBuscaEndTelefoneCnpj executor = new ExecutaBuscaEndTelefoneCnpj();

		//Gravar os parametros da consulta na Entidade
		buscaCnpj.setCodigo(codigo);
		buscaCnpj.setSenha(senha);
		buscaCnpj.setCnpj(cnpj);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(buscaCnpj, false);

	}

	public static String respostaXML(String codigo, String senha, String cnpj, String ip) {
		String respostaXML = "";
		BuscaEndTelefoneCnpj buscaCnpj = new BuscaEndTelefoneCnpj();
		ExecutaBuscaEndTelefoneCnpj executor = new ExecutaBuscaEndTelefoneCnpj();

		//Gravar os parametros da consulta na Entidade
		buscaCnpj.setCodigo(codigo);
		buscaCnpj.setSenha(senha);
		buscaCnpj.setCnpj(cnpj);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		buscaCnpj = executor.executar(buscaCnpj, true);
		
		respostaXML = buscaCnpj.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ></S-CNPJ><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO:</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CNPJ>", cnpj, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)buscaCnpj.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", buscaCnpj.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "624", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		buscaCnpj.setXml(respostaXML);
		
		return buscaCnpj.getXml();
	}
/*
	public static void main(String[] args) {
		BuscaEndTelefoneCnpj buscaCnpj = new BuscaEndTelefoneCnpj();
		
		//buscaCnpj = buscaEndTelefoneCnpj("00001", "08493296", "33014556000196");

		System.out.println(respostaXML("00001", "08493296", "06118205000152", "127.0.0.1"));
		
		if(buscaCnpj.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
		}
		System.out.println(buscaCnpj.getStatusRetorno());
		System.out.println(buscaCnpj.getMensagemRetorno());
	}*/
}
