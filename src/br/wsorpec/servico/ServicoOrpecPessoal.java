package br.wsorpec.servico;

import br.wsorpec.crediOnline.relatorioCPF.ExecutaRelatorioCPF;
import br.wsorpec.crediOnline.relatorioCPF.RelatorioCPF;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class ServicoOrpecPessoal {
	/*
	 * Servico SpcNetNacionalCnpj - Obrigatorio informar todos os parametros
	 * 
	 */
	public static RelatorioCPF orpecPessoal(String codigo, String senha, String cpf) {
		
		RelatorioCPF relatorioCpf = new RelatorioCPF();
		ExecutaRelatorioCPF executor = new ExecutaRelatorioCPF();

		//Gravar os parametros da consulta na Entidade
		relatorioCpf.setCodigo(codigo);
		relatorioCpf.setSenha(senha);
		relatorioCpf.setCpf(cpf);

		GlobalConstants.IPREMOTO = Util.ipRemoto();
		
		return executor.executar(relatorioCpf, false);
		
	}
	
	public static String respostaJSON(String codigo, String senha, String cpf, String ip) {
		RelatorioCPF relatorioCpf = new RelatorioCPF();
		ExecutaRelatorioCPF executor = new ExecutaRelatorioCPF();

		//Gravar os parametros da consulta na Entidade
		relatorioCpf.setCodigo(codigo);
		relatorioCpf.setSenha(senha);
		relatorioCpf.setCpf(cpf);
		
		if((ip!=null) && (ip!="")) {
			GlobalConstants.IPREMOTO = ip;
		}else {
			GlobalConstants.IPREMOTO = Util.ipRemoto();
		}
		
		relatorioCpf = executor.executar(relatorioCpf, true);
				
		if(relatorioCpf.getJson()==null || relatorioCpf.getJson()=="") {	
			relatorioCpf.setJson("{\"msg\":\""+relatorioCpf.getMensagemRetorno()+"\",\"status\":"+relatorioCpf.getStatusRetorno()+"}");			
		}
		
		return relatorioCpf.getJson();
	}
	
/*
	public static void main(String[] args){
		RelatorioCPF relatorioCpf = new RelatorioCPF();
		//XStream stream = new XStream(new DomDriver());
				
		//System.out.println(stream.toXML(orpecPessoal("00001", "08493296", "73820121404")));
		
		//relatorioCpf = orpecPessoal("00001", "08493296", "74071785772");
			
	    //System.out.println(respostaJSON("01004", "221074", "74071785772", "127.0.0.1"));
	    //System.out.println(respostaJSON("00001", "08493296", "74071785772", "127.0.0.1"));
	    
		if(relatorioCpf.getCredionlineRelatorioCpf().getCpf()!=null) {
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getCpf());
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getNome());
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getDtNascimento());
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getStatus());			
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getParticipacoes().size());
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getEmails().size());
			System.out.println(relatorioCpf.getCredionlineRelatorioCpf().getTelefones().size());
		}
		System.out.println(relatorioCpf.getStatusRetorno());
		System.out.println(relatorioCpf.getMensagemRetorno());
	}
*/
}
