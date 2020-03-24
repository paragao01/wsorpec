package br.wsorpec.util;

import java.util.ArrayList;

public class LerRespostaV7 {

	private int codigo;
	private int inicio;
	private int tamanho;
	private int fim;
	private LerRespostaV7 obj;
	
	public LerRespostaV7() {
		
	}

	/**
	 * @return the codigo
	 */
	public int getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return the fim
	 */
	public int getFim() {
		return fim;
	}

	/**
	 * @param fim the fim to set
	 */
	public void setFim(int fim) {
		this.fim = fim;
	}

	/**
	 * @return the inicio
	 */
	public int getInicio() {
		return inicio;
	}

	/**
	 * @param inicio the inicio to set
	 */
	public void setInicio(int inicio) {
		this.inicio = inicio;
	}

	/**
	 * @return the tamanho
	 */
	public int getTamanho() {
		return tamanho;
	}

	/**
	 * @param tamanho the tamanho to set
	 */
	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public ArrayList<LerRespostaV7> separaResposta(String strRet){
		ArrayList<LerRespostaV7> resultado = new ArrayList<LerRespostaV7>();
		int qtdResp = 0;
		int posInicial = 0;
		
		qtdResp = Integer.parseInt(strRet.substring(169, 171));
		posInicial = 171;
		
		for (int i=0;i<qtdResp;i++) {
			obj = new LerRespostaV7();
			obj.setCodigo(Integer.parseInt(strRet.substring(posInicial+0, posInicial+3)));
			obj.setInicio(Integer.parseInt(strRet.substring(posInicial+3, posInicial+7)));
			obj.setTamanho(Integer.parseInt(strRet.substring(posInicial+7, posInicial+11)));
			obj.setFim((obj.getInicio()+obj.getTamanho())-1);
			resultado.add(obj);
			posInicial+=11;
		}
		return resultado;
	}
}
