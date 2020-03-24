package br.wsorpec.fornecedor;

import java.io.BufferedReader;
import java.io.IOException;
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

import com.google.gson.Gson;

import br.wsorpec.crediOnline.relatorioCNPJ.CrediOnLineRelatorioCNPJ;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.wscdlrio.empresarialGold.SPCAXML;

public class Fornecedores {

	private static Log log;
	
	public BufferedReader consultaCDLRio(String solicitacao, String protocolo) {
		URL url;
		log = new Log(false, GlobalConstants.PASTALOG);
		HttpURLConnection con;
		BufferedReader br = null;
		String resp = "";
		SPCAXML obj = new SPCAXML();
		
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
			//con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

			// Write post data
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(solicitacao);
			writer.flush();
			writer.close();
			
			//InputStream is = null;
			
			try {
				//is = con.getInputStream();

				byte[] imageBytes = IOUtils.toByteArray(con.getInputStream());

				resp = new String(imageBytes, "ISO-8859-1");
				resp = resp.trim().replace("\n", "");
				resp = resp.trim().replace("    ", "");
				br = new BufferedReader(new StringReader(resp));

				System.out.println(resp);

				// Faco o parse aqui xml->classe java
				JAXBContext jaxbContext = JAXBContext.newInstance(SPCAXML.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				obj = (SPCAXML) jaxbUnmarshaller.unmarshal(br);

				System.out.println("-------------------------------------------------------------");
				System.out.println(obj.getRESPOSTA().getRESPOSTARETORNO().getSTATUSRESPOSTA());
				System.out.println(obj.getRESPOSTA().getRESPOSTARETORNO().getMENSAGEMRESPOSTA());
				
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
				e.printStackTrace();
				// Perform any other exception handling that's appropriate.
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				//if (is != null) {
				//	is.close();
				//}
			}
				
				br = new BufferedReader(new InputStreamReader((con.getInputStream())));
			
			
			/*
			BufferedReader br1 = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = br1.readLine()) != null) {
			  sb.append(line);
			}
			br1.close();

			byte[] latin1 = new String(sb.toString().getBytes("UTF-8")).getBytes("ISO-8859-1");

			System.out.println(sb.toString());
			System.out.println(new String(latin1));
			*/
//			   System.out.println( new String("ÇÁÕ".getBytes(), "UTF-8") );
//			   System.out.println( new String("ÇÁÕ".getBytes(), "ISO-8859-1") );
//			   System.out.println( new String("ÇÁÕ".getBytes("UTF-8"), "UTF-8") );
//			   System.out.println( new String("ÇÁÕ".getBytes("ISO-8859-1"), "ISO-8859-1") );

/*			
//			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

//			String line;
//			while ((line = br.readLine()) != null) {
//				resp += line+"\n";
//			}
			
			System.out.println(resp);
			
			String encodedWithISO88591 = "Ã¼zÃ¼m baÄlarÄ±";
			String decodedToUTF8 = new String(resp.getBytes("ISO-8859-1"), "UTF-8");
			
			System.out.println(decodedToUTF8);
			
			
			String encodedWithUTF8 = "üzüm bağları";
			String decodedToISO88591 = new String(resp.getBytes("UTF-8"), "ISO-8859-1");
			//Result, decodedToISO88591 --> "Ã¼zÃ¼m baÄlarÄ±"
			System.out.println(decodedToISO88591);
*/			
			
			//is.close();
			//br.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return br;
	}	
	
	public BufferedReader consultaCrediOline(String documento, String tipoPessoa, String solicitacao, String protocolo) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		int HTTP_COD_SUCESSO = 200;
		String resp = "";

		try {						
			if(tipoPessoa.equals("F")) {
				solicitacao = "relatorio/cpf/"+documento+"?email="+GlobalConstants.EMAILCRED+"&token="+GlobalConstants.TOKENCRED;
			}else {
				solicitacao = "relatorio/cnpj/"+documento+"?email="+GlobalConstants.EMAILCRED+"&token="+GlobalConstants.TOKENCRED;
			}	

			url = new URL(GlobalConstants.URLCRED+solicitacao);
			con = (HttpURLConnection) url.openConnection();
			
			if (con.getResponseCode() != HTTP_COD_SUCESSO) {
				log.registro(protocolo, "ERRO", "HTTP error code : " + con.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				resp += line.trim();
			}

			br.close();
			con.disconnect();
			
			Gson gson = new Gson();			
			if(!tipoPessoa.equals("F")) {
				CrediOnLineRelatorioCNPJ obj = gson.fromJson(resp, CrediOnLineRelatorioCNPJ.class);
				System.out.println(obj.getRazaoSocial());
			}else{
				
			}
		} catch (MalformedURLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} catch (IOException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		}
		return br;

	}
	
}
