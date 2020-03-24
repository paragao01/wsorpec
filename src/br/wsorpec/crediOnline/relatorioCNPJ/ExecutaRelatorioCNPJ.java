package br.wsorpec.crediOnline.relatorioCNPJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.cache.Cache;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class ExecutaRelatorioCNPJ {
	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaRelatorioCNPJ() {}
	
	public RelatorioCNPJ executar(RelatorioCNPJ relatorioCnpj, boolean xml) {
		
		if (!critica(relatorioCnpj)) {
			relatorioCnpj.setSenha("");
			return relatorioCnpj;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		relatorioCnpj.setSenha(Util.converteMD5(relatorioCnpj.getSenha(), protocolo, log));
		
		String ipRemoto    = Util.ipRemoto();
		boolean isCache    = false;
		String codigo      = relatorioCnpj.getCodigo();
        String senha       = relatorioCnpj.getSenha();
		String produto     = "55";
		String solicitacao = "Produto="+produto+";Codigo="+relatorioCnpj.getCodigo()+";Senha="+relatorioCnpj.getSenha()+";CNPJ="+relatorioCnpj.getCnpj();
				
		resposta = cache.cacheConsulta(solicitacao, protocolo, log); // Verifica cache
		
		if (resposta.trim().length() > 0) { // verifica se o cache respondeu
			isCache = true;
		}

		if (!isCache) { // Se nao foi respondido pelo cache da procedimento.
			log.registro(protocolo, "SND", solicitacao);
			
			//Fazer logon - verificar codigo, senha e se o produto pode ser consultado.
			Autenticacao aut = new Autenticacao();
			String strLogon = aut.logon(codigo, senha, produto, protocolo);
			if(!strLogon.equals("OK")) {
				cache.apagaCache(protocolo);
				log.registro(protocolo, "RCV", strLogon);
				relatorioCnpj.setStatusRetorno(1);
				relatorioCnpj.setMensagemRetorno(strLogon);				
				return relatorioCnpj;
			}
						
			//Fazer a consulta nos BUREAU 			
			relatorioCnpj = consultaCrediOnline(relatorioCnpj);	
			
			if(relatorioCnpj.getStatusRetorno()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, relatorioCnpj.getCnpj(), produto, ipRemoto, protocolo);
			}
			
			if(xml){// Quando a resposta retorna só o xml
				relatorioCnpj.setJson(resposta);
			}else {
				relatorioCnpj.setJson("");
			}
			cache.cacheResposta(resposta, protocolo);
			log.registro(protocolo, "RCV", resposta);
		}else{// Se foi respondido pelo cache
			Gson gson = new Gson();
			CrediOnLineRelatorioCNPJ obj = new CrediOnLineRelatorioCNPJ();
			JsonReader jsonReader = new JsonReader(new StringReader(resposta));
			jsonReader.setLenient(true);
			obj = gson.fromJson(jsonReader, CrediOnLineRelatorioCNPJ.class);
			
			relatorioCnpj.setCredionlineRelatorioCnpj(obj);
			if(xml){relatorioCnpj.setJson(resposta);}// Quando a resposta retorna só o xml
		}
		return relatorioCnpj;
	}

	public boolean critica(RelatorioCNPJ relatorioCnpj){
		
		if(Util.Empty(relatorioCnpj.getCodigo())){
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(relatorioCnpj.getCodigo())){
				relatorioCnpj.setStatusRetorno(1);
				relatorioCnpj.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(relatorioCnpj.getSenha())){
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(relatorioCnpj.getCnpj())) {
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("CNPJ nao informado");				
			return false;			
		}else {
			if (!Util.IsCNPJ(relatorioCnpj.getCnpj())) {
				relatorioCnpj.setStatusRetorno(1);
				relatorioCnpj.setMensagemRetorno("CNPJ invalido");				
				return false;
			}
		}
		return true;
	}
		
	public RelatorioCNPJ consultaCrediOnline(RelatorioCNPJ relatorioCnpj) {
		CrediOnLineRestritivoCNPJ obj1 = new CrediOnLineRestritivoCNPJ();		
		CrediOnLineRelatorioCNPJ  obj2 = new CrediOnLineRelatorioCNPJ();
		String respostaGeral;
		
		try {
			//Primeira Consulta
			relatorioCnpj = httpConsultaCrediOnline(relatorioCnpj, "restritivos/cnpj/");
			
			if(relatorioCnpj.getStatusRetorno()==0) {
				Gson gson = new Gson();			
				obj1 = gson.fromJson(relatorioCnpj.getJson(), CrediOnLineRestritivoCNPJ.class);
				relatorioCnpj.setCredionlineRestritivoCnpj(obj1);
				
				respostaGeral = relatorioCnpj.getJson();
				
				//Segunda Consulta
				relatorioCnpj = httpConsultaCrediOnline(relatorioCnpj, "relatorio/cnpj/");
				
				if(relatorioCnpj.getStatusRetorno()==0) {
					Gson gson2 = new Gson();
					obj2 = gson2.fromJson(relatorioCnpj.getJson(), CrediOnLineRelatorioCNPJ.class);
					relatorioCnpj.setCredionlineRelatorioCnpj(obj2);
				
					respostaGeral = respostaGeral+"<>"+relatorioCnpj.getJson();
					resposta = respostaGeral;
					
					relatorioCnpj.setStatusRetorno(0);
					relatorioCnpj.setMensagemRetorno("Consulta realizada");
				}
			}
		} catch (JsonSyntaxException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO-CREDIOLINE JsonSyntaxException ", e.getMessage());
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");			
		}
		return relatorioCnpj;
	}	

	public RelatorioCNPJ httpConsultaCrediOnline(RelatorioCNPJ relatorioCnpj, String requisicao) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		String solicitacao = "";
		int HTTP_COD_SUCESSO = 200;
		String resp = "";
		
		solicitacao = requisicao +
					  relatorioCnpj.getCnpj() +
					  "?email=" +
					  GlobalConstants.EMAILCRED +
					  "&token=" +
					  GlobalConstants.TOKENCRED;
		try {
			url = new URL(GlobalConstants.URLCRED+solicitacao);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			
			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				if(con.getResponseCode()==400) {
					relatorioCnpj.setStatusRetorno(400);
					relatorioCnpj.setMensagemRetorno("Documento invalido, Produto : "+requisicao);
				}else if(con.getResponseCode()==401) {
					relatorioCnpj.setStatusRetorno(401);
					relatorioCnpj.setMensagemRetorno("Acesso negado, Produto : "+requisicao);			
				}else if(con.getResponseCode()==404) {
					relatorioCnpj.setStatusRetorno(404);
					relatorioCnpj.setMensagemRetorno("Documento nao encontrado, Produto : "+requisicao);
				}else if(con.getResponseCode()==429) {
					relatorioCnpj.setStatusRetorno(429);
					relatorioCnpj.setMensagemRetorno("Limite de consulta excedido, Produto : "+requisicao);			
				}else {
					relatorioCnpj.setStatusRetorno(1);
					relatorioCnpj.setMensagemRetorno("Servico não Disponivel, Produto : "+requisicao);			
				}
				resposta = "{\"msg\":\""+relatorioCnpj.getMensagemRetorno()+"\",\"status\":"+relatorioCnpj.getStatusRetorno()+"}";
				cache.apagaCache(protocolo);
				log.registro(protocolo.trim(), "INF", "Apaga cache, "+relatorioCnpj.getMensagemRetorno());
				return relatorioCnpj;
			}

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resp += line.trim();
			}
			
			br.close();
			con.disconnect();
			
			relatorioCnpj.setStatusRetorno(0);
			relatorioCnpj.setMensagemRetorno("Consulta realizada");	
			relatorioCnpj.setJson(resp);
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO-CREDIOLINE IOException ", e.getMessage());
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");			
		}
		return relatorioCnpj;
	}	

}
