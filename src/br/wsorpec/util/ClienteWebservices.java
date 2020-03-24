package br.wsorpec.util;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class ClienteWebservices {

	public static void main(String[] args) {
		RPCServiceClient serviceClient = null;

		try {
			serviceClient = new RPCServiceClient();			
			EndpointReference targetEPR = new EndpointReference("http://localhost:8080/axis2/services/ServicoPessoalGold.ServicoPessoalGoldHttpSoap12Endpoint/");

			// Authorizing access to the url
			QName action = new QName("http://servico.wsorpec.br", "pessoalGold");

			Options options = serviceClient.getOptions();
			options.setTo(targetEPR);
			
			//codigo=00001&senha=08493296&cpf=29588226805
			Object obj[] = new Object[] { "00001", "08493296", "29588226805" }; 
			
			OMElement responseObject = serviceClient.invokeBlocking(action, obj);
			//String response = responseObject.getFirstElement().getText();
			//System.out.println(response);
			
			System.out.println(responseObject);
		} catch (AxisFault e) {
			e.printStackTrace();
		} finally {
			try {
				serviceClient.cleanup();
				serviceClient.cleanupTransport();				
			} catch (AxisFault e) {
				// do nothing
			}
		}
	}
}
