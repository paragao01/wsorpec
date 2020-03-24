package br.wsorpec.servico;

import br.wsorpec.crediOnline.relatorioCNPJ.ExecutaRelatorioCNPJ;
import br.wsorpec.crediOnline.relatorioCNPJ.RelatorioCNPJ;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class ServicoOrpecEmpresarial {
	/*
	 * Servico SpcNetNacionalCnpj - Obrigatorio informar todos os parametros
	 * 
	 */
	public static  RelatorioCNPJ orpecEmpresarial(String codigo, String senha, String cnpj) {
		
		RelatorioCNPJ relatorioCnpj = new RelatorioCNPJ();
		ExecutaRelatorioCNPJ executor = new ExecutaRelatorioCNPJ();

		//Gravar os parametros da consulta na Entidade
		relatorioCnpj.setCodigo(codigo);
		relatorioCnpj.setSenha(senha);
		relatorioCnpj.setCnpj(cnpj);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(relatorioCnpj, false);
		
	}

	public static String respostaJSON(String codigo, String senha, String cnpj, String ip) {
		RelatorioCNPJ relatorioCnpj = new RelatorioCNPJ();
		ExecutaRelatorioCNPJ executor = new ExecutaRelatorioCNPJ();

		//Gravar os parametros da consulta na Entidade
		relatorioCnpj.setCodigo(codigo);
		relatorioCnpj.setSenha(senha);
		relatorioCnpj.setCnpj(cnpj);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else{
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		relatorioCnpj = executor.executar(relatorioCnpj, true);
				
		if(relatorioCnpj.getJson()==null || relatorioCnpj.getJson()=="") {
			relatorioCnpj.setJson("{\"msg\":\""+relatorioCnpj.getMensagemRetorno()+"\",\"status\":"+relatorioCnpj.getStatusRetorno()+"}");			
		}
		
		return relatorioCnpj.getJson();
	}

/*
	public static void main(String[] args) {
		RelatorioCNPJ relatorioCnpj = new RelatorioCNPJ();
		XStream stream = new XStream(new DomDriver());
		
		//System.out.println(stream.toXML(orpecEmpresarial("00001", "08493296", "33014556000196")));
		
		//relatorioCnpj = orpecEmpresarial("00001", "08493296", "05393382000184");
		
		System.out.println(respostaJSON("00001", "08493296", "33014556000196", "127.0.0.1"));
		
		if(relatorioCnpj.getCredionlineRelatorioCnpj().getCnpj()!=null) {
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getCnpj());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getRazaoSocial());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getAtividade01());
			System.out.println("----------------------------------------------");
			
			Iterator<String> itr = relatorioCnpj.getCredionlineRelatorioCnpj().getAtividade02().iterator();
			while(itr.hasNext()) {
		         String element = itr.next();
		         System.out.println(element);
		    }
			System.out.println("----------------------------------------------");			
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getNatJur());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getSitCad());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getCoberturaCartorios().getQtdCartorio());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getCoberturaCartorios().getQtdCartorioParticipa());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getCoberturaCartorios().getQtdMun());
			System.out.println("----------------------------------------------");			
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(0).getCargo());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(0).getRazaoSocial());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(0).getCnpj());	
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(1).getCargo());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(1).getRazaoSocial());
			System.out.println(relatorioCnpj.getCredionlineRelatorioCnpj().getParticipacoesEmpresa().get(1).getCnpj());			
			
		}
		System.out.println(relatorioCnpj.getStatusRetorno());
		System.out.println(relatorioCnpj.getMensagemRetorno());
	}
*/
}
