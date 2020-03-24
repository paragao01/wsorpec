package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.buscaEndTelefoneCpf.BuscaEndTelefoneCpf;
import br.wsorpec.wscdlrio.buscaEndTelefoneCpf.ExecutaBuscaEndTelefoneCpf;

public class ServicoBuscaEndTelefoneCpf {
	/*
	 * Servico buscaEndTelefoneCpf - Obrigatorio informar todos os parametros
	 * 
	 */
	public static BuscaEndTelefoneCpf buscaEndTelefoneCpf(String codigo, String senha, String cpf) {
		
		BuscaEndTelefoneCpf buscaCpf = new BuscaEndTelefoneCpf();
		ExecutaBuscaEndTelefoneCpf executor = new ExecutaBuscaEndTelefoneCpf();

		//Gravar os parametros da consulta na Entidade
		buscaCpf.setCodigo(codigo);
		buscaCpf.setSenha(senha);
		buscaCpf.setCpf(cpf);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(buscaCpf, false);

	}

	public static String respostaXML(String codigo, String senha, String cpf, String ip) {
		String respostaXML = "";
		BuscaEndTelefoneCpf buscaCpf = new BuscaEndTelefoneCpf();
		ExecutaBuscaEndTelefoneCpf executor = new ExecutaBuscaEndTelefoneCpf();

		//Gravar os parametros da consulta na Entidade
		buscaCpf.setCodigo(codigo);
		buscaCpf.setSenha(senha);
		buscaCpf.setCpf(cpf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		buscaCpf = executor.executar(buscaCpf, true);
		
		respostaXML = buscaCpf.getXml();
		
		if(respostaXML==null || respostaXML=="" ||
		   Integer.valueOf(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA())>0) {			
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CPF>12312312387</S-CPF><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO: TRANSACAO BLOQUEADA PARA ESTA SENHA(277)</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CPF>", cpf, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)buscaCpf.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", buscaCpf.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "53", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		buscaCpf.setXml(respostaXML);
		
		return buscaCpf.getXml();
	}
/*
	public static void main(String[] args) {
		BuscaEndTelefoneCpf buscaCpf = new BuscaEndTelefoneCpf();
		
		//buscaCpf = buscaEndTelefoneCpf("00001", "08493296", "74071785772");
		
		System.out.println(respostaXML("00001", "08493296", "74071785772", "127.0.0.1"));
		
		if(buscaCpf.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA500IDENTIFICA().getSPCA500DOCUMENTO());
			System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA501LOCALIZACAO().getSPCA501NOME());
			//System.out.println(buscaCpf.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA502GRAFIAS().get(0).getSPCA502CPF());				
		}else {
			System.out.println(buscaCpf.getStatusRetorno());
			System.out.println(buscaCpf.getMensagemRetorno());
		}
	}*/
}
