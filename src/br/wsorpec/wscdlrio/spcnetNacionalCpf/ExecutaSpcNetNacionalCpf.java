package br.wsorpec.wscdlrio.spcnetNacionalCpf;

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

public class ExecutaSpcNetNacionalCpf {

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaSpcNetNacionalCpf() {}
	
	public SpcNetNacionalCpf executar(SpcNetNacionalCpf spcnetNacCpf, boolean xml) {
		
		if (!critica(spcnetNacCpf)) {
			spcnetNacCpf.setSenha("");
			return spcnetNacCpf;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		spcnetNacCpf.setSenha(Util.converteMD5(spcnetNacCpf.getSenha(), protocolo, log));
		
		String ipRemoto    = GlobalConstants.IPREMOTO;
		boolean isCache    = false;
		String codigo      = spcnetNacCpf.getCodigo();
        String senha       = spcnetNacCpf.getSenha();
		String produto     = "24";
		String solicitacao = "Produto="+produto+";Codigo="+spcnetNacCpf.getCodigo()+";Senha="+spcnetNacCpf.getSenha()+";CPF="+spcnetNacCpf.getCpf();
				
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
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno(strLogon);				
				return spcnetNacCpf;
			}
						
			//Fazer a consulta nos BUREAU 			
			spcnetNacCpf = consultaCDLRio(spcnetNacCpf);	
			
			if(spcnetNacCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, spcnetNacCpf.getCpf(), produto, ipRemoto, protocolo);
			}
			
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			spcnetNacCpf.setStatusRetorno(spcnetNacCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			spcnetNacCpf.setMensagemRetorno(spcnetNacCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			//Limpo as tag <S-CODIGO>, <S-SENHA>, <S-SOLICITANTE> e <ASSOCIADO-SOLICITANTE> 
			resposta = Util.gravaTag("<S-CODIGO>", "0", resposta);
			resposta = Util.gravaTag("<S-SENHA>", "0", resposta);
			resposta = Util.gravaTag("<S-SOLICITANTE>", "", resposta);
			resposta = Util.gravaTag("<ASSOCIADO-SOLICITANTE>", "", resposta);
						
			//Retiro informacoes da tag <SPCA-XML>
			resposta = Util.limpaXML(resposta);
			
			if(xml){spcnetNacCpf.setXml(resposta);}// Quando a resposta retorna só o xml
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

			spcnetNacCpf.setSpcaxml(obj);
			if(xml){spcnetNacCpf.setXml(resposta);}// Quando a resposta retorna só o xml
			
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			spcnetNacCpf.setStatusRetorno(spcnetNacCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			spcnetNacCpf.setMensagemRetorno(spcnetNacCpf.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			is.close();
			br.close();
			
			} catch (JAXBException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno("Erro no parse do cache para o objeto");						
			} catch (IOException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno("Erro no parse do cache para o objeto");						
			}
		}
		return spcnetNacCpf;
	}

	public boolean critica(SpcNetNacionalCpf spcnetNacCpf){
		
		if(Util.Empty(spcnetNacCpf.getCodigo())){
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(spcnetNacCpf.getCodigo())){
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(spcnetNacCpf.getSenha())){
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		
		if (Util.Empty(spcnetNacCpf.getCpf())) {
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("CPF nao informado");				
			return false;			
		}else {
			if (!Util.IsCPF(spcnetNacCpf.getCpf())) {
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno("CPF invalido");				
				return false;
			}
		}
		return true;
	}
	
	public SpcNetNacionalCpf consultaCDLRio(SpcNetNacionalCpf spcnetNacCpf) {
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
				  "		<S-CONSULTA>296</S-CONSULTA>" + 
				  "		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				  "		<S-CPF>"+spcnetNacCpf.getCpf()+"</S-CPF>" + 
				  "		<S-TPUF></S-TPUF>" +
				  "		<S-SPCNET-PRODUTO>" + 
				  "			<S-SPCNET-CONSULTA>F1</S-SPCNET-CONSULTA>" + 
				  "		</S-SPCNET-PRODUTO>" + 
				  "		<S-TIPO-CREDITO>XX</S-TIPO-CREDITO>" +				  
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
				spcnetNacCpf.setStatusRetorno((byte) 1);
				spcnetNacCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");				
				return spcnetNacCpf;
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

			spcnetNacCpf.setSpcaxml(obj);

			is.close();
			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (JAXBException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCpf.setStatusRetorno((byte) 1);
			spcnetNacCpf.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");						
		}
		return spcnetNacCpf;
	}	
		
	
}
