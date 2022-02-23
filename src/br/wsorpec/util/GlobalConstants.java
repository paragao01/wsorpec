package br.wsorpec.util;

import br.wsorpec.util.ArquivoParametro;

public class GlobalConstants {
    
	/* Banco MS SQL SERVER - Ambiente de Producao*/
	public static String DRVBANCO = ArquivoParametro.buscaparametro("DRVBANCO");
	public static String URLBANCO = ArquivoParametro.buscaparametro("URLBANCO");
	public static String USRBANCO = ArquivoParametro.buscaparametro("USRBANCO");	
	public static String PWRBANCO = ArquivoParametro.buscaparametro("PWRBANCO");									
		
	/*Diretorio do log*/
	public static String PASTALOG = System.getProperty("user.dir")+ArquivoParametro.buscaparametro("PASTALOG");
	
	/*Tempo do Cache*/
	public static int TEMPOCACH = Integer.valueOf(ArquivoParametro.buscaparametro("TEMPOCACH"));
	
	/*Informacoes para consultar no CDLRio*/
	public static String URLCDLRIO = ArquivoParametro.buscaparametro("URLCDLRIO");
	public static String USRCDLRIO = ArquivoParametro.buscaparametro("USRCDLRIO");
	public static String PWRCDLRIO = ArquivoParametro.buscaparametro("PWRCDLRIO");
	public static String URLCDLNEG = ArquivoParametro.buscaparametro("URLCDLNEG");
	

	/*Informacoes para consultar CrediOnLine*/
	public static String URLCRED   = ArquivoParametro.buscaparametro("URLCRED");	
	public static String EMAILCRED = ArquivoParametro.buscaparametro("EMAILCRED");
	public static String TOKENCRED = ArquivoParametro.buscaparametro("TOKENCRED");
	
	/*Informar o IP remoto*/
	public static String IPREMOTO;
	
	/*Armazena para fazer a bilhetagem*/
	public static int idCliente;
	public static int idPai;
	
	/*Entidade, firma e senha para incluirvnegativacao no CDLRio
	 *Quando sair a carta para o devedor ira com o remetente cliente OrpeCredit
	*/
	public static String entidade;
	public static String firma;
	public static String senha;
	public static String protocoloCDLRio;
	
}