package br.wsorpec.wscdlrio.negativacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.wsorpec.autenticacao.Autenticacao;
import br.wsorpec.faturamento.Bilhetagem;
import br.wsorpec.log.Log;
import br.wsorpec.util.Formatador;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.LerRespostaV7;
import br.wsorpec.util.ParteFixaV7;
import br.wsorpec.util.Util;

public class ExecutaIncluir {

	private static Log log;
	private static String protocolo;
	

	public ExecutaIncluir() {}
	
	public Incluir executar(Incluir incluir, boolean xml) {
		XStream stream = new XStream(new DomDriver());
		Bilhetagem bilhetar = new Bilhetagem();

		if (!critica(incluir)) {
			incluir.setSenha("");
			return incluir;
		}
		
		log = new Log(false, GlobalConstants.PASTALOG);
		protocolo = Util.geraProtocolo();	
		
		//Criptografa a senha;
		incluir.setSenha(Util.converteMD5(incluir.getSenha(), protocolo, log));
		
		String codigo      = incluir.getCodigo();
        String senha       = incluir.getSenha();
		String produto     = "72";
		String solicitacao = "PRODUTO : "+produto+" - "+stream.toXML(incluir).replace("\n", "");
		
		log.registro(protocolo, "SNDWSORPEC-INCLUIR", solicitacao);
			
		//Fazer logon - verificar codigo, senha e se o produto pode ser consultado.
		Autenticacao aut = new Autenticacao();
		String strLogon = aut.logon(codigo, senha, produto, protocolo);
		if(!strLogon.equals("OK")) {
			log.registro(protocolo, "RCV", strLogon);
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno(strLogon);				
			return incluir;
		}
		
		//Verificar se nao eh duplicidade de inclusao
		if(!bilhetar.verificaInclusao(incluir, protocolo)) {
			log.registro(protocolo, "RCV", "Erro: Registro já incluido com o mesmo documento e contrato");
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Erro: Registro já incluido com o mesmo documento e contrato");			
			return incluir;
		}
		//Fazer a consulta nos BUREAU
		incluir = consultaCDLRio(incluir);
		
		//Fazer a bilhetagem 
		if(incluir.getCodigoRetorno().equals("1")) {
			bilhetar.bilhetar(GlobalConstants.idCliente, incluir.getDocumento(), produto, GlobalConstants.IPREMOTO, protocolo);
			/*Grava Informacoes na tabela de negativacao.*/
			bilhetar.gravarInclusao(incluir, protocolo);
		}

		log.registro(protocolo, "RCVWSORPEC-INCLUIR", stream.toXML(incluir).replace("\n", ""));
		
		return incluir;
	}

	public boolean critica(Incluir incluir){
		
		if(Util.Empty(incluir.getCodigo())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Codigo nao informado");
			return false;
		}else{
			if(!Util.isdigit(incluir.getCodigo())){
				incluir.setCodigoRetorno("0");
				incluir.setMensagemRetorno("Codigo invalido");				
				return false;
			}
		}
		if(Util.Empty(incluir.getSenha())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Senha nao informada");				
			return false;
		}
		if (Util.Empty(incluir.getTipoPessoa())) {
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Tipo Pessoa nao informado");				
			return false;			
		}else {
			if (!incluir.getTipoPessoa().equals("F") && !incluir.getTipoPessoa().equals("J")) {
				incluir.setCodigoRetorno("0");
				incluir.setMensagemRetorno("Tipo Pessoa invalido");				
				return false;
			}
		}
		if (Util.Empty(incluir.getDocumento())) {
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Documento nao informado");				
			return false;			
		}else {
			if(incluir.getTipoPessoa().equals("F")) {
				if (!Util.IsCPF(incluir.getDocumento())) {
					incluir.setCodigoRetorno("0");
					incluir.setMensagemRetorno("CPF invalido");				
					return false;
				}
				incluir.getDadosDevedor().setCpf(incluir.getDocumento());
				incluir.getDadosDocumento().setNumero(incluir.getDocumento());
				incluir.getDadosDocumento().setTipo("CPF");
			}else{
				if (!Util.IsCNPJ(incluir.getDocumento())) {
					incluir.setCodigoRetorno("0");
					incluir.setMensagemRetorno("CNPJ invalido");				
					return false;
				}
				incluir.getDadosDocumento().setNumero(incluir.getDocumento());
				incluir.getDadosDocumento().setTipo("CNPJ");
			}
		}
		if(Util.Empty(incluir.getDadosContrato().getContrato())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Contrato nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosContrato().getNaturezaOperacao())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Natureza da Operacao nao informada");				
			return false;
		}
		if(Util.Empty(incluir.getDadosContrato().getDataAtTraso())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Data do Atraso nao informada");				
			return false;
		}else {
			if(!Util.isDateValid(incluir.getDadosContrato().getDataAtTraso(), "ddMMyyyy")) {
				incluir.setCodigoRetorno("0");
				incluir.setMensagemRetorno("Data do Atraso invalida");				
				return false;					
			}
		}
		if(Util.Empty(incluir.getDadosContrato().getDataTermino())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Data do Termino nao informada");				
			return false;
		}else {
			if(!Util.isDateValid(incluir.getDadosContrato().getDataTermino(), "ddMMyyyy")) {
				incluir.setCodigoRetorno("0");
				incluir.setMensagemRetorno("Data do Termino invalida");				
				return false;					
			}
		}
		if(Util.Empty(incluir.getDadosContrato().getValorParcela())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Valor da Parcela nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosContrato().getQuantidade())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Quantidade nao informada");				
			return false;
		}
		if(Util.Empty(incluir.getDadosContrato().getMoeda())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Moeda nao informada");				
			return false;
		}
		if(Util.Empty(incluir.getDadosDevedor().getTipoDevedor())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Tipo do Devedor invalido");				
			return false;
		}else if(!incluir.getDadosDevedor().getTipoDevedor().equals("C")&&
				 !incluir.getDadosDevedor().getTipoDevedor().equals("F")&&
				 !incluir.getDadosDevedor().getTipoDevedor().equals("E")&&
				 !incluir.getDadosDevedor().getTipoDevedor().equals("")){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Tipo do Devedor invalido");				
			return false;
		}
		if(Util.Empty(incluir.getDadosDevedor().getNome())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Nome do Devedor nao informado");				
			return false;
		}
		if(incluir.getTipoPessoa().equals("F")) {		
			if(!Util.Empty(incluir.getDadosDevedor().getDataNascimento())){
				if(!Util.isDateValid(incluir.getDadosDevedor().getDataNascimento(), "ddMMyyyy")) {
					incluir.setCodigoRetorno("0");
					incluir.setMensagemRetorno("Data de Nascimento do Devedor invalida");				
					return false;					
				}
			}else {
				incluir.setCodigoRetorno("0");
				incluir.setMensagemRetorno("Data de Nascimento do Devedor nao informada");				
				return false;									
			}
		}
		if(Util.Empty(incluir.getDadosEndereco().getLogradouro())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Logradouro nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosEndereco().getBairro())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Bairro nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosEndereco().getMunicipio()	)){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Municipio nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosEndereco().getEstado())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Estado nao informado");				
			return false;
		}else {
			incluir.getDadosDocumento().setEstado(incluir.getDadosEndereco().getEstado());
		}
		if(Util.Empty(incluir.getDadosEndereco().getCep())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("CEP nao informado");				
			return false;
		}
		if(Util.Empty(incluir.getDadosEndereco().getTipo())){
			incluir.setCodigoRetorno("0");
			incluir.setMensagemRetorno("Tipo do Endereco nao informado");				
			return false;
		}
		return true;
	}
	
	public Incluir consultaCDLRio(Incluir incluir) {
		URL url;
		HttpURLConnection con;
		BufferedReader br = null;
		String retorno = "";
		
		try {
			url = new URL(GlobalConstants.URLCDLNEG+montaSolicitacao(incluir));
			con = (HttpURLConnection) url.openConnection();

			br = new BufferedReader(new InputStreamReader((con.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				retorno += line.trim();
			}
			
			br.close();
			con.disconnect();
			incluir = montaResposta(incluir, retorno);
		} catch (IOException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
			incluir.setCodigoRetorno("00099");
			incluir.setMensagemRetorno("Erro na comunicacao com o Fornecedor 1");			
		}
		return incluir;
	}	
		
	public String montaSolicitacao(Incluir inc) {
		ParteFixaV7 parteFixa = new ParteFixaV7();
		if(inc.getTipoPessoa().equals("F")) {
			parteFixa.setTransacao("SPCSINC");
		} else {
			parteFixa.setTransacao("SIASINC");
		}
		parteFixa.setEntidade(GlobalConstants.entidade);
		parteFixa.setFirma(GlobalConstants.firma);
		parteFixa.setSenha(GlobalConstants.senha);
		String solicitacao = parteFixa.getStr();
		String valor = inc.getDadosContrato().getValorParcela();
		valor = valor.replace(",","");
		valor = valor.replace(".", "");
		
		if(inc.getTipoPessoa().equals("F")) {
			solicitacao += 
					 //Contrato
					 Formatador.formataCampo(inc.getDadosContrato().getContrato(), 20)
					 +Formatador.formataCampo(inc.getDadosContrato().getNaturezaOperacao(), 2)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getDataAtTraso(), 8)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getDataTermino(), 8)
					 +Formatador.zeroEsquerda(valor, 11)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getQuantidade(), 2)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getMoeda(), 1)
					 //DadosDevedor					 
					 +Formatador.formataCampo(inc.getDadosDevedor().getTipoDevedor(), 1)
					 +Formatador.formataCampo(inc.getDadosDevedor().getNome().trim(), 60)
					 +Formatador.zeroEsquerda(inc.getDadosDevedor().getDataNascimento(), 8)
					 +Formatador.formataCampo(inc.getDadosDevedor().getFiliaçãoMae(), 60)
					 +Formatador.formataCampo(inc.getDadosDevedor().getSexo(), 1)
					 +Formatador.formataCampo(inc.getDadosDevedor().getEstadoCivil(), 1)
					 +Formatador.formataCampo(inc.getDadosDevedor().getNaturalidade(), 2)
					 +(inc.getTipoPessoa().equals("F")?Formatador.zeroEsquerda(inc.getDadosDevedor().getCpf(), 11):Formatador.formataCampo(inc.getDadosDevedor().getCpf(), 11))
					 //Documento
					 +Formatador.formataCampo(inc.getDadosDocumento().getNumero(), 15)
					 +Formatador.formataCampo(inc.getDadosDocumento().getTipo(), 5)
					 +Formatador.formataCampo(inc.getDadosDocumento().getEstado(), 2)
					 //Endereco
					 +Formatador.formataCampo(inc.getDadosEndereco().getLogradouro(), 40)
					 +Formatador.formataCampo(inc.getDadosEndereco().getBairro(), 30)
					 +Formatador.formataCampo(inc.getDadosEndereco().getMunicipio(), 30)
					 +Formatador.formataCampo(inc.getDadosEndereco().getEstado(), 2)
					 +Formatador.zeroEsquerda(inc.getDadosEndereco().getCep().replace("-", ""), 8)
					 +Formatador.formataCampo(inc.getDadosEndereco().getTipo(), 1)
					 // Telefone
					 +Formatador.zeroEsquerda(inc.getDadosTelefone().getDdd1(), 3)
					 +Formatador.zeroEsquerda(inc.getDadosTelefone().getNumero1(), 8)
					 +Formatador.formataCampo(inc.getDadosTelefone().getRamal1(), 4)
					 +Formatador.formataCampo(inc.getDadosTelefone().getTipo1(), 1)
		 			 +Formatador.zeroEsquerda(inc.getDadosTelefone().getDdd2(), 3)
		 			 +Formatador.zeroEsquerda(inc.getDadosTelefone().getNumero2(), 8)
		 			 +Formatador.formataCampo(inc.getDadosTelefone().getRamal2(), 4)
		 			 +Formatador.formataCampo(inc.getDadosTelefone().getTipo2(), 1)
		 			 // Alerta Devedor
		 			 +Formatador.formataCampo(inc.getObservacao(), 40);
		} else {
			solicitacao += 
					 //Documento
					 Formatador.formataCampo(inc.getDadosDocumento().getNumero(), 14)
					 +Formatador.formataCampo(inc.getDadosDevedor().getNome().trim(), 60)
					 //Endereco
					 +Formatador.formataCampo(inc.getDadosEndereco().getLogradouro(), 60)
					 +Formatador.formataCampo(inc.getDadosEndereco().getBairro(), 30)
					 +Formatador.formataCampo(inc.getDadosEndereco().getMunicipio(), 30)
					 +Formatador.formataCampo(inc.getDadosEndereco().getEstado(), 2)
					 +Formatador.zeroEsquerda(inc.getDadosEndereco().getCep(), 8)
					 // Telefone
					 +Formatador.zeroEsquerda(inc.getDadosTelefone().getDdd1(), 3)
					 +Formatador.zeroEsquerda(inc.getDadosTelefone().getNumero1(), 8)
					 //Contrato
					 +Formatador.formataCampo(inc.getDadosContrato().getContrato(), 20)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getQuantidade(), 2)					 
					 +Formatador.zeroEsquerda(valor, 14)					 
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getDataAtTraso(), 8)
					 +Formatador.zeroEsquerda(inc.getDadosContrato().getDataTermino(), 8)
					 +Formatador.formataCampo(inc.getDadosContrato().getNaturezaOperacao(), 2)
					 +Formatador.formataCampo(inc.getDadosDevedor().getTipoDevedor(), 1)					 
		 			 // Reservado
		 			 +Formatador.formataCampo(inc.getObservacao(), 230);
		}
		log.registro(protocolo, "SNDCDLRio-INCLUIR", solicitacao);
		return solicitacao.replaceAll(" ","%20");
	}
	
	public Incluir montaResposta(Incluir inc, String retorno) {
   		LerRespostaV7 lrv = new LerRespostaV7();
   		ArrayList<LerRespostaV7> listaResp = new ArrayList<LerRespostaV7>();
   		
		retorno = retorno.replace("<HTML><HEAD></HEAD><BODY>","");
		retorno = retorno.replace("</BODY></HTML>","");
		
		if(retorno.substring(0, 7).equals("SPCRINC")) {
			inc.setCodigoRetorno("1");
			inc.setMensagemRetorno("Inclusao realizada com sucesso");
			GlobalConstants.protocoloCDLRio = retorno.substring(63, 72);
		} else if(retorno.substring(0, 7).equals("SIARINC")) {
			inc.setCodigoRetorno("1");
			inc.setMensagemRetorno("Inclusao realizada com sucesso");
			GlobalConstants.protocoloCDLRio = retorno.substring(63, 72);
		}else{
       		String registro001 = "";
       		String strRet = retorno;
       		       		
			listaResp = lrv.separaResposta(retorno);
			
			for (int i = 0; i < listaResp.size(); i++) {
				lrv = (LerRespostaV7) listaResp.get(i);

				switch (lrv.getCodigo()) {
				case 1:
					registro001 = strRet.substring(lrv.getInicio() - 1, lrv.getFim());
					break;
				}
			}
			if (registro001.trim().length()>0) {
				inc.setCodigoRetorno(registro001.substring(2, 7));
				inc.setMensagemRetorno(registro001.substring(7, 44));
				if(inc.getCodigoRetorno().equals("00046")) {
					inc.setMensagemRetorno("* NOME INFORMADO NAO CONFERE COM O NOME DA SINTESE");
				}
			}
		}
		log.registro(protocolo, "RCVCDLRio-INCLUIR", retorno);;		
		return inc;
	}
}
