package br.wsorpec.wscdlrio.buscaEndTelefoneCpf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.cache.Cache;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class ExecutaBuscaEndTelefoneCpf {

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaBuscaEndTelefoneCpf() {}
	
	public BuscaEndTelefoneCpf executar(BuscaEndTelefoneCpf buscaCpf, Boolean xml) {
		
		if (!critica(buscaCpf)) {
			buscaCpf.setSenha("");
			return buscaCpf;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		buscaCpf.setSenha(Util.converteMD5(buscaCpf.getSenha(), protocolo, log));
		
		String ipRemoto    = GlobalConstants.IPREMOTO;
		boolean isCache    = false;
		String codigo      = buscaCpf.getCodigo();
        String senha       = buscaCpf.getSenha();
		String produto     = "53";
		String solicitacao = "Produto="+produto+";Codigo="+buscaCpf.getCodigo()+";Senha="+buscaCpf.getSenha()+";CPF="+buscaCpf.getCpf();
				
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
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno(strLogon);				
				return buscaCpf;
			}
						
			//Fazer a consulta nos BUREAU 			
			buscaCpf = consultaCDLRio(buscaCpf);	
			
			if(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA().equals("0")) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, buscaCpf.getCpf(), produto, ipRemoto, protocolo);
			}
			
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			buscaCpf.setStatusRetorno((byte) Integer.parseInt(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()));
			buscaCpf.setMensagemRetorno(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			//Limpo as tag <S-CODIGO>, <S-SENHA>, <S-SOLICITANTE> e <ASSOCIADO-SOLICITANTE> 
			resposta = Util.gravaTag("<S-CODIGO>", "0", resposta);
			resposta = Util.gravaTag("<S-SENHA>", "0", resposta);
			resposta = Util.gravaTag("<S-SOLICITANTE>", "", resposta);
			resposta = Util.gravaTag("<ASSOCIADO-SOLICITANTE>", "", resposta);
						
			//Retiro informacoes da tag <SPCA-XML>
			resposta = Util.limpaXML(resposta);
			
			if(xml){buscaCpf.setXml(resposta);}// Quando a resposta retorna só o xml
			cache.cacheResposta(resposta, protocolo);
			log.registro(protocolo, "RCV", resposta);
		}else{// Se foi respondido pelo cache
			InputStream is = null;
			BufferedReader br = null;
			SPCAXML obj = new SPCAXML();
			
			try {
			is = new ByteArrayInputStream(resposta.getBytes());
			br = new BufferedReader(new InputStreamReader(is));
			
			//Faco o parse aqui xml->classe java				
			JAXBContext jaxbContext = JAXBContext.newInstance(SPCAXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			obj = (SPCAXML) jaxbUnmarshaller.unmarshal(br);

			buscaCpf.setSpcaxml(obj);
			if(xml){buscaCpf.setXml(resposta);}// Quando a resposta retorna só o xml
			
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			buscaCpf.setStatusRetorno((byte) Integer.parseInt(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()));
			buscaCpf.setMensagemRetorno(buscaCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			is.close();
			br.close();
			
			} catch (JAXBException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno("Erro no parse do cache para o objeto");						
			} catch (IOException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno("Erro no parse do cache para o objeto");						
			}
		}
		return buscaCpf;
	}

	public boolean critica(BuscaEndTelefoneCpf buscaCpf){
		
		if(Util.Empty(buscaCpf.getCodigo())){
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(buscaCpf.getCodigo())){
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(buscaCpf.getSenha())){
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		
		if (Util.Empty(buscaCpf.getCpf())) {
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("CPF nao informado");				
			return false;			
		}else {
			if (!Util.IsCPF(buscaCpf.getCpf())) {
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno("CPF invalido");				
				return false;
			}
		}
		return true;
	}
	
	public BuscaEndTelefoneCpf consultaCDLRio(BuscaEndTelefoneCpf buscaCpf) {
		URL url;
		HttpURLConnection con;
		InputStream is = null;
		BufferedReader br = null;
		SPCAXML obj = new SPCAXML();
		String solicitacao = "";
		int HTTP_COD_SUCESSO = 200;
		
		solicitacao = "<SPCA-XML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.scpc.inf.br/spcn/spcaxmlefx.xsd\">" + 
				  "	<VERSAO>20151030</VERSAO>" + 
				  "	<SOLICITACAO>" + 
				  "		<S-CODIGO>"+GlobalConstants.USRCDLRIO+"</S-CODIGO>" + 
				  "		<S-SENHA>"+GlobalConstants.PWRCDLRIO+"</S-SENHA>" + 
				  "		<S-CONSULTA>333</S-CONSULTA>" + 
				  "		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				  "		<S-CPF>"+buscaCpf.getCpf()+"</S-CPF>" + 
				  "		<S-TIPO-CREDITO>XX</S-TIPO-CREDITO>" +
				  "		<S-ACERTA-PRODUTO>" + 
				  "			<S-ACERTA-SCORE>N</S-ACERTA-SCORE>" + 
				  "			<S-ACERTA-SCORE-12>N</S-ACERTA-SCORE-12>" + 
				  "			<S-ACERTA-SCORE-CC>N</S-ACERTA-SCORE-CC>" + 
				  "			<S-ACERTA-CHEQUE>N</S-ACERTA-CHEQUE>" + 
				  "		</S-ACERTA-PRODUTO>" + 
				  "	</SOLICITACAO>" + 
				  "</SPCA-XML>";
	
		try {
			url = new URL(GlobalConstants.URLCDLRIO);
			con = (HttpURLConnection) url.openConnection();
			// specify that we will send output and accept input
			con.setDoInput(true);
			con.setDoOutput(true);
			// con.setConnectTimeout(15000); //long timeout, but not infinite
			// con.setReadTimeout(15000);
			con.setUseCaches(false);
			con.setDefaultUseCaches(false);
			// Tell the web server what we are sending
			con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");

			// Write post data
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(solicitacao);
			writer.flush();
			writer.close();

			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				log.registro(protocolo, "ERRO", "HTTP error code : " + con.getResponseCode());
				buscaCpf.setStatusRetorno((byte) 1);
				buscaCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");				
				return buscaCpf;
			}

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resposta += line.trim();
			}
			
			is = new ByteArrayInputStream(resposta.getBytes());
			br = new BufferedReader(new InputStreamReader(is));
			
			//Faco o parse aqui xml->classe java				
			JAXBContext jaxbContext = JAXBContext.newInstance(SPCAXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			obj = (SPCAXML) jaxbUnmarshaller.unmarshal(br);

			buscaCpf.setSpcaxml(obj);

			is.close();
			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (JAXBException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCpf.setStatusRetorno((byte) 1);
			buscaCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");						
		}
		return buscaCpf;
	}	
		
	
}
