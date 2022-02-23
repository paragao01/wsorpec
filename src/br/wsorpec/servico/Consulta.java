package br.wsorpec.servico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import br.wsorpec.crediOnline.relatorioCNPJ.CrediOnLineRelatorioCNPJ;
import br.wsorpec.crediOnline.relatorioCNPJ.CrediOnLineRestritivoCNPJ;
import br.wsorpec.crediOnline.relatorioCNPJ.RelatorioCNPJ;
import br.wsorpec.crediOnline.relatorioCPF.CrediOnLineChequeCPF;
import br.wsorpec.crediOnline.relatorioCPF.CrediOnLineRelatorioCPF;
import br.wsorpec.crediOnline.relatorioCPF.CrediOnLineRestritivoCPF;
import br.wsorpec.fornecedor.Fornecedores;
import br.wsorpec.servico.ExecutaConsulta;
import br.wsorpec.util.GlobalConstants;

public class Consulta {
	
	
	public static void main(String[] args) throws IOException {
		//consultaCdlRio("");
		
		System.out.println(getDateTime());
		//8493442000129
		System.out.println("Primeira  : "+consultaCrediOnline("12312312387", "restritivos/cpf/", true, 1));		
		System.out.println("Segundo   : "+consultaCrediOnline("12312312387", "relatorio/cpf/", true, 2));
		System.out.println("Terceira  : "+consultaCrediOnline("12312312387", "cheques/cpf/", true, 3));
		
		System.out.println(getDateTime());
/*	
		String xml= "";
		try {  
			JSONObject json = XML.toJSONObject(xml);   
			        String jsonString = json.toString(4);  
			        System.out.println(jsonString);  
			  
		}catch (JSONException e) {  
			// TODO: handle exception  
			System.out.println(e.toString()); 
		}
*/

	}	
	
	public static String consultaCrediOnline(String doc, String servico, boolean fechar, int num) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		CrediOnLineRestritivoCPF obj1 = new CrediOnLineRestritivoCPF();		
		CrediOnLineRelatorioCPF  obj2 = new CrediOnLineRelatorioCPF();
		CrediOnLineChequeCPF     obj3 = new CrediOnLineChequeCPF();
		
		RelatorioCNPJ relatorioCnpj = new RelatorioCNPJ();
		String solicitacao = "";
		int HTTP_COD_SUCESSO = 200;
		String resposta = "";
		
		solicitacao = servico +
					  doc+
					  "?email=" +
					  GlobalConstants.EMAILCRED +
					  "&token=" +
					  GlobalConstants.TOKENCRED;
		try {
			url = new URL(GlobalConstants.URLCRED+solicitacao);
			con = (HttpURLConnection) url.openConnection();
			
			//con.setRequestProperty("Connection", "Keep-Alive");
			//System.out.println("Conexão : "+con.getHeaderField("keep-alive"));
			
			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				if(con.getResponseCode()==400) {
					resposta = "400 - Documento invalido";
				}else if(con.getResponseCode()==401) {
					resposta = "401 - Acesso negado";
				}else if(con.getResponseCode()==404) {
					resposta = "404 - Documento nao encontrado";		
				}else if(con.getResponseCode()==429) {
					resposta = "429 - Limite de consulta excedido";
				}else {
					resposta = "Servico não Disponivel";			
				}
				return resposta;
			}
						
			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resposta += line.trim();
			}
			
			Gson gson = new Gson();			

			if(num==1) {
				System.out.println("*****111 : "+resposta);				
				obj1 = gson.fromJson(resposta, CrediOnLineRestritivoCPF.class);
			} else if(num==2) {
				obj2 = gson.fromJson(resposta, CrediOnLineRelatorioCPF.class);	
				System.out.println("*****222 : "+resposta);
			} else if(num==3) {
				obj3 = gson.fromJson(resposta, CrediOnLineChequeCPF.class);	
				System.out.println("*****333 : "+resposta);
			}
											
			if(fechar) {
				br.close();				
				con.disconnect();
			}
			
		} catch (JsonSyntaxException e) {
			//cache.apagaCache(protocolo);
			//log.registro(protocolo, "ERRO-CREDIOLINE", e.getMessage());
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");
			e.printStackTrace();
		} catch (IOException e) {
			//cache.apagaCache(protocolo);
			//log.registro(protocolo, "ERRO-CREDIOLINE", e.getMessage());
			relatorioCnpj.setStatusRetorno(1);
			relatorioCnpj.setMensagemRetorno("Erro na comunicacao com o Fornecedor 2");	
			e.printStackTrace();
		}
		return resposta;
	}	

	public static String consultar(String xml) {
		ExecutaConsulta exec = new ExecutaConsulta();
		return exec.executar(xml);
	}

	public static void consultaCdlRio(String doc) {
		String xml = ""; 
		//CDLRio
		//System.out.println(consultar(xml));
		
		xml =	"<SPCA-XML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.scpc.inf.br/spcn/spcaxmlefx.xsd\">" + 
				"	<VERSAO>20180430</VERSAO>" + 
				"	<SOLICITACAO>" + 
				"		<S-CODIGO>23960</S-CODIGO>" + 
				"		<S-SENHA>83690</S-SENHA>" + 
				"		<S-CONSULTA>648</S-CONSULTA>" + 
				"		<S-SOLICITANTE>RJ001</S-SOLICITANTE>" + 
				"		<S-NUMERO-RESPOSTA>269718569-8</S-NUMERO-RESPOSTA>" +				  
				"	</SOLICITACAO>" + 
				"</SPCA-XML>";
		
		Fornecedores fornecedo = new Fornecedores();				
		BufferedReader br = null;				
		br = fornecedo.consultaCDLRio(xml, "11111111");

		if(br==null) {
			System.out.println("Deu erro");
		}		
		
	}
	
	private static String getDateTime() { 
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS"); 
		Date date = new Date(); 
		return dateFormat.format(date); 
	}
}
			