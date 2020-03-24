package br.wsorpec.autenticacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import br.wsorpec.acessoBanco.Conexao;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.Util;

public class Autenticacao extends Conexao {
	
	public String logon(String codigo, String senha, String produto, String protocolo) {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;		
		ResultSet rs = null;
		ResultSet rs1 = null;		
		Connection conn = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();		
		Log log = new Log(false, GlobalConstants.PASTALOG);
		String resp = "";
		int idCliente = 0;
		boolean pro = false;
		boolean result = false;
		try {
			conn = this.getConnection();
			
			sql.append("SELECT Cliente.idCliente, Produto.idProduto, Cliente.CodigoCliente, Cliente.Senha,					\n");
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
					idCliente = rs.getInt("idCliente");
					pro = true;
					break;
				}	
			}
			if (result) {
				if(pro) {
					resp = "OK";
					if(produto.equals("72")) {
						sql1.append("SELECT EntidadeOrigem, FirmaOrigem, SenhaOrigem from NegativacaoClientes where idcliente= "+idCliente);
						ps1 = conn.prepareStatement(sql1.toString());
						rs1 = ps1.executeQuery();
						if(rs1.next()) {
							GlobalConstants.entidade = rs1.getString("EntidadeOrigem");
							GlobalConstants.firma = rs1.getString("FirmaOrigem");
							GlobalConstants.senha = rs1.getString("SenhaOrigem");
							if(Util.Empty(GlobalConstants.entidade)||
							   Util.Empty(GlobalConstants.firma)||
							   Util.Empty(GlobalConstants.senha)) {
								resp = "Usuario sem login para negativacao ao fornecedor 1";
							}
						}else{
							resp = "Usuario sem login para negativacao ao fornecedor 1";
						}
					}
					GlobalConstants.idCliente = rs.getInt("CodigoCliente");					
				}else{
					resp = "Produto nao autorizado para esse usuario";
				}
			}else{
				resp = "Codigo ou senha invalido(s)";
			}
		} catch (Exception e) {
			resp = "ERRO de autenticação do cliente ao sistema";
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeResultSet(rs1);
				this.closeStatement(ps);
				this.closeStatement(ps1);				
				this.closeConnection(conn);
			} catch (Exception e) {
				resp = "ERRO de autenticação do cliente ao sistema";				
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
		return resp;
	}	

}
