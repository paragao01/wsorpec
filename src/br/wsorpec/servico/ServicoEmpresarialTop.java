package br.wsorpec.servico;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.empresarialTop.EmpresarialTop;
import br.wsorpec.wscdlrio.empresarialTop.ExecutaEmpresarialTop;

public class ServicoEmpresarialTop {

	/*
	 * Servico DefineRisco - Obrigatorio informar todos os parametros
	 * 
	 */
	public static EmpresarialTop empresarialTop(String codigo, 
									String senha, 
									String cnpj) {
		
		EmpresarialTop empTop = new EmpresarialTop();
		ExecutaEmpresarialTop executor = new ExecutaEmpresarialTop();

		//Gravar os parametros da consulta na Entidade
		empTop.setCodigo(codigo);
		empTop.setSenha(senha);
		empTop.setCnpj(cnpj);
		
		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(empTop, false);

	}

	public static String respostaXML(String codigo, 
									 String senha, 
									 String cnpj,
									 String ip) {
		
		String respostaXML = "";
		EmpresarialTop empTop = new EmpresarialTop();
		ExecutaEmpresarialTop executor = new ExecutaEmpresarialTop();

		//Gravar os parametros da consulta na Entidade
		empTop.setCodigo(codigo);
		empTop.setSenha(senha);
		empTop.setCnpj(cnpj);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		empTop = executor.executar(empTop, true);
		
		respostaXML = empTop.getXml();

		if(respostaXML==null || respostaXML=="" ||
		   empTop.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()>0) {
			respostaXML = "<SPCA-XML><SOLICITACAO><S-CODIGO>0</S-CODIGO><S-SENHA>0</S-SENHA><S-CONSULTA>74</S-CONSULTA><S-SOLICITANTE></S-SOLICITANTE><S-CNPJ>"+cnpj+"</S-CNPJ><S-TIPO-CREDITO>XX</S-TIPO-CREDITO><S-SPCNET-PRODUTO><S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA></S-SPCNET-PRODUTO></SOLICITACAO><RESPOSTA><IP-SOLICITANTE>"+ip+"</IP-SOLICITANTE><RESPOSTA-RETORNO><STATUS-RESPOSTA>9</STATUS-RESPOSTA><MENSAGEM-RESPOSTA>ERRO:</MENSAGEM-RESPOSTA></RESPOSTA-RETORNO></RESPOSTA></SPCA-XML>";
		}
		respostaXML = Util.gravaTag("<S-CODIGO>", codigo, respostaXML);
		respostaXML = Util.gravaTag("<S-SENHA>", "", respostaXML);
		respostaXML = Util.gravaTag("<S-CNPJ>", cnpj, respostaXML);
		respostaXML = Util.gravaTag("<STATUS-RESPOSTA>", String.valueOf((byte)empTop.getStatusRetorno()), respostaXML);			
		respostaXML = Util.gravaTag("<MENSAGEM-RESPOSTA>", empTop.getMensagemRetorno(), respostaXML);
		respostaXML = Util.gravaTag("<S-CONSULTA>", "74", respostaXML);
		respostaXML = Util.gravaTag("<IP-SOLICITANTE>", GlobalConstants.IPREMOTO, respostaXML);
		
		empTop.setXml(respostaXML);
		
		return empTop.getXml();
	}
/*
	public static void main(String[] args) {
		EmpresarialTop empTop = new EmpresarialTop();
				
		//empTop = empresarialTop("00001","08493296","33014556000196");

		System.out.println(respostaXML("00001", "08493296", "33014556000196", "129.0.0.1"));
		
		
		//if(empTop.getSpcaxml().getRESPOSTA()!=null) {
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getIPSOLICITANTE());
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getNUMERORESPOSTA());
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getASSOCIADOSOLICITANTE());
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getNOMECONSULTA());
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
		//	System.out.println(empTop.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());	
		//}
		
		System.out.println(empTop.getStatusRetorno());
		System.out.println(empTop.getMensagemRetorno());	
	}
*/
}
