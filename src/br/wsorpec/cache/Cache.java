package br.wsorpec.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

import br.wsorpec.acessoBanco.Conexao;
import br.wsorpec.log.Log;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.ObjetoWait;

public class Cache extends Conexao{

	public Cache(){}
	
	public String cacheConsulta(String str, String protocolo, Log log){
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlCache = new StringBuffer();
		ObjetoWait wait = new ObjetoWait();
		PreparedStatement psCache = null;
		Connection conn = null;
		String resp = "";
		String hash = "";
		String cons = "";
		int minuto  = GlobalConstants.TEMPOCACH; //Tempo do cache em minuto.
		int i = 0;
		int l = 0;
		int sai = 0;
		int apaga;

		while (sai==0) {
			try {
				cons  = str;
				apaga = 0;
				hash = stringHexa(gerarHash(cons, "SHA-256"));

				conn = this.getConnection();

				sql.append(" SELECT data, resposta, tamanhRespOriginal, protocolo FROM Cache\n");
				sql.append(" WHERE hash = ?													\n");

				ps = conn.prepareStatement(sql.toString());
				ps.setString(1, hash);

				rs = ps.executeQuery();
				if(rs.next()){
					while (true) {
						if(rs.getString("resposta").trim().length()>0){
							if(tempoCache(rs.getTimestamp("data")) <= (minuto * 60)) {
								log.registro(rs.getString("protocolo").trim(), "RCV-CACHE", str);
								resp = descompacta(rs.getString("resposta"), rs.getInt("tamanhRespOriginal"));
								log.registro(rs.getString("protocolo").trim(), "SND-CACHE", resp);
								protocolo = rs.getString("protocolo").trim();
							}else{
								log.registro(rs.getString("protocolo").trim(), "INF", "Apaga cache, tempo expirado.");
								ps = null;
								ps = conn.prepareStatement("DELETE FROM Cache WHERE hash = '"+hash+"'");
								ps.execute();
								try {
									sql = new StringBuffer();
									this.closeResultSet(rs);
									this.closeStatement(ps);
									this.closeConnection(conn);
								} catch (Exception e1) {
									log.registro(rs.getString("protocolo").trim(), "ERRO", e1.getMessage());
								}
								apaga = 1;
							}
							break;
						}else{
							if(l==5){
								sai = 1;
								break; 
							}else{
								l++;								
								log.registro(rs.getString("protocolo").trim(), "INF", "Aguardando 2 segundos.");
								wait.doWait(2 * 1000); //Aguarda 2 segundos								
							}
							closeResultSet(rs);
							closeStatement(ps);
							ps = conn.prepareStatement(sql.toString());
							ps.setString(1, hash);
							rs = ps.executeQuery();
							rs.next();
						}
					}
				}else{
					sqlCache.append(" INSERT INTO Cache (protocolo, data, hash, resposta)\n");
					sqlCache.append(" VALUES ( ?, CURRENT_TIMESTAMP, ?, ?)				 \n");

					psCache = conn.prepareStatement(sqlCache.toString());

					this.setObject(psCache, Types.VARCHAR, ++i, protocolo);
					this.setObject(psCache, Types.VARCHAR, ++i, hash);
					this.setObject(psCache, Types.VARCHAR, ++i, " ");

					psCache.execute();
				}
				if (apaga==1){
					continue;
				}
			} catch (SQLException e) {
				try {
					log.registro(protocolo, "ERRO", e.getMessage());
				} catch (Exception e1) {}
				if(e.getMessage().indexOf("duplicate key violates")>0){
					this.closeResultSet(rs);
					this.closeStatement(ps);
					this.closeConnection(conn);
					sql = new StringBuffer();
					continue;
				}
				sai = 1;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					this.closeResultSet(rs);
					this.closeStatement(ps);
					this.closeStatement(psCache);
					this.closeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sai = 1;
		}
		return resp;
	}

	public void cacheResposta(String str, String protocolo){
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sqlCache = new StringBuffer();
		Connection conn = null;
		int i = 0;

		try {
			conn = this.getConnection();

			sqlCache.append("UPDATE Cache SET resposta = CONVERT(VARCHAR(8000), ?),	\n");
			sqlCache.append("				  tamanhRespOriginal = ? 				\n");
			sqlCache.append(" WHERE protocolo = ?		    					   	\n");

			ps = conn.prepareStatement(sqlCache.toString());
			
			this.setObject(ps, Types.VARCHAR, ++i, compacta(str, str.length()+200));
			this.setObject(ps, Types.INTEGER, ++i, str.length()+200);
			this.setObject(ps, Types.VARCHAR, ++i, protocolo);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();;
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void apagaCache(String protocolo){
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sqlCache = new StringBuffer();
		Connection conn = null;
		int i = 0;

		try {
			conn = this.getConnection();

			sqlCache.append("DELETE FROM Cache WHERE protocolo = ? \n");

			ps = conn.prepareStatement(sqlCache.toString());
			
			this.setObject(ps, Types.VARCHAR, ++i, protocolo);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();;
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] gerarHash(String frase, String algoritmo) {
		// MD5 		Tamanho 33
		// SHA-1	Tamanho 41
		// SHA-256	Tamanho 65tempoCache(Date dataBanco)
		try {
			MessageDigest md = MessageDigest.getInstance(algoritmo);
			md.update(frase.getBytes());
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private static String stringHexa(byte[] bytes) {
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
			int parteBaixa = bytes[i] & 0xf;
			if (parteAlta == 0)
				s.append('0');
			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}
		return s.toString();
	}
	
	public int tempoCache(Timestamp timestamp){
		 //long diferencaMin   = diferenca/(60*1000);		//DIFERENCA EM MINUTOS 
		 //long diferencaHoras = diferenca/(60*60*1000);	//DIFERENCA EM HORAS
		Long dif;
		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(timestamp);
		
		dif = Long.valueOf((System.currentTimeMillis()-dataInicial.getTimeInMillis())); 
		
		return (dif.intValue()/1000);
	}
	
    public static String compacta(String expressao, int tamanho) {
        //String -&gt; byte[] -&gt; compressão -&gt; byte[] -&gt; Base-64 -&gt; String
        Deflater compresser = new Deflater();
        byte[] input = expressao.getBytes();
        byte[] output = new byte[tamanho];
        compresser.setInput(input);
        compresser.finish();
        int quant = compresser.deflate(output);
        int tamanhoArray = 0;
        if ((quant * 8) % 24 != 0) {
            tamanhoArray = (((quant * 8) / 24) + 1) * 4;
        } else {
            tamanhoArray = ((quant * 8) / 24) * 4;
        }
        return new String(Base64.encodeBase64(output)).substring(0, tamanhoArray);
    }
    
    public static String descompacta(String expressao, int tamanho) {
        //String -&gt; Base64 -&gt; byte[] -&gt; descompressão -&gt; byte[] -&gt; String
        try {
            byte output[] = Base64.decodeBase64(expressao.getBytes());
            Inflater decompresser = new Inflater();
            decompresser.setInput(output);
            byte[] result = new byte[tamanho];
            decompresser.inflate(result);
            decompresser.end();
            return new String(result).trim();
        } catch (java.util.zip.DataFormatException ex) {
            ex.printStackTrace();
            return "";
		}
    }
	
}
