package br.wsorpec.servico;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.negativacao.Baixar;
import br.wsorpec.wscdlrio.negativacao.ExecutaBaixar;
import br.wsorpec.wscdlrio.negativacao.ExecutaIncluir;
import br.wsorpec.wscdlrio.negativacao.ExecutaListar;
import br.wsorpec.wscdlrio.negativacao.Incluir;
import br.wsorpec.wscdlrio.negativacao.Listar;
import br.wsorpec.wscdlrio.negativacao.Listar.ListaDocumentos;

public class ServicoNegativacao {
	
	public static String incluir(String xml) {
		Incluir incluir = new Incluir();
		ExecutaIncluir executor = new ExecutaIncluir();
		XStream stream = new XStream(new DomDriver());
		String xmlRet = "";

		try {
			stream.alias("Incluir", Incluir.class);
			incluir = (Incluir) stream.fromXML(xml);

			if ((incluir.getIp() != null) && (incluir.getIp() != "")) {
				GlobalConstants.IPREMOTO = incluir.getIp();
			} else {
				GlobalConstants.IPREMOTO = Util.ipRemoto();
			}

			incluir = executor.executar(incluir, true);

			incluir.setIp(GlobalConstants.IPREMOTO);

			stream.alias("Incluir", Incluir.class);
			xmlRet = stream.toXML(incluir);

			xmlRet = xmlRet.replace("\n", "");
			xmlRet = xmlRet.replace(">  <", "><");
			xmlRet = xmlRet.replace(">    <", "><");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlRet;
	}
	
	public static String baixar(String xml) {
		Baixar baixar = new Baixar();
		ExecutaBaixar executor = new ExecutaBaixar();
		XStream stream = new XStream(new DomDriver());
		String xmlRet = "";

		try {
			stream.alias("Baixar", Baixar.class);
			baixar = (Baixar) stream.fromXML(xml);

			if ((baixar.getIp() != null) && (baixar.getIp() != "")) {
				GlobalConstants.IPREMOTO = baixar.getIp();
			} else {
				GlobalConstants.IPREMOTO = Util.ipRemoto();
			}

			baixar = executor.executar(baixar, true);

			baixar.setIp(GlobalConstants.IPREMOTO);

			stream.alias("Baixar", Baixar.class);
			xmlRet = stream.toXML(baixar);

			xmlRet = xmlRet.replace("\n", "");
			xmlRet = xmlRet.replace(">  <", "><");
			xmlRet = xmlRet.replace(">    <", "><");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlRet;
	}

	public static String listar(String xml) {
		Listar listar = new Listar();
		ExecutaListar executor = new ExecutaListar();
		XStream stream = new XStream(new DomDriver());
		String xmlRet = "";

		try {
			stream.alias("Listar", Listar.class);
			listar = (Listar) stream.fromXML(xml);

			if ((listar.getIp() != null) && (listar.getIp() != "")) {
				GlobalConstants.IPREMOTO = listar.getIp();
			} else {
				GlobalConstants.IPREMOTO = Util.ipRemoto();
			}

			listar = executor.executar(listar, true);

			listar.setIp(GlobalConstants.IPREMOTO);

			stream.alias("Listar", Listar.class);
			stream.alias("ListaDocumentos", ListaDocumentos.class);
			xmlRet = stream.toXML(listar);

			xmlRet = xmlRet.replace("\n", "");
			xmlRet = xmlRet.replace(">  <", "><");
			xmlRet = xmlRet.replace(">    <", "><");
			xmlRet = xmlRet.replace(">      <", "><");
			xmlRet = xmlRet.replace(">        <", "><");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmlRet;
	}
/*
	public static void main(String[] args) {
		XStream stream = new XStream(new DomDriver());
		Incluir incluir = new Incluir();
		
		incluir.setCodigo("00001");
		incluir.setSenha("08493296");
		incluir.setTipoPessoa("F");
		incluir.setDocumento("12312312387");
		//incluir.setObservacao("Teste do Sistema Orpec");
		
		incluir.getDadosDevedor().setTipoDevedor("C");
		incluir.getDadosDevedor().setNome("Raimundo Correa");
		incluir.getDadosDevedor().setDataNascimento("01011990");
		incluir.getDadosDevedor().setFiliaçãoMae("Maria da Silva");
		incluir.getDadosDevedor().setSexo("1");
		incluir.getDadosDevedor().setEstadoCivil("2");
		incluir.getDadosDevedor().setNaturalidade("RJ");
		incluir.getDadosDevedor().setCpf("12312312387");
		
		incluir.getDadosContrato().setContrato("5566778899");
		incluir.getDadosContrato().setNaturezaOperacao("01");
		incluir.getDadosContrato().setDataAtTraso("01102019");
		incluir.getDadosContrato().setDataTermino("30102019");
		incluir.getDadosContrato().setValorParcela("100,98");
		incluir.getDadosContrato().setQuantidade("1");
		incluir.getDadosContrato().setMoeda("J");
		
		//incluir.getDadosDoc().setNumero("12312312387");
		//incluir.getDadosDocumento().setTipo("OU");
		//incluir.getDadosDoc().setEstado("RJ");
		
		incluir.getDadosEndereco().setLogradouro("Rua da Alfandega, 125");
		incluir.getDadosEndereco().setBairro("Centro");
		incluir.getDadosEndereco().setMunicipio("Rio de Janeiro");
		incluir.getDadosEndereco().setEstado("RJ");;
		incluir.getDadosEndereco().setCep("22071110");
		//incluir.getDadosEndereco().setTipo("1");
		
		//incluir.getDadosTelefone().setDdd1("21");
		//incluir.getDadosTelefone().setNumero1("22564545");
		//incluir.getDadosTelefone().setRamal1("231");
		//incluir.getDadosTelefone().setTipo1("1");
		//incluir.getDadosTelefone().setDdd2("21");
		//incluir.getDadosTelefone().setNumero2("999995566");
		//incluir.getDadosTelefone().setRamal2("222");
		//incluir.getDadosTelefone().setTipo2("3");
		
		//incluir.setCodigoRetorno("");
		//incluir.setMensagemRetorno("");
		
		System.out.println(incluir(stream.toXML(incluir)));
		
	}
*/
/*		
	public static void main(String[] args) {
		XStream stream = new XStream(new DomDriver());
		Baixar baixar = new Baixar();
		
		baixar.setCodigo("00001");
		baixar.setSenha("08493296");
		baixar.setTipoPessoa("F");
		baixar.setDocumento("12312312387");
		baixar.setIdNegativacao(75);
				
		//Dados da Baixa
		baixar.getDadosBaixa().setContrato("5566778899");
		baixar.getDadosBaixa().setMotivo("01");
		baixar.getDadosBaixa().setNome("Raimundo Correa");
		baixar.getDadosBaixa().setDataNascimento("01011990");
		baixar.getDadosBaixa().setValorParcela("00000000000000");
		baixar.getDadosBaixa().setTipoDocumento("CPF");
		baixar.getDadosBaixa().setEstado("RJ");
		//baixar.getDadosBaixa().setNome("Lojas Americanas");
		baixar.getDadosBaixa().setDataAtraso("01012019");		
		//baixar.getDadosBaixa().setTipoDocumento("CNPJ");
		baixar.getDadosBaixa().setEstado("RJ");

		baixar.setCodigoRetorno("");
		baixar.setMensagemRetorno("");
		
		System.out.println(baixar(stream.toXML(baixar)));
	}
*/

	public static void main(String[] args) {
		XStream stream = new XStream(new DomDriver());
		Listar listar = new Listar();
		
		listar.setCodigo("00001");
		listar.setSenha("08493296");
		listar.setTipoPessoa("F");
		listar.setDocumento("12312312387");
		listar.setDataInicio("01012020");
		listar.setDataFim("09032020");
		listar.setStatus("T");
				
		System.out.println(listar(stream.toXML(listar)));
		
	}

	
}
