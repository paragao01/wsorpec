package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.pessoalPlus.ExecutaPessoalPlus;
import br.wsorpec.wscdlrio.pessoalPlus.PessoalPlus;

public class ServicoPessoalPlus {

	/*
	 * Servico Acerta - Obrigatorio informar todos os parametros
	 * 
	 */
	public static PessoalPlus pessoalPlus(String codigo, String senha, String cpf) {
		
		PessoalPlus pesPlus = new PessoalPlus();
		ExecutaPessoalPlus executor = new ExecutaPessoalPlus();

		//Gravar os parametros da consulta na Entidade
		pesPlus.setCodigo(codigo);
		pesPlus.setSenha(senha);
		pesPlus.setCpf(cpf);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(pesPlus, false);

	}
	
	public static String respostaXML(String codigo, String senha, String cpf, String ip) {
		String respostaXML = "";
		PessoalPlus pesPlus = new PessoalPlus();
		ExecutaPessoalPlus executor = new ExecutaPessoalPlus();

		//Gravar os parametros da consulta na Entidade
		pesPlus.setCodigo(codigo);
		pesPlus.setSenha(senha);
		pesPlus.setCpf(cpf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
				
		pesPlus = executor.executar(pesPlus, true);
		
		respostaXML = pesPlus.getXml();

		if(respostaXML==null || respostaXML=="" || 
				Integer.valueOf(pesPlus.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA())>0) {
				respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>73</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ>"+cpf+"</S-CNPJ><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>"+ip+"</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO:</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CPF>", cpf, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)pesPlus.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", pesPlus.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "73", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		pesPlus.setXml(respostaXML);	
		
		return pesPlus.getXml();
	}

	public static void main(String[] args) {
		PessoalPlus pesPlus = new PessoalPlus();
		
		//pesPlus = pessoalPlus("00001", "08493296", "12312312387");
		
		System.out.println(respostaXML("00001", "08493296", "12312312387", "127.0.0.1"));
		
		if(pesPlus.getSpcaxml().getRESPOSTA()!=null) {
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
			System.out.println("************************************************");
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126TIPO());			
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126DESCRICAO());
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126DATA());			
			System.out.println(pesPlus.getSpcaxml().getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().get(0).getSPCA126INFORMANTE());			
		}
		System.out.println("************************************************");		
		System.out.println(pesPlus.getStatusRetorno());
		System.out.println(pesPlus.getMensagemRetorno());
		
	}
}
