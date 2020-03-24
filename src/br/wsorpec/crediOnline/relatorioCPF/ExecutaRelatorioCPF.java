package br.wsorpec.crediOnline.relatorioCPF;

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

public class ExecutaRelatorioCPF {
	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaRelatorioCPF() {}
	
	public RelatorioCPF executar(RelatorioCPF relatorioCpf, boolean xml) {
		
		if (!critica(relatorioCpf)) {
			relatorioCpf.setSenha("");
			return relatorioCpf;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		relatorioCpf.setSenha(Util.converteMD5(relatorioCpf.getSenha(), protocolo, log));
		
		String ipRemoto    = Util.ipRemoto();
		boolean isCache    = false;
		String codigo      = relatorioCpf.getCodigo();
        String senha       = relatorioCpf.getSenha();
		String produto     = "60";
		String solicitacao = "Produto="+produto+";Codigo="+relatorioCpf.getCodigo()+";Senha="+relatorioCpf.getSenha()+";CPF="+relatorioCpf.getCpf();
				
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
				relatorioCpf.setStatusRetorno(1);
				relatorioCpf.setMensagemRetorno(strLogon);				
				return relatorioCpf;
			}
						
			//Fazer a consulta nos BUREAU 			
			relatorioCpf = consultaCrediOnline(relatorioCpf);	
			
			if(relatorioCpf.getStatusRetorno()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, relatorioCpf.getCpf(), produto, ipRemoto, protocolo);
			}
			
			if(xml){// Quando a resposta retorna só o xml
				relatorioCpf.setJson(resposta);
			}else{
				relatorioCpf.setJson("");
			}
			cache.cacheResposta(resposta, protocolo);
			log.registro(protocolo, "RCV", resposta);
		}else{// Se foi respondido pelo cache
			Gson gson = new Gson();
			CrediOnLineRelatorioCPF obj = new CrediOnLineRelatorioCPF();
			JsonReader jsonReader = new JsonReader(new StringReader(resposta));
			jsonReader.setLenient(true);
			obj = gson.fromJson(jsonReader, CrediOnLineRelatorioCPF.class);
			
			relatorioCpf.setCredionlineRelatorioCpf(obj);
			if(xml){relatorioCpf.setJson(resposta);}// Quando a resposta retorna só o xml
		}
		return relatorioCpf;
	}

	public boolean critica(RelatorioCPF relatorioCpf){
		
		if(Util.Empty(relatorioCpf.getCodigo())){
			relatorioCpf.setStatusRetorno(1);
			relatorioCpf.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(relatorioCpf.getCodigo())){
				relatorioCpf.setStatusRetorno(1);
				relatorioCpf.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(relatorioCpf.getSenha())){
			relatorioCpf.setStatusRetorno(1);
			relatorioCpf.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(relatorioCpf.getCpf())) {
			relatorioCpf.setStatusRetorno(1);
			relatorioCpf.setMensagemRetorno("CPF nao informado");				
			return false;			
		}else {
			if (!Util.IsCPF(relatorioCpf.getCpf())) {
				relatorioCpf.setStatusRetorno(1);
				relatorioCpf.setMensagemRetorno("CPF invalido");				
				return false;
			}
		}
		return true;
	}
		
	public RelatorioCPF consultaCrediOnline(RelatorioCPF relatorioCpf) {
		CrediOnLineRestritivoCPF obj1 = new CrediOnLineRestritivoCPF();		
		CrediOnLineRelatorioCPF  obj2 = new CrediOnLineRelatorioCPF();
		CrediOnLineChequeCPF     obj3 = new CrediOnLineChequeCPF();
		
		String respostaGeral;
		
		try {
			//Primeira Consulta
			relatorioCpf = httpConsultaCrediOnline(relatorioCpf, "restritivos/cpf/");
			
			if(relatorioCpf.getStatusRetorno()==0) {
				Gson gson = new Gson();			
				obj1 = gson.fromJson(relatorioCpf.getJson(), CrediOnLineRestritivoCPF.class);
				relatorioCpf.setCredionlineRestritivoCpf(obj1);
				
				respostaGeral = relatorioCpf.getJson();
				
				//Segunda Consulta
				relatorioCpf = httpConsultaCrediOnline(relatorioCpf, "relatorio/cpf/");
				
				if(relatorioCpf.getStatusRetorno()==0) {
					Gson gson2 = new Gson();			
					obj2 = gson2.fromJson(relatorioCpf.getJson(), CrediOnLineRelatorioCPF.class);
					relatorioCpf.setCredionlineRelatorioCpf(obj2);
				
					respostaGeral = respostaGeral+"<>"+relatorioCpf.getJson();
					
					//Terceira Consulta
					relatorioCpf = httpConsultaCrediOnline(relatorioCpf, "cheques/cpf/");
					
					if(relatorioCpf.getStatusRetorno()==0) {
						Gson gson3 = new Gson();			
						obj3 = gson3.fromJson(relatorioCpf.getJson(), CrediOnLineChequeCPF.class);
						relatorioCpf.setCredionlineChequeCpf(obj3);
					
						respostaGeral = respostaGeral+"<>"+relatorioCpf.getJson();
						resposta = respostaGeral;
						
						relatorioCpf.setStatusRetorno(0);
						relatorioCpf.setMensagemRetorno("Consulta realizada");
					}
				}
			}
		} catch (JsonSyntaxException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO-CREDIOLINE JsonSyntaxException ", e.getMessage());
			relatorioCpf.setStatusRetorno(1);
			relatorioCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");			
		}
		return relatorioCpf;
	}	

	public RelatorioCPF httpConsultaCrediOnline(RelatorioCPF relatorioCpf, String requisicao) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		String solicitacao = "";
		int HTTP_COD_SUCESSO = 200;
		String resp = "";
		
		solicitacao = requisicao +
					  relatorioCpf.getCpf()+
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
					relatorioCpf.setStatusRetorno(400);
					relatorioCpf.setMensagemRetorno("Documento invalido, Produto : "+requisicao);
				}else if(con.getResponseCode()==401) {
					relatorioCpf.setStatusRetorno(401);
					relatorioCpf.setMensagemRetorno("Acesso negado, Produto : "+requisicao);			
				}else if(con.getResponseCode()==404) {
					relatorioCpf.setStatusRetorno(404);
					relatorioCpf.setMensagemRetorno("Documento nao encontrado, Produto : "+requisicao);
				}else if(con.getResponseCode()==429) {
					relatorioCpf.setStatusRetorno(429);
					relatorioCpf.setMensagemRetorno("Limite de consulta excedido, Produto : "+requisicao);			
				}else {
					relatorioCpf.setStatusRetorno(1);
					relatorioCpf.setMensagemRetorno("Servico não Disponivel, Produto : "+requisicao);			
				}
				resposta = "{\"msg\":\""+relatorioCpf.getMensagemRetorno()+"\",\"status\":"+relatorioCpf.getStatusRetorno()+"}";
				cache.apagaCache(protocolo);
				log.registro(protocolo.trim(), "INF", "Apaga cache, "+relatorioCpf.getMensagemRetorno());
				return relatorioCpf;
			}

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resp += line.trim();
			}
			
			br.close();
			con.disconnect();
			
			relatorioCpf.setStatusRetorno(0);
			relatorioCpf.setMensagemRetorno("Consulta realizada");	
			relatorioCpf.setJson(resp);
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO-CREDIOLINE IOException ", e.getMessage());
			e.printStackTrace();
			relatorioCpf.setStatusRetorno(1);
			relatorioCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");			
		}
		return relatorioCpf;
	}	
	
}
