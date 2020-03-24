package br.wsorpec.wscdlrio.empresarialGold;

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

public class ExecutaEmpresarialGold {

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	

	public ExecutaEmpresarialGold() {}
	
	public EmpresarialGold executar(EmpresarialGold empGold, boolean xml) {
		
		if (!critica(empGold)) {
			empGold.setSenha("");
			return empGold;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		resposta = "";
		
		//Criptografa a senha;
		empGold.setSenha(Util.converteMD5(empGold.getSenha(), protocolo, log));
		
		String ipRemoto    = GlobalConstants.IPREMOTO;
		boolean isCache    = false;
		String codigo      = empGold.getCodigo();
        String senha       = empGold.getSenha();
		String produto     = "52";
		String solicitacao = "Produto="+produto+
							 ";Codigo="+empGold.getCodigo()+
							 ";Senha="+empGold.getSenha()+
							 ";CNPJ="+empGold.getCnpj()+
							 ";QuadroSocial="+empGold.getQuadroSocial()+
							 ";QuadroSocialRestricao="+empGold.getQuadroSocialRestricao()+
							 ";Participacoes="+empGold.getParticipacoes()+ 
							 ";ParticipacoesRestricao="+empGold.getParticipacoesRestricao()+
							 ";Decisao="+empGold.getDecisao()+ 
							 ";FaturamentoPresumido="+empGold.getFaturamentoPresumido()+ 
							 ";LimiteCredito="+empGold.getLimiteCredito()+ 
							 ";Anvisa="+empGold.getAnvisa()+ 
							 ";EmpresaMesmoEndereco="+empGold.getEmpresaMesmoEndereco()+ 
							 ";Cheque="+empGold.getFolhaCheque();
		
		if(empGold.getFolhaCheque().equals("S")) {
			solicitacao += ";Banco="+empGold.getBanco()+
						   ";Agencia="+empGold.getAgencia()+
						   ";NumConta="+empGold.getNumConta()+
						   ";DvConta="+empGold.getDvConta()+
						   ";NumCheque="+empGold.getNumCheque()+
						   ";DvCheque="+empGold.getDvCheque()+
						   ";CMC7="+empGold.getCmc7()+
						   ";ChequeOrigem="+empGold.getChequeOrigem()+
						   ";QtdeCheque="+empGold.getQtdeCheque()+
						   ";DataCheque"+empGold.getDataCheque(); 
		}
				
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
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno(strLogon);				
				return empGold;
			}
						
			//Fazer a consulta nos BUREAU 			
			empGold = consultaCDLRio(empGold);	
			
			if(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA()==0) {
				//Fazer a bilhetagem					
				Bilhetagem bilhetar = new Bilhetagem();
				bilhetar.bilhetar(GlobalConstants.idCliente, empGold.getCnpj(), produto, ipRemoto, protocolo);
				
				//Fazer bilhetagem opcionais
				if(empGold.getQuadroSocial().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "61", ipRemoto, protocolo);
				}
				if(empGold.getQuadroSocialRestricao().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "62", ipRemoto, protocolo);
				}
				if(empGold.getParticipacoes().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "63", ipRemoto, protocolo);
				}
				if(empGold.getParticipacoesRestricao().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "64", ipRemoto, protocolo);
				}
				if(empGold.getDecisao().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "65", ipRemoto, protocolo);
				}
				if(empGold.getFaturamentoPresumido().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "66", ipRemoto, protocolo);
				}
				if(empGold.getLimiteCredito().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "67", ipRemoto, protocolo);
				}
				if(empGold.getAnvisa().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "68", ipRemoto, protocolo);
				}
				if(empGold.getEmpresaMesmoEndereco().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "69", ipRemoto, protocolo);
				}
				if(empGold.getFolhaCheque().equals("S")) {
					bilhetar.bilhetarOpcionais(GlobalConstants.idPai, GlobalConstants.idCliente, empGold.getCnpj(), "70", ipRemoto, protocolo);
				}
			}
									
			//Atualizo a mensagem que veio do CDLRio para o WSOrpec
			empGold.setStatusRetorno(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
			empGold.setMensagemRetorno(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
			
			//Limpo as tag <S-CODIGO>, <S-SENHA>, <S-SOLICITANTE> e <ASSOCIADO-SOLICITANTE> 
			resposta = Util.gravaTag("<S-CODIGO>", "0", resposta);
			resposta = Util.gravaTag("<S-SENHA>", "0", resposta);
			resposta = Util.gravaTag("<S-SOLICITANTE>", "", resposta);
			resposta = Util.gravaTag("<ASSOCIADO-SOLICITANTE>", "", resposta);
						
			//Retiro informacoes da tag <SPCA-XML>
			resposta = Util.limpaXML(resposta);
			
			if(xml){empGold.setXml(resposta);}// Quando a resposta retorna só o xml
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

				empGold.setSpcaxml(obj);
				if (xml) {
					empGold.setXml(resposta);
				} // Quando a resposta retorna só o xml

				// Atualizo a mensagem que veio do CDLRio para o WSOrpec
				empGold.setStatusRetorno(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
				empGold.setMensagemRetorno(empGold.getSpcaxml().getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());

				is.close();
				br.close();
			} catch (JAXBException e) {
				e.printStackTrace();
				log.registro(protocolo, "ERRO", e.getMessage());
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("Erro no parse do cache para o objeto");
			} catch (IOException e) {
				log.registro(protocolo, "ERRO", e.getMessage());
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("Erro no parse do cache para o objeto");
			}
		}
		return empGold;
	}

	public boolean critica(EmpresarialGold empGold){
		
		if(Util.Empty(empGold.getCodigo())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(empGold.getCodigo())){
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(empGold.getSenha())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(empGold.getCnpj())) {
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("CNPJ nao informado");				
			return false;			
		}else {
			if (!Util.IsCNPJ(empGold.getCnpj())) {
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("CNPJ invalido");				
				return false;
			}
		}
		if (Util.Empty(empGold.getQuadroSocial())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Quando Social esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getQuadroSocialRestricao())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Quando Social Restricao esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getParticipacoes())){ 
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Participacoes esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getParticipacoesRestricao())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Participacoes Restricao esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getDecisao())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Decisao esta em branco preencha S/N");				
			return false;
		}else if(Util.Empty(empGold.getFaturamentoPresumido())){ 
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Faturamento Presumido esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getLimiteCredito())){ 
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Limite Credito esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getAnvisa())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Anvisa esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getEmpresaMesmoEndereco())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Empresa Mesmo Endereco esta em branco preencha S/N");				
			return false;			
		}else if(Util.Empty(empGold.getFolhaCheque())){
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Parametro Cheque esta em branco preencha S/N");				
			return false;
		}
		if (empGold.getFolhaCheque().equals("S")) {
			if (Util.Empty(empGold.getChequeOrigem())) {
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("Cheque origem nao informado");
				return false;
			} else {
				if (empGold.getChequeOrigem().equals("C")) {
					if (Util.Empty(empGold.getCmc7())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("CMC7 nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getCmc7())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("CMC7 tem que ter so numero");
							return false;
						}
					}
				} else {
					if (Util.Empty(empGold.getBanco())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Banco nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getBanco())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("Banco tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getAgencia())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Agencia nao informada");
						return false;
					} else {
						if (!Util.isdigit(empGold.getAgencia())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("Agencia tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getNumConta())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Numero da conta nao informado");
						return false;
					}
					if (Util.Empty(empGold.getDvConta())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("DV da conta nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getDvConta())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("DV da conta tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getNumCheque())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Numero do cheque nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getNumCheque())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("Numero do cheque tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getDvCheque())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("DV do cheque nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getDvCheque())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("DV do cheque tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getQtdeCheque())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Quantidade de cheques nao informado");
						return false;
					} else {
						if (!Util.isdigit(empGold.getQtdeCheque())) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("Quantidade de cheques tem que ter so numero");
							return false;
						}
					}
					if (Util.Empty(empGold.getDataCheque())) {
						empGold.setStatusRetorno((byte) 1);
						empGold.setMensagemRetorno("Data do cheque nao informada");
						return false;
					} else {
						if (!Util.isDateValid(empGold.getDataCheque(), "ddMMyyyy")) {
							empGold.setStatusRetorno((byte) 1);
							empGold.setMensagemRetorno("Data do cheque invalida");
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public EmpresarialGold consultaCDLRio(EmpresarialGold empGold) {
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
				  "		<S-CONSULTA>623</S-CONSULTA>" + 
				  "		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				  "		<S-CNPJ>"+empGold.getCnpj()+"</S-CNPJ>";
		
		if(empGold.getFolhaCheque().equals("S")) {
			solicitacao += "		<S-BANCO>"+empGold.getBanco()+"</S-BANCO>" +
						   "        <S-AGENCIA>"+empGold.getAgencia()+"</S-AGENCIA>" +
						   "        <S-CONTA-CORRENTE>"+empGold.getNumConta()+"</S-CONTA-CORRENTE>"+
						   "        <S-CONTA-DIGITO>"+empGold.getDvConta()+"</S-CONTA-DIGITO>"+
						   "        <S-CHEQUE>"+empGold.getNumCheque()+"</S-CHEQUE>"+
						   "        <S-CHEQUE-DIGITO>"+empGold.getDvCheque()+"</S-CHEQUE-DIGITO>"+
						   "        <S-CMC7>"+empGold.getCmc7()+"</S-CMC7>"+
						   "        <S-CHEQUE-ORIGEM>"+empGold.getChequeOrigem()+"</S-CHEQUE-ORIGEM>"+
						   "        <S-CHEQUE-DATA>"+empGold.getDataCheque()+"</S-CHEQUE-DATA>"+
						   "        <S-CHEQUE-QTDE>"+empGold.getQtdeCheque()+"</S-CHEQUE-QTDE>";
		}
		solicitacao += "<S-NUMERO-RESPOSTA>0</S-NUMERO-RESPOSTA>" +				  
				  "		<S-DEFINE-PRODUTO>" + 
				  "			<S-DEF-QUADRO-SOCIAL>"+empGold.getQuadroSocial()+"</S-DEF-QUADRO-SOCIAL>" + 
				  "			<S-DEF-QUADRO-COMRESTRICAO>"+empGold.getQuadroSocialRestricao()+"</S-DEF-QUADRO-COMRESTRICAO>" + 
				  "			<S-DEF-PARTICIPACOES>"+empGold.getParticipacoes()+"</S-DEF-PARTICIPACOES>" + 
				  "			<S-DEF-PART-COMRESTRICAO>"+empGold.getParticipacoesRestricao()+"</S-DEF-PART-COMRESTRICAO>" +
				  "			<S-DEF-DECISAO>"+empGold.getDecisao()+"</S-DEF-DECISAO>" + 
				  "			<S-DEF-FAT-PRESUMIDO>"+empGold.getFaturamentoPresumido()+"</S-DEF-FAT-PRESUMIDO>" + 
				  "			<S-DEF-LIMITE-CREDITO>"+empGold.getLimiteCredito()+"</S-DEF-LIMITE-CREDITO>" + 
				  "			<S-DEF-ANVISA>"+empGold.getAnvisa()+"</S-DEF-ANVISA>" + 
				  "			<S-DEF-EMPR-MESMO-ENDERECO>"+empGold.getEmpresaMesmoEndereco()+"</S-DEF-EMPR-MESMO-ENDERECO>" + 
				  "			<S-DEF-CHEQUE>"+empGold.getFolhaCheque()+"</S-DEF-CHEQUE>" + 
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
			//con.setRequestProperty("Content-Type", "text/xml;charset=ISO-8859-1");

			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(solicitacao);
			writer.flush();
			writer.close();

			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				log.registro(protocolo, "ERRO", "HTTP error code : " + con.getResponseCode());
				empGold.setStatusRetorno((byte) 1);
				empGold.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");
				cache.apagaCache(protocolo);
				log.registro(protocolo.trim(), "INF", "Apaga cache, "+empGold.getMensagemRetorno());
				return empGold;
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

			empGold.setSpcaxml(obj);

			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (IOException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		} catch (JAXBException e) {
			cache.apagaCache(protocolo);
			log.registro(protocolo, "ERRO", e.getMessage());
			empGold.setStatusRetorno((byte) 1);
			empGold.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");						
		}
		return empGold;
	}	
		
	
}
