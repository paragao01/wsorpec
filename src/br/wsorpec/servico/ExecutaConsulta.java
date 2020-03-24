package br.wsorpec.servico;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.Gson;

import br.wsorpec.acessoBanco.Conexao;
import br.wsorpec.cache.Cache;
import br.wsorpec.crediOnline.relatorioCNPJ.CrediOnLineRelatorioCNPJ;
import br.wsorpec.log.Log;
import br.wsorpec.util.Formatador;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.pessoalGold.SPCAXML;

public class ExecutaConsulta extends Conexao{

	private static Log log;
	private static Cache cache = new Cache();
	private static String protocolo;
	private static String resposta;
	private static String solicitacao;
	private static String codigo;
	private static String senha;
	private static String produto;
	private static String produtoFornecedor;
	private static String fornecedor;
	private static String ipRemoto;
	
	public ExecutaConsulta() {}
	
	public String executar(String xml) {
		
		if(!verificacaXML(xml)) {
			xml = "Problemas na estrutura do xml";
			return xml;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		ipRemoto = Util.ipRemoto();
		protocolo = Util.geraProtocolo();
		solicitacao = xml;
		
		if (!verificaCache()) { // Se nao foi respondido pelo cache da procedimento.			
			log.registro(protocolo, "SND", xml);
			
			//Fazer logon - verificar codigo, senha e se o produto pode ser consultado.
			String strLogon = logon();
			if(!strLogon.equals("OK")) {
				cache.cacheResposta(strLogon, protocolo);
				log.registro(protocolo, "RCV", strLogon);
				return strLogon;
			}
			
			//Fazer a consulta nos BUREAU 
			if(fornecedor.equals("1")) {
				consultaCDLRio();
				if(lerTag("<STATUS-RESPOSTA>", resposta).equals("0")) {
					//bilhetagem();
				}				
			}else if(fornecedor.equals("2")) {
				consultaCrediOline();
				if(lerTag("<STATUS-RESPOSTA>", resposta).equals("0")) {
					//bilhetagem();
				}				
			}else {
				return "";
			}
			//resposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SPCA-XML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://www.scpc.inf.br/spcn/spcaxml.xsd\">    <VERSAO>20151030</VERSAO>    <SOLICITACAO>        <S-CODIGO>23960</S-CODIGO>        <S-SENHA>83690</S-SENHA>        <S-CONSULTA>309</S-CONSULTA>        <S-SOLICITANTE>RJ001</S-SOLICITANTE>        <S-CPF>74071785772</S-CPF>        <S-DDD-1>21</S-DDD-1>        <S-TELEFONE-1>38136109</S-TELEFONE-1>        <S-TIPO-CREDITO>OU</S-TIPO-CREDITO>        <S-TPUF>SP</S-TPUF>    </SOLICITACAO>    <RESPOSTA>        <RESPOSTA-RETORNO>            <STATUS-RESPOSTA>0</STATUS-RESPOSTA>            <MENSAGEM-RESPOSTA>CONSULTA CONCLUIDA</MENSAGEM-RESPOSTA>        </RESPOSTA-RETORNO>        <NUMERO-RESPOSTA>267870332-8</NUMERO-RESPOSTA>        <ASSOCIADO-SOLICITANTE>ORPEC</ASSOCIADO-SOLICITANTE>        <NOME-CONSULTA>Acerta</NOME-CONSULTA>        <IP-SOLICITANTE>179.218.26.208</IP-SOLICITANTE>        <REGISTRO-ACSP-SPCA>            <SPCA-221-SINTESE>                <SPCA-221-NOME>PAULO CESAR PIRES DE ARAGAO</SPCA-221-NOME>                <SPCA-221-CPF>74071785772</SPCA-221-CPF>                <SPCA-221-NASCIMENTO>19640228</SPCA-221-NASCIMENTO>                <SPCA-221-MAE>CLEUZA MAGALHAES PIRES DE ARAGAO</SPCA-221-MAE>                <SPCA-221-TIT-ELEITOR>11090541953</SPCA-221-TIT-ELEITOR>            </SPCA-221-SINTESE>            <SPCA-111-CONSULTA>                <SPCA-111-RESUMO>                    <SPCA-111-TOTAL>3</SPCA-111-TOTAL>                    <SPCA-111-DATA-1>                        <SPCA-111-ANO-1>2018</SPCA-111-ANO-1>                        <SPCA-111-MES-1>5</SPCA-111-MES-1>                        <SPCA-111-QTD-1>3</SPCA-111-QTD-1>                    </SPCA-111-DATA-1>                    <SPCA-111-DATA-2>                        <SPCA-111-ANO-2>2018</SPCA-111-ANO-2>                        <SPCA-111-MES-2>4</SPCA-111-MES-2>                        <SPCA-111-QTD-2>0</SPCA-111-QTD-2>                    </SPCA-111-DATA-2>                    <SPCA-111-DATA-3>                        <SPCA-111-ANO-3>2018</SPCA-111-ANO-3>                        <SPCA-111-MES-3>3</SPCA-111-MES-3>                        <SPCA-111-QTD-3>0</SPCA-111-QTD-3>                    </SPCA-111-DATA-3>                    <SPCA-111-DATA-4>                        <SPCA-111-ANO-4>2018</SPCA-111-ANO-4>                        <SPCA-111-MES-4>2</SPCA-111-MES-4>                        <SPCA-111-QTD-4>0</SPCA-111-QTD-4>                    </SPCA-111-DATA-4>                </SPCA-111-RESUMO>            </SPCA-111-CONSULTA>            <SPCA-126-CONSULTA>                <SPCA-126-TIPO>OU</SPCA-126-TIPO>                <SPCA-126-DESCRICAO>OUTROS</SPCA-126-DESCRICAO>                <SPCA-126-DATA>20180529</SPCA-126-DATA>                <SPCA-126-INFORMANTE>RJ-RJO/ORPEC</SPCA-126-INFORMANTE>            </SPCA-126-CONSULTA>            <SPCA-126-CONSULTA>                <SPCA-126-TIPO>OU</SPCA-126-TIPO>                <SPCA-126-DESCRICAO>OUTROS</SPCA-126-DESCRICAO>                <SPCA-126-DATA>20180528</SPCA-126-DATA>                <SPCA-126-INFORMANTE>RJ-RJO/ORPEC</SPCA-126-INFORMANTE>            </SPCA-126-CONSULTA>            <SPCA-126-CONSULTA>                <SPCA-126-TIPO>OU</SPCA-126-TIPO>                <SPCA-126-DESCRICAO>OUTROS</SPCA-126-DESCRICAO>                <SPCA-126-DATA>20180522</SPCA-126-DATA>                <SPCA-126-INFORMANTE>RJ-RJO/ORPEC</SPCA-126-INFORMANTE>            </SPCA-126-CONSULTA>            <SPCA-223-TELEFONE>                <SPCA-223-CONFIRMACAO>                    <SPCA-223-DDD>21</SPCA-223-DDD>                    <SPCA-223-FONE>38136109</SPCA-223-FONE>                    <SPCA-223-ENDERECO>R GOMES CARNEIRO 124 AP 402</SPCA-223-ENDERECO>                    <SPCA-223-BAIRRO>IPANEMA</SPCA-223-BAIRRO>                    <SPCA-223-CEP>22071110</SPCA-223-CEP>                    <SPCA-223-CIDADE>RIO DE JANEIRO</SPCA-223-CIDADE>                    <SPCA-223-UF>RJ</SPCA-223-UF>                </SPCA-223-CONFIRMACAO>            </SPCA-223-TELEFONE>            <SPCA-601-SCORE-CRED>                <SPCA-601-PESSOA>FISICA</SPCA-601-PESSOA>                <SPCA-601-SCORE>0733</SPCA-601-SCORE>                <SPCA-601-EXECUCAO>NAO</SPCA-601-EXECUCAO>                <SPCA-601-DESCRICAO>                    <SPCA-601-D-SCORE>09-RISCO 12 MESES</SPCA-601-D-SCORE>                </SPCA-601-DESCRICAO>                <SPCA-601-CLASSIFICA>                    <SPCA-601-C-NUMERICO>1</SPCA-601-C-NUMERICO>                    <SPCA-601-C-ALFABETICO>A</SPCA-601-C-ALFABETICO>                </SPCA-601-CLASSIFICA>                <SPCA-601-PROVAVEL>00470</SPCA-601-PROVAVEL>                <SPCA-601-TEXTO>DE CADA 100 PESSOAS CLASSIFICADAS NESTA CLASSE DE SCORE, E PROVAVEL QUE 5 APRESENTEM DEBITOS NO MERCADO NOS PROXIMOS 12 MESES.</SPCA-601-TEXTO>            </SPCA-601-SCORE-CRED>        </REGISTRO-ACSP-SPCA>    </RESPOSTA></SPCA-XML>";
						
			cache.cacheResposta(resposta, protocolo);
			log.registro(protocolo, "RCV", resposta);
		}
		return resposta;
	}

	public void bilhetagem() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer();
		String doc = "";
		int i = 0;
		try {
			doc = lerTag("<S-CPF>", resposta);
			if(doc.equals("")) {
				doc = lerTag("<S-CNPJ>", resposta);
			}
			doc = Formatador.unformatString(doc);
			
			conn = this.getConnection();

			sql.append("INSERT INTO Bilhetagem																  \n");
			sql.append("(idBilhetagemPai, idCliente, idProduto, DataConsulta, Documento, Bilhetar, IPCliente) \n");
			sql.append("VALUES(NULL, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?); 									  \n");

			ps = conn.prepareStatement(sql.toString());

			this.setObject(ps, Types.INTEGER, ++i, Integer.parseInt(codigo));
			this.setObject(ps, Types.INTEGER, ++i, Integer.parseInt(produto));
			this.setObject(ps, Types.VARCHAR, ++i, doc);			
			this.setObject(ps, Types.INTEGER, ++i, 1);
			this.setObject(ps, Types.VARCHAR, ++i, ipRemoto);
			
			ps.execute();

		} catch (Exception e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
	}	

	public String consultaCDLRio() {
		URL url;
		HttpURLConnection con;
		InputStream is = null;
		BufferedReader br = null;
		SPCAXML obj = new SPCAXML();
		int HTTP_COD_SUCESSO = 200;

		solicitacao = gravaTag("<S-CODIGO>", GlobalConstants.USRCDLRIO, solicitacao);
		solicitacao = gravaTag("<S-SENHA>", GlobalConstants.PWRCDLRIO, solicitacao);
		solicitacao = gravaTag("<S-CONSULTA>", produtoFornecedor, solicitacao);
		
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
				resposta = "HTTP error code : " + con.getResponseCode();
				return resposta;
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

			System.out.println(obj.getRESPOSTA().getIPSOLICITANTE());
			//System.out.println(obj.getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().getSPCA126DESCRICAO());
			//System.out.println(obj.getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().getSPCA126INFORMANTE());
			//System.out.println(obj.getRESPOSTA().getREGISTROACSPSPCA().getSPCA126CONSULTA().getSPCA126TIPO());
			
			
			is.close();
			br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} catch (IOException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} catch (JAXBException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		}
		
		return resposta;
	}	
	
	public String consultaCrediOline() {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		int HTTP_COD_SUCESSO = 200;
		String documento = "";

		try {			
			documento = ExecutaConsulta.lerTag("<CPF>", solicitacao);
			
			if(!documento.equals("")) {
				solicitacao = "relatorio/cpf/"+documento+"?email="+GlobalConstants.EMAILCRED+"&token="+GlobalConstants.TOKENCRED;
				documento = "cpf";
			}else {
				documento = ExecutaConsulta.lerTag("<CNPJ>", solicitacao);
				solicitacao = "relatorio/cnpj/"+documento+"?email="+GlobalConstants.EMAILCRED+"&token="+GlobalConstants.TOKENCRED;
				documento = "cnpj";				
			}	

			url = new URL(GlobalConstants.URLCRED+solicitacao);
			con = (HttpURLConnection) url.openConnection();
			
			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				log.registro(protocolo, "ERRO", "HTTP error code : " + con.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resposta += line.trim();
			}

			br.close();
			con.disconnect();
			
			Gson gson = new Gson();			
			if(!documento.equals("cpf")) {
				CrediOnLineRelatorioCNPJ obj = gson.fromJson(resposta, CrediOnLineRelatorioCNPJ.class);
				System.out.println(obj.getFantasia());
			}else{
				
			}
		} catch (MalformedURLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} catch (IOException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		}
		return resposta;

	}
	
	public String logon() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer();
		String resp = "";
		boolean pro = false;
		boolean result = false;
		try {
			senha = Util.converteMD5(senha, protocolo, log);

			conn = this.getConnection();
			
			sql.append("SELECT Cliente.idCliente, Produto.idProduto, Cliente.Senha,											\n");
			sql.append("       Produto.codigoProdutoFornecedor, Produto.fornecedor											\n");
			sql.append("  FROM Produto 																						\n"); 
			sql.append(" INNER JOIN ProdutoPlano ON Produto.idProduto = ProdutoPlano.idProduto								\n");
			sql.append(" INNER JOIN ClienteProdutoPlano ON ProdutoPlano.idProdutoPlano = ClienteProdutoPlano.idProdutoPlano \n");
			sql.append(" INNER JOIN Cliente ON ClienteProdutoPlano.idCliente = Cliente.idCliente							\n");
			sql.append(" WHERE Cliente.CodigoCliente = ? 																	\n"); 
			sql.append("   AND Cliente.Senha = ?																			\n");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, codigo);
			ps.setString(2, senha);

			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
				if(rs.getString("idProduto").equals(produto)) {
					produtoFornecedor = rs.getString("codigoProdutoFornecedor");
					fornecedor = rs.getString("fornecedor");
					pro = true;
					break;
				}	
			}
			if (result) {
				if(pro) {
					resp = "OK";
				}else{
					resp = "Produto nao autorizado para esse usuario";
				}
			}else{
				resp = "Codigo ou senha invalido(s)";
			}

			return resp;
		} catch (Exception e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
		return resp;
	}	
	
	public static boolean verificaCache() {
		boolean isCache = false;
				
		resposta = cache.cacheConsulta(solicitacao, protocolo, log); // Verifica cache
		
		if (resposta.trim().length() > 0) { // verifica se o cache respondeu
			isCache = true;
		}
		
		return isCache;
	}
	
	public static boolean verificacaXML(String xml) {
		boolean ret = true;
		codigo  = lerTag("<S-CODIGO>", xml);
		senha   = lerTag("<S-SENHA>", xml);
		produto = lerTag("<S-CONSULTA>", xml);
		
		if(Util.Empty(codigo) || Util.Empty(senha) || Util.Empty(produto)) {
			ret = false;
		}
		return ret;
	}
	
	public static String lerTag(String tag, String xml) {
		String str = "";
		String tagFin = "";
		int posIni = 0;
		int posFin = 0;
		posIni = xml.indexOf(tag);
		if(posIni > 0) {
			tagFin = "</"+tag.substring(1, tag.length());
			posFin = xml.indexOf(tagFin);
			if(posFin > 0) {
				str = xml.substring(posIni+tag.length(), posFin);
			}
		}
		return str;
	}
	
	public static String gravaTag(String tag, String valorTag, String xml) {
		String tagFin = "";
		String str = "";
		int posIni = 0;
		int posFin = 0;
		posIni = xml.indexOf(tag);
		if(posIni > 0) {
			tagFin = "</"+tag.substring(1, tag.length());
			posFin = xml.indexOf(tagFin);
			if(posFin > 0) {
				str = tag+xml.substring(posIni+tag.length(), posFin)+tagFin;
				valorTag = tag+valorTag+tagFin;
				xml = xml.replace(str, valorTag);
			}
		}
		return xml;
	}
	
}
