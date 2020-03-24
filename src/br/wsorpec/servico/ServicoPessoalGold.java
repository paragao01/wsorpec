package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.pessoalGold.PessoalGolde;
import br.wsorpec.wscdlrio.pessoalGold.ExecutaPessoalGold;

public class ServicoPessoalGold {

	/*
	 * Servico AcertaEssencial - Obrigatorio informar todos os parametros
	 * 
	 */
	public static PessoalGolde pessoalGold(String codigo, String senha, String cpf) {
		
		PessoalGolde pesGold = new PessoalGolde();
		ExecutaPessoalGold executor = new ExecutaPessoalGold();

		//Gravar os parametros da consulta na Entidade
		pesGold.setCodigo(codigo);
		pesGold.setSenha(senha);
		pesGold.setCpf(cpf);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(pesGold, false);

	}
	
	public static String respostaXML(String codigo, String senha, String cpf, String ip) {
		String respostaXML = "";
		PessoalGolde pesGold = new PessoalGolde();
		ExecutaPessoalGold executor = new ExecutaPessoalGold();

		//Gravar os parametros da consulta na Entidade
		pesGold.setCodigo(codigo);
		pesGold.setSenha(senha);
		pesGold.setCpf(cpf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
				
		pesGold = executor.executar(pesGold, true);
		
		respostaXML = pesGold.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   Integer.valueOf(pesGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA())>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>296</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CPF>12312312387</S-CPF><S-CHEQUE-ORIGEM>D</S-CHEQUE-ORIGEM><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO: TRANSACAO BLOQUEADA PARA ESTA SENHA(277)</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CPF>", cpf, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)pesGold.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", pesGold.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "48", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		pesGold.setXml(respostaXML);	
		
		return pesGold.getXml();
	}
/*
	public static void main(String[] args) {
		PessoalGolde pesGold = new PessoalGolde();
		
		//pesGold = pessoalGold("00001", "08493296", "12312312387", "127.0.0.1");
		
		System.out.println(respostaXML("00001", "08493296", "74071785772", "127.0.0.1"));
		
		if(pesGold.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126TIPO());			
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126DESCRICAO());
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126DATA());			
			System.out.println(pesGold.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126INFORMANTE());			
		}
		System.out.println("************************************************");		
		System.out.println(pesGold.getStatusRetorno());
		System.out.println(pesGold.getMensagemRetorno());
		
	}*/
}
