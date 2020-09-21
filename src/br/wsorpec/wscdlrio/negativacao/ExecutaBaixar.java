package br.wsorpec.wscdlrio.negativacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.Formatador;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.LerRespostaV7;
import br.wsorpec.util.ParteFixaV7;
import br.wsorpec.util.Util;

public class ExecutaBaixar {

	private static Log log;
	private static String protocolo;
	

	public ExecutaBaixar() {}
	
	public Baixar executar(Baixar baixar, boolean xml) {
		XStream stream = new XStream(new DomDriver());
		
		if (!critica(baixar)) {
			baixar.setSenha("");
			return baixar;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		
		//Criptografa a senha;
		baixar.setSenha(Util.converteMD5(baixar.getSenha(), protocolo, log));
		
		String codigo      = baixar.getCodigo();
        String senha       = baixar.getSenha();
		String produto     = "72";
		String solicitacao = "PRODUTO : "+produto+" - "+stream.toXML(baixar).replace("\n", "");
		
		log.registro(protocolo, "SNDWSORPEC-BAIXAR", solicitacao);
			
		//Fazer logon - verificar codigo, senha e se o produto pode ser consultado.
		Autenticacao aut = new Autenticacao();
		String strLogon = aut.logon(codigo, senha, produto, protocolo);
		if(!strLogon.equals("OK")) {
			log.registro(protocolo, "RCV", strLogon);
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno(strLogon);				
			return baixar;
		}
						
		// Fazer a consulta nos BUREAU
		baixar = consultaCDLRio(baixar);
		
		//Fazer a bilhetagem 
		//if(baixar.getCodigoRetorno().equals("1")) {
			Bilhetagem bilhetar = new Bilhetagem();
			//bilhetar.bilhetar(GlobalConstants.idCliente, baixar.getDocumento(), produto, GlobalConstants.IPREMOTO, protocolo);
			//Grava Informacoes na tabela de negativacao.
			bilhetar.gravarBaixa(baixar, protocolo);
		//}

		log.registro(protocolo, "RCVWSORPEC-BAIXAR", stream.toXML(baixar).replace("\n", ""));
		
		return baixar;
	}

	public boolean critica(Baixar baixar){
		
		if(Util.Empty(baixar.getCodigo())){
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(baixar.getCodigo())){
				baixar.setCodigoRetorno("0");
				baixar.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(baixar.getSenha())){
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(baixar.getTipoPessoa())) {
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Tipo de pessoa nao informada");				
			return false;			
		}else {
			if (!baixar.getTipoPessoa().equals("F") && !baixar.getTipoPessoa().equals("J")) {
				baixar.setCodigoRetorno("0");
				baixar.setMensagemRetorno("Tipo de pessoa invalida");				
				return false;
			}
		}
		if (Util.Empty(baixar.getDocumento())) {
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Documento nao informado");				
			return false;			
		}else {
			if(baixar.getTipoPessoa().equals("F")) {
				if (!Util.IsCPF(baixar.getDocumento())) {
					baixar.setCodigoRetorno("0");
					baixar.setMensagemRetorno("CPF invalido");				
					return false;
				}
				baixar.getDadosBaixa().setCpf(baixar.getDocumento());
				baixar.getDadosBaixa().setDocumento(baixar.getDocumento());
				baixar.getDadosBaixa().setTipoDocumento("CPF");
			}else{
				if (!Util.IsCNPJ(baixar.getDocumento())) {
					baixar.setCodigoRetorno("0");
					baixar.setMensagemRetorno("CNPJ invalido");				
					return false;
				}
				baixar.getDadosBaixa().setDocumento(baixar.getDocumento());
				baixar.getDadosBaixa().setTipoDocumento("CNPJ");
			}
		}
		if(baixar.getIdNegativacao()==0){
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("ID de Negativacao nao informado");				
			return false;
		}				
		if(Util.Empty(baixar.getDadosBaixa().getContrato())){
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Contrato nao informado");				
			return false;
		}
		if(Util.Empty(baixar.getDadosBaixa().getMotivo())){
			baixar.setCodigoRetorno("0");
			baixar.setMensagemRetorno("Motivo nao informado");				
			return false;
		}
		return true;
	}
	
	public Baixar consultaCDLRio(Baixar baixar) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		String retorno = "";
		
		try {
			url = new URL(GlobalConstants.URLCDLNEG+montaSolicitacao(baixar));
			con = (HttpURLConnection) url.openConnection();

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				retorno += line.trim();
			}
			
			br.close();
			con.disconnect();
			baixar = montaResposta(baixar, retorno);
		} catch (IOException e) {
			//cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			baixar.setCodigoRetorno("00099");
			baixar.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		}
		return baixar;
	}	
		
	public String montaSolicitacao(Baixar bai) {
		ParteFixaV7 parteFixa = new ParteFixaV7();
		if(bai.getTipoPessoa().equals("F")) {
			parteFixa.setTransacao("SPCSBXC");
		} else {
			parteFixa.setTransacao("SIASBAX");			
		}
		parteFixa.setEntidade(GlobalConstants.entidade);
		parteFixa.setFirma(GlobalConstants.firma);
		parteFixa.setSenha(GlobalConstants.senha);		
		String solicitacao = parteFixa.getStr();
		String valor = bai.getDadosBaixa().getValorParcela();
		
		if(bai.getTipoPessoa().equals("F")) {
			solicitacao += 
					 //Contrato
					  Formatador.formataCampo(bai.getDadosBaixa().getContrato(), 20)
					 +Formatador.zeroEsquerda(bai.getDadosBaixa().getMotivo(), 2)					 
					 +Formatador.formataCampo(bai.getDadosBaixa().getNome(), 60)
					 +Formatador.zeroEsquerda(bai.getDadosBaixa().getDataNascimento(), 8)
					 +Formatador.zeroEsquerda(bai.getDadosBaixa().getCpf(), 11)
					 +Formatador.formataCampo(bai.getDadosBaixa().getDocumento(), 15)
					 +Formatador.formataCampo(bai.getDadosBaixa().getTipoDocumento(), 15)
					 +Formatador.formataCampo(bai.getDadosBaixa().getEstado(), 2)
					 +Formatador.formataCampo("", 50);
		} else {
			solicitacao += 
					 //Contrato
					  Formatador.zeroEsquerda(bai.getDadosBaixa().getDocumento(), 14)					
					 +Formatador.formataCampo(bai.getDadosBaixa().getContrato(), 20)
					 +Formatador.zeroEsquerda(bai.getDadosBaixa().getMotivo(), 2)					 
					 +Formatador.formataCampo(bai.getDadosBaixa().getNome(), 60)
					 +Formatador.zeroEsquerda(bai.getDadosBaixa().getDataAtraso(), 8)
					 +Formatador.zeroEsquerda(valor, 14)
					 +Formatador.formataCampo("", 82);
		}
		 
		log.registro(protocolo, "SNDCDLRio-BAIXAR", solicitacao);
		
		return solicitacao.replaceAll(" ","%20");
	}
	
	public Baixar montaResposta(Baixar bai, String retorno) {
   		LerRespostaV7 lrv = new LerRespostaV7();
   		ArrayList<LerRespostaV7> listaResp = new ArrayList<LerRespostaV7>();

		retorno = retorno.replace("<HTML><HEAD></HEAD><BODY>","");
		retorno = retorno.replace("</BODY></HTML>","");
		
		if(retorno.substring(0, 7).equals("SPCRBXC")) {
			bai.setCodigoRetorno("1");
			bai.setMensagemRetorno("Baixa realizada com sucesso");
			GlobalConstants.protocoloCDLRio = retorno.substring(63, 72);
		} else if(retorno.substring(0, 7).equals("SIARBAX")) {
			bai.setCodigoRetorno("1");
			bai.setMensagemRetorno("Baixa realizada com sucesso");
			GlobalConstants.protocoloCDLRio = retorno.substring(63, 72);
		}else{
       		String registro001 = "";
       		String strRet = retorno;
       		       		
			listaResp = lrv.separaResposta(retorno);
			
			for (int i = 0; i < listaResp.size(); i++) {
				lrv = (LerRespostaV7) listaResp.get(i);

				switch (lrv.getCodigo()) {
				case 1:
					registro001 = strRet.substring(lrv.getInicio() - 1, lrv.getFim());
					break;
				}
			}
			if (registro001.trim().length()>0) {
				bai.setCodigoRetorno(registro001.substring(2, 7));
				bai.setMensagemRetorno(registro001.substring(7, 44));
			}
		}
		log.registro(protocolo, "RCVCDLRio-BAIXAR", retorno);;		
		return bai;
	}
}
