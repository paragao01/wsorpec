package br.wsorpec.acessoBanco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;


public abstract class Conexao {
	private Log log;
	private String protocolo;
			
	private final String URL = GlobalConstants.URLBANCO,
		 				 PWR = GlobalConstants.PWRBANCO,
		 				 USR = GlobalConstants.USRBANCO,
		 				 DRV = GlobalConstants.DRVBANCO;

	private Connection linkBanco = null;
	
	public Conexao(){}
	
    public Conexao(String numProtocolo) {
    	log = new Log(false, GlobalConstants.PASTALOG);
    	protocolo = numProtocolo;
    }

	/**
	 * Metodo que gerencia as conexoes.
	 */
	public Connection getConnection(){
		try {
			Class.forName(DRV);
		    linkBanco = DriverManager.getConnection(URL, USR, PWR);
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} catch (ClassNotFoundException e1) {
			log.registro(protocolo, "ERRO", e1.getMessage());
		}
		return linkBanco;		
	}
	
	/**
	 * Metodo que fecha o ResultSet.
	 * @param ResultSet
	 * @return void
	 */
	public void closeResultSet(ResultSet rs){
		try {
			if (rs != null ) {
              rs.close();
			}
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());			
		}
	}
	
	/**
	 * Metodo que fecha o Statement.*/
	public void closeStatement(Statement stmt){
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		}
	}
	
	/**
	 * Metodo que fecha a conexao.*/
	public void closeConnection(Connection conn) {
		try {
			if ( (conn != null) && !conn.isClosed() ) {				
//				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		}
	}
    
	public Integer getAutoIncrementByEntidade(String table, String rowName, String idEntidade) {
		int returnId = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		try {
			conn = getConnection();
			sql.append(" SELECT MAX( ");
			sql.append(rowName);
			sql.append(" ) ID FROM ");
			sql.append(table);
			sql.append(" WHERE ID_ENTIDADE = ? ");

			int i=0;
            
            ps = conn.prepareStatement(sql.toString());
            ps.setInt(++i, Integer.valueOf(idEntidade));
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnId = rs.getInt("ID");
			}
			
			returnId++;
			
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(ps);
				closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
		return returnId;
	}

	
	public Long getAutoIncrementByUnidadeNegocioGestor(String table, String rowName, Long idUnidadeNegocio,Long idGestor) {
		long returnId = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		try {
			conn = getConnection();
			sql.append(" SELECT MAX( ");
			sql.append(rowName);
			sql.append(" ) ID FROM ");
			sql.append(table);
			sql.append(" WHERE IDUNDNGC = ? ");
			sql.append(" AND IDGTO = ? ");

			int i=0;
            
            ps = conn.prepareStatement(sql.toString());
            ps.setLong(++i,idUnidadeNegocio.longValue());
            ps.setLong(++i,idGestor.longValue());
            
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnId = rs.getLong("ID");
			}
			
			returnId++;
			
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(ps);
				closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
		return new Long(returnId);
	}


	public Integer getAutoIncrement(String table, String rowName) {
		int returnId = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		try {
			conn = getConnection();
			sql.append(" SELECT MAX( ");
			sql.append(rowName);
			sql.append(" ) ID FROM ");
			sql.append(table);
            
            ps = conn.prepareStatement(sql.toString());
    		rs = ps.executeQuery();
			
			if (rs.next()) {
				returnId = rs.getInt("ID");
			}
			returnId++;
			
		} catch (SQLException e) {
			log.registro(protocolo, "ERRO", e.getMessage());
		} finally {
			try {
				closeResultSet(rs);
				closeStatement(ps);
				closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO", e.getMessage());
			}
		}
		return returnId;
	}
	
	// captura o codigo(PK) do registro recem-incluido na tabela
	public long getUltimoCodigo(PreparedStatement pst) {
	    ResultSet rs = null;
	    long codigo = 0;
	    try {
	        rs = pst.getGeneratedKeys();
	        while (rs.next()) {
	            codigo = rs.getLong(1);
	        }
	        return codigo;
	    } catch (Exception e) {
			log.registro(protocolo, "ERRO", e.getMessage());
	    }
	    return codigo;
	}
	
	/*
	 * Seta objetos em PreparedStatement verificando se nulo
	 * @param PreparedStatement pstmt - Statement alvo
	 * @param int dataType - um dos valores em java.sql.Types
	 * @param int idx - indice do parametro no sql
	 * @param String str - valor que sera setado no PreparedStatement
	 */
	public void setObject(PreparedStatement pstmt, int dataType, int idx, String str) throws SQLException {
		if ( str == ""){
			pstmt.setNull(idx, dataType); 
	    } else {
	    	pstmt.setString(idx, str); 
	    }
	}
	
	/*
	 * Seta objetos em PreparedStatement verificando se nulo
	 * @param PreparedStatement pstmt - Statement alvo
	 * @param int dataType - um dos valores em java.sql.Types
	 * @param int idx - indice do parametro no sql
	 * @param int num - valor que sera setado no PreparedStatement
	 */
	public void setObject(PreparedStatement pstmt, int dataType, int idx, int num) throws SQLException {
		if (num == 0){
			pstmt.setNull(idx, dataType); 
	    } else {
	    	pstmt.setInt(idx, num); 
	    }
	}	
	
	/*
	 * Seta objetos em PreparedStatement verificando se nulo
	 * @param PreparedStatement pstmt - Statement alvo
	 * @param int dataType - um dos valores em java.sql.Types
	 * @param int idx - indice do parametro no sql
	 * @param Object obj - valor que sera setado no PreparedStatement
	 */
	public void setObject(PreparedStatement pstmt, int dataType, int idx, Object obj) throws SQLException {
		if ( obj == null){
			pstmt.setNull(idx, dataType); 
	    } else {
	    	pstmt.setObject(idx, obj); 
	    }
	}

}
