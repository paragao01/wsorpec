package br.wsorpec.wscdlrio.spcnetNacionalCnpj;

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

public class ExecutaSpcNetNacionalCnpj {

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaSpcNetNacionalCnpj() {}
	
	public SpcNetNacionalCnpj executar(SpcNetNacionalCnpj spcnetNacCnpj, boolean xml) {
		
		if (!critica(spcnetNacCnpj)) {
			spcnetNacCnpj.setSenha("");
			return spcnetNacCnpj;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		spcnetNacCnpj.setSenha(Util.converteMD5(spcnetNacCnpj.getSenha(), protocolo, log));
		
		String ipRemoto    = GlobalConstants.IPREMOTO;
		boolean isCache    = false;
		String codigo      = spcnetNacCnpj.getCodigo();
        String senha       = spcnetNacCnpj.getSenha();
		String produto     = "47";
		String solicitacao = "Produto="+produto+";Codigo="+spcnetNacCnpj.getCodigo()+";Senha="+spcnetNacCnpj.getSenha()+";CNPJ="+spcnetNacCnpj.getCnpj();
				
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
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno(strLogon);				
				return spcnetNacCnpj;
			}
						
			//Fazer a consulta nos BUREAU 			
			spcnetNacCnpj = consultaCDLRio(spcnetNacCnpj);	
			
			if(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, spcnetNacCnpj.getCnpj(), produto, ipRemoto, protocolo);
			}
									
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			spcnetNacCnpj.setStatusRetorno(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			spcnetNacCnpj.setMensagemRetorno(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			//Limpo as tag <S-CODIGO>, <S-SENHA>, <S-SOLICITANTE> e <ASSOCIADO-SOLICITANTE> 
			resposta = Util.gravaTag("<S-CODIGO>", "0", resposta);
			resposta = Util.gravaTag("<S-SENHA>", "0", resposta);
			resposta = Util.gravaTag("<S-SOLICITANTE>", "", resposta);
			resposta = Util.gravaTag("<ASSOCIADO-SOLICITANTE>", "", resposta);
						
			//Retiro informacoes da tag <SPCA-XML>
			resposta = Util.limpaXML(resposta);
			
			if(xml){spcnetNacCnpj.setXml(resposta);}// Quando a resposta retorna só o xml
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

			spcnetNacCnpj.setSpcaxml(obj);
			if(xml){spcnetNacCnpj.setXml(resposta);}// Quando a resposta retorna só o xml
			
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			spcnetNacCnpj.setStatusRetorno(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			spcnetNacCnpj.setMensagemRetorno(spcnetNacCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			is.close();
			br.close();
			
			} catch (JAXBException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno("Erro no parse do cache para o objeto");						
			} catch (IOException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno("Erro no parse do cache para o objeto");						
			}
		}
		return spcnetNacCnpj;
	}

	public boolean critica(SpcNetNacionalCnpj spcnetNacCnpj){
		
		if(Util.Empty(spcnetNacCnpj.getCodigo())){
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(spcnetNacCnpj.getCodigo())){
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(spcnetNacCnpj.getSenha())){
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(spcnetNacCnpj.getCnpj())) {
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("CNPJ nao informado");				
			return false;			
		}else {
			if (!Util.IsCNPJ(spcnetNacCnpj.getCnpj())) {
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno("CNPJ invalido");				
				return false;
			}
		}
		return true;
	}
	
	public SpcNetNacionalCnpj consultaCDLRio(SpcNetNacionalCnpj spcnetNacCnpj) {
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
				  "		<S-CONSULTA>298</S-CONSULTA>" + 
				  "		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				  "		<S-CNPJ>"+spcnetNacCnpj.getCnpj()+"</S-CNPJ>" + 
				  "		<S-TPUF></S-TPUF>" +				  
				  "		<S-SPCNET-PRODUTO>" + 
				  "			<S-SPCNET-CONSULTA>J1</S-SPCNET-CONSULTA>" + 
				  "		</S-SPCNET-PRODUTO>" + 
				  "		<S-TIPO-CREDITO>FI</S-TIPO-CREDITO>" +				  
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
				spcnetNacCnpj.setStatusRetorno((byte) 1);
				spcnetNacCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");				
				return spcnetNacCnpj;
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

			spcnetNacCnpj.setSpcaxml(obj);

			is.close();
			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (JAXBException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			spcnetNacCnpj.setStatusRetorno((byte) 1);
			spcnetNacCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");						
		}
		return spcnetNacCnpj;
	}	
		
	
}
