package br.wsorpec.wscdlrio.negativacao;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class ExecutaListar {

	private static Log log;
	private static String protocolo;
	

	public ExecutaListar() {}
	
	public Listar executar(Listar listar, boolean xml) {
		XStream stream = new XStream(new DomDriver());
		
		if (!critica(listar)) {
			listar.setSenha("");
			return listar;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		
		//Criptografa a senha;
		listar.setSenha(Util.converteMD5(listar.getSenha(), protocolo, log));
		
		String codigo      = listar.getCodigo();
        String senha       = listar.getSenha();
		String produto     = "72";
		String solicitacao = "PRODUTO : "+produto+" - "+stream.toXML(listar).replace("\n", "");
		
		log.registro(protocolo, "SNDWSORPEC-LISTAR", solicitacao);
			
		//Fazer logon - verificar codigo, senha e se o produto pode ser consultado.
		Autenticacao aut = new Autenticacao();
		String strLogon = aut.logon(codigo, senha, produto, protocolo);
		if(!strLogon.equals("OK")) {
			log.registro(protocolo, "RCV", strLogon);
			listar.setCodigoRetorno("0");
			listar.setMensagemRetorno(strLogon);				
			return listar;
		}
		
		//Consulta Informacoes na tabela de negativacao.			
		Bilhetagem bilhetar = new Bilhetagem();
		bilhetar.consultarLista(listar, protocolo);

		log.registro(protocolo, "RCVWSORPEC-LISTAR", stream.toXML(listar).replace("\n", ""));
		
		return listar;
	}

	public boolean critica(Listar listar){
		
		if(Util.Empty(listar.getCodigo())){
			listar.setCodigoRetorno("0");
			listar.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(listar.getCodigo())){
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(listar.getSenha())){
			listar.setCodigoRetorno("0");
			listar.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(listar.getTipoPessoa())) {
			listar.setCodigoRetorno("0");
			listar.setMensagemRetorno("Tipo Pessoa nao informado");				
			return false;			
		}else {
			listar.getListaDoc().getDadosDocumento().setTipo("");
			if (!listar.getTipoPessoa().equals("F") && !listar.getTipoPessoa().equals("J") && 
				!listar.getTipoPessoa().equals("T")) {
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Tipo Pessoa invalido");		
				return false;
			}
			if (listar.getTipoPessoa().equals("F")) {
				if(!Util.Empty(listar.getDocumento())) {
					if (!Util.IsCPF(listar.getDocumento())) {
						listar.setCodigoRetorno("0");
						listar.setMensagemRetorno("CPF invalido");				
						return false;
					}
				}
				listar.getListaDoc().getDadosDocumento().setTipo("CPF");				
			}else if (listar.getTipoPessoa().equals("J")){
				if(!Util.Empty(listar.getDocumento())) {				
					if (!Util.IsCNPJ(listar.getDocumento())) {
						listar.setCodigoRetorno("0");
						listar.setMensagemRetorno("CNPJ invalido");				
						return false;
					}
				}
				listar.getListaDoc().getDadosDocumento().setTipo("CNPJ");				
			}
		}
		if(!Util.Empty(listar.getDataInicio())){
			if(!Util.isDateValid(listar.getDataInicio(), "ddMMyyyy")) {
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Data Inicial invalida");				
				return false;					
			}
		}
		if(!Util.Empty(listar.getDataFim())){
			if(!Util.isDateValid(listar.getDataFim(), "ddMMyyyy")) {
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Data Final invalida");				
				return false;					
			}
		}
		if(Util.Empty(listar.getStatus())){
			listar.setCodigoRetorno("0");
			listar.setMensagemRetorno("Status nao informado");				
			return false;
		}else {
			if(!listar.getStatus().equals("T") && !listar.getStatus().equals("B") && !listar.getStatus().equals("A")) {
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Status invalido");				
				return false;
			}
		}
		return true;
	}
}
