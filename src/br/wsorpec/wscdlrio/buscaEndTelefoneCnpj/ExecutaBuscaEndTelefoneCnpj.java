package br.wsorpec.wscdlrio.buscaEndTelefoneCnpj;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.cache.Cache;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.buscaEndTelefoneCnpj.SPCAXML;

public class ExecutaBuscaEndTelefoneCnpj {

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaBuscaEndTelefoneCnpj() {}
	
	public BuscaEndTelefoneCnpj executar(BuscaEndTelefoneCnpj buscaCnpj, boolean xml) {
		
		if (!critica(buscaCnpj)) {
			buscaCnpj.setSenha("");
			return buscaCnpj;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		buscaCnpj.setSenha(Util.converteMD5(buscaCnpj.getSenha(), protocolo, log));
		
		String ipRemoto    = GlobalConstants.IPREMOTO;
		boolean isCache    = false;
		String codigo      = buscaCnpj.getCodigo();
        String senha       = buscaCnpj.getSenha();
		String produto     = "71";
		String solicitacao = "Produto="+produto+";Codigo="+buscaCnpj.getCodigo()+";Senha="+buscaCnpj.getSenha()+";CNPJ="+buscaCnpj.getCnpj();
		
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
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno(strLogon);				
				return buscaCnpj;
			}
						
			//Fazer a consulta nos BUREAU 			
			buscaCnpj = consultaCDLRio(buscaCnpj);	
			
			if(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, buscaCnpj.getCnpj(), produto, ipRemoto, protocolo);
			}
									
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			buscaCnpj.setStatusRetorno(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			buscaCnpj.setMensagemRetorno(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			//Limpo as tag <S-CODIGO>, <S-SENHA>, <S-SOLICITANTE> e <ASSOCIADO-SOLICITANTE> 
			resposta = Util.gravaTag("<S-CODIGO>", "0", resposta);
			resposta = Util.gravaTag("<S-SENHA>", "0", resposta);
			resposta = Util.gravaTag("<S-SOLICITANTE>", "", resposta);
			resposta = Util.gravaTag("<ASSOCIADO-SOLICITANTE>", "", resposta);
						
			//Retiro informacoes da tag <SPCA-XML>
			resposta = Util.limpaXML(resposta);
			
			if(xml){buscaCnpj.setXml(resposta);}// Quando a resposta retorna só o xml
			cache.cacheResposta(resposta, protocolo);
			log.registro(protocolo, "RCV", resposta);
		}else{// Se foi respondido pelo cache
			InputStream is = null;
			BufferedReader br = null;
			SPCAXML obj = new SPCAXML();
			try {
				is = new ByteArrayInputStream(resposta.getBytes());
				br = new BufferedReader(new InputStreamReader(is));

				// Faco o parse aqui xml->classe java
				JAXBContext jaxbContext = JAXBContext.newInstance(SPCAXML.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				obj = (SPCAXML) jaxbUnmarshaller.unmarshal(br);

				buscaCnpj.setSpcaxml(obj);
				if (xml) {
					buscaCnpj.setXml(resposta);
				} // Quando a resposta retorna só o xml

				// Atualizo a mensagem que veio do CDLRio para o WSOrpec
				buscaCnpj.setStatusRetorno(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
				buscaCnpj.setMensagemRetorno(buscaCnpj.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());

				is.close();
				br.close();

			} catch (JAXBException e) {
				e.printStackTrace();
				log.registro(protocolo, "ERRO", e.getMessage());
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno("Erro no parse do cache para o objeto");
			} catch (IOException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno("Erro no parse do cache para o objeto");
			}
		}
		return buscaCnpj;
	}

	public boolean critica(BuscaEndTelefoneCnpj buscaCnpj){
		
		if(Util.Empty(buscaCnpj.getCodigo())){
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(buscaCnpj.getCodigo())){
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(buscaCnpj.getSenha())){
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(buscaCnpj.getCnpj())) {
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("CNPJ nao informado");				
			return false;			
		}else {
			if (!Util.IsCNPJ(buscaCnpj.getCnpj())) {
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno("CNPJ invalido");				
				return false;
			}
		}
		return true;
	}
	
	public BuscaEndTelefoneCnpj consultaCDLRio(BuscaEndTelefoneCnpj buscaCnpj) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		SPCAXML obj = new SPCAXML();
		String solicitacao = "";
		int HTTP_COD_SUCESSO = 200;
		
		solicitacao = "<SPCA-XML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.scpc.inf.br/spcn/spcaxmlefx.xsd\">" + 
				  "	<VERSAO>20151030</VERSAO>" + 
				  "	<SOLICITACAO>" + 
				  "		<S-CODIGO>"+GlobalConstants.USRCDLRIO+"</S-CODIGO>" + 
				  "		<S-SENHA>"+GlobalConstants.PWRCDLRIO+"</S-SENHA>" + 
				  "		<S-CONSULTA>624</S-CONSULTA>" + 
				  "		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				  "		<S-CNPJ>"+buscaCnpj.getCnpj()+"</S-CNPJ>"+
				  "		<S-NUMERO-RESPOSTA>0</S-NUMERO-RESPOSTA>" +				  
				  "		<S-DEFINE-PRODUTO>" + 
				  "		</S-DEFINE-PRODUTO>" + 
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

			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(solicitacao);
			writer.flush();
			writer.close();

			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				log.registro(protocolo, "ERRO", "HTTP error code : " + con.getResponseCode());
				buscaCnpj.setStatusRetorno((byte) 1);
				buscaCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");				
				return buscaCnpj;
			}

			//Aqui o encolde eh ISO-8859-1
			byte[] imageBytes = IOUtils.toByteArray(con.getInputStream());
			resposta = new String(imageBytes, "ISO-8859-1");
			resposta = resposta.trim().replace("\n", "");
			resposta = resposta.trim().replace("    ", "");
			br = new BufferedReader(new StringReader(resposta));
			
			//Faco o parse aqui xml->classe java				
			JAXBContext jaxbContext = JAXBContext.newInstance(SPCAXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			obj = (SPCAXML) jaxbUnmarshaller.unmarshal(br);

			buscaCnpj.setSpcaxml(obj);

			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (JAXBException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			buscaCnpj.setStatusRetorno((byte) 1);
			buscaCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");						
		}
		return buscaCnpj;
	}	
		
	
}
