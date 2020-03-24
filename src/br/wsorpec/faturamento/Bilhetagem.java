package br.wsorpec.faturamento;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.wsorpec.acessoBanco.Conexao;
import br.wsorpec.log.Log;
import br.wsorpec.util.Formatador;
import br.wsorpec.util.GlobalConstants;
import br.wsorpec.util.ParteFixaV7;
import br.wsorpec.util.Util;
import br.wsorpec.wscdlrio.negativacao.Baixar;
import br.wsorpec.wscdlrio.negativacao.Incluir;
import br.wsorpec.wscdlrio.negativacao.Listar;
import br.wsorpec.wscdlrio.negativacao.Listar.ListaDocumentos;

public class Bilhetagem extends Conexao {
	
	public void bilhetar(int codigo, String documento, String produto, String ipRemoto, String protocolo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer();
		Log log = new Log(false, GlobalConstants.PASTALOG);
		int i = 0;
		try {			
			conn = this.getConnection();
			
			sql.append("INSERT INTO Bilhetagem																  \n");
			sql.append("(idBilhetagemPai, idCliente, idProduto, DataConsulta, Documento, Bilhetar, IPCliente) \n");
			sql.append("VALUES(NULL, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?); 									  \n");

			ps = conn.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

			this.setObject(ps, Types.INTEGER, ++i, codigo);
			this.setObject(ps, Types.INTEGER, ++i, Integer.parseInt(produto));
			this.setObject(ps, Types.VARCHAR, ++i, Formatador.unformatString(documento));			
			this.setObject(ps, Types.INTEGER, ++i, 1);
			this.setObject(ps, Types.VARCHAR, ++i, ipRemoto);
			
	        if (ps.executeUpdate() > 0) {
	            rs = ps.getGeneratedKeys();
	            if ( rs.next() ) {
	            	GlobalConstants.idPai = + rs.getInt(1);
	            }
	        }
		} catch (Exception e) {
			log.registro(protocolo, "ERRO-BILHETAGEM", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-BILHETAGEM", e.getMessage());
			}
		}
	}	

	public void bilhetarOpcionais(int idPai, int codigo, String documento, String produto, String ipRemoto, String protocolo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer();
		Log log = new Log(false, GlobalConstants.PASTALOG);
		int i = 0;
		try {		
			conn = this.getConnection();

			sql.append("INSERT INTO Bilhetagem																  \n");
			sql.append("(idBilhetagemPai, idCliente, idProduto, DataConsulta, Documento, Bilhetar, IPCliente) \n");
			sql.append("VALUES(?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?); 									  	  \n");

			ps = conn.prepareStatement(sql.toString());

			this.setObject(ps, Types.INTEGER, ++i, idPai);			
			this.setObject(ps, Types.INTEGER, ++i, codigo);
			this.setObject(ps, Types.INTEGER, ++i, Integer.parseInt(produto));
			this.setObject(ps, Types.VARCHAR, ++i, Formatador.unformatString(documento));			
			this.setObject(ps, Types.INTEGER, ++i, 1);
			this.setObject(ps, Types.VARCHAR, ++i, ipRemoto);
			
			ps.execute();

		} catch (Exception e) {
			log.registro(protocolo, "ERRO-BILHETAGEM OPCIONAL", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-BILHETAGEM OPCIONAL", e.getMessage());
			}
		}
	}	
	
	public boolean verificaInclusao(Incluir inc, String protocolo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		Log log = new Log(false, GlobalConstants.PASTALOG);

		StringBuffer sql = new StringBuffer();
		boolean retorno = false;		
		int i = 0;
		try {		
			conn = this.getConnection();
			
			sql.append("SELECT Count(*)  as Contador FROM viStatusNegativacao	\n");
			sql.append(" WHERE idcliente = ?									\n");
			sql.append("   AND NumeroDocumento = ?								\n");
			sql.append("   AND Contrato = ?										\n");
			sql.append("   AND Databaixa IS NULL								\n");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++i, String.valueOf(GlobalConstants.idCliente));
			ps.setString(++i, inc.getDocumento());
			ps.setString(++i, inc.getDadosContrato().getContrato());
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				if(rs.getInt("Contador")==0) {
					retorno = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.registro(protocolo, "ERRO-VERIFICA-INCLUSAO", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-VERIFICA-INCLUSAO", e.getMessage());
			}
		}
		
		return retorno;
	}
	
	public void gravarInclusao(Incluir inc, String protocolo) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		Log log = new Log(false, GlobalConstants.PASTALOG);
		ParteFixaV7 pf = new ParteFixaV7();
		StringBuffer sql = new StringBuffer();		
		String valor = inc.getDadosContrato().getValorParcela();
		valor = valor.replace(".", "");
		valor = valor.replace(",",".");
		String transacao = (inc.getTipoPessoa().equals("F")?"SPCSINC":"SIASINC");

		try {		
			conn = this.getConnection();
			
			sql.append("EXEC [dbo].[SpInsertNegativacao]"+
					   " @idcliente = "+Integer.parseInt(inc.getCodigo())+","+
					   " @CodTransacao = N'"+transacao+"',"+
					   " @layout = "+Integer.parseInt(pf.getVersao())+","+
					   " @Entidade = N'"+GlobalConstants.entidade.trim()+"',"+
					   " @Firma = N'" +GlobalConstants.firma.trim()+"',"+
					   " @SenhaOrigem = N'"+ GlobalConstants.senha.trim()+"',"+
					   " @PracaCredito = N'"+pf.getPraca().trim()+"',"+
					   " @ViaAcesso = N'"+pf.getVia().trim()+"',"+
					   " @Terminal = N'"+pf.getTerminal().trim()+"',"+
					   " @Contrato = N'"+inc.getDadosContrato().getContrato()+"',"+
					   " @NatOperacao = N'"+inc.getDadosContrato().getNaturezaOperacao()+"',"+
					   " @DataAtraso = N'"+Formatador.parseDate(inc.getDadosContrato().getDataAtTraso())+"',"+ 
					   " @DataTermino = N'"+Formatador.parseDate(inc.getDadosContrato().getDataTermino())+"',"+
					   " @ValorParcela = "+valor+","+
					   " @Quantidade = "+Integer.parseInt(inc.getDadosContrato().getQuantidade())+","+ 
					   " @Moeda = N'"+inc.getDadosContrato().getMoeda()+"',"+
					   " @TipoDevedor = N'"+inc.getDadosDevedor().getTipoDevedor()+"',"+
					   " @Nome = N'"+inc.getDadosDevedor().getNome()+"',"+
					   " @DataNasc = N'"/*+Formatador.parseDate(inc.getDadosDevedor().getDataNascimento())*/+"',"+
					   " @Mae = N'"+inc.getDadosDevedor().getFiliaçãoMae()+"',"+ 
					   " @Sexo = "+1/*Integer.parseInt(inc.getDadosDevedor().getSexo())*/+","+ 
					   " @EstadoCivil = "+1/*Integer.parseInt(inc.getDadosDevedor().getEstadoCivil())*/+","+
					   " @Naturalidade = N'"+inc.getDadosDevedor().getNaturalidade()+"',"+ 
					   " @CPF = N'"+inc.getDadosDevedor().getCpf()+"',"+
					   " @NumeroDocumento = N'"+inc.getDadosDocumento().getNumero()+"',"+ 
 					   " @TipoDocumento = N'"+inc.getDadosDocumento().getTipo()+"',"+
 					   " @Estado = N'"+ inc.getDadosDocumento().getEstado()+"',"+
 					   " @Logradouro = N'"+inc.getDadosEndereco().getLogradouro()+"',"+ 
 					   " @Bairro = N'"+inc.getDadosEndereco().getBairro()+"',"+  
 					   " @Municipio = N'"+inc.getDadosEndereco().getMunicipio()+"',"+ 
					   " @UF = N'"+inc.getDadosEndereco().getEstado()+"',"+ 
					   " @CEP = N'"+inc.getDadosEndereco().getCep()+"',"+ 
					   " @TipoEndereco = "+1/*Integer.parseInt(inc.getDadosEndereco().getTipo())*/+","+ 
					   " @DDDFone1 = N'"+inc.getDadosTelefone().getDdd1()+"',"+ 
					   " @NumeroFone1 = N'"+inc.getDadosTelefone().getNumero1()+"',"+ 
					   " @Ramal1 = N'"+inc.getDadosTelefone().getRamal1()+"',"+ 
					   " @TipoFone1 = "+1/*Integer.parseInt(inc.getDadosTelefone().getTipo1())*/+","+ 
					   " @DDDFone2 = N'"+inc.getDadosTelefone().getDdd2()+"',"+  
					   " @NumeroFone2 = N'"+inc.getDadosTelefone().getNumero1()+"',"+
					   " @Ramal2 = N'"+inc.getDadosTelefone().getRamal2()+"',"+
					   " @TipoFone2 = "+1/*Integer.parseInt(inc.getDadosTelefone().getTipo1())*/+","+  
					   " @Observacao = N'null"/*inc.getObservacao().trim()*/+"',"+ 
					   " @DataInclusao = N'"+Formatador.dataHoje()+"',"+ 
					   " @ProtocoloInclusao = N'"+protocolo+"',"+
					   " @ProtocoloCDLRIO = N'"+GlobalConstants.protocoloCDLRio+"'");
						
			stmt = conn.prepareCall(sql.toString());
			stmt.execute();
		} catch (Exception e) {
			log.registro(protocolo, "ERRO-GRAVAR NEGATIVACAO-INCLUSAO", e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(stmt);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-GRAVAR NEGATIVACAO-INCLUSAO", e.getMessage());
			}
		}

	}
	
	public void gravarBaixa(Baixar bai, String protocolo) {
		CallableStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		Log log = new Log(false, GlobalConstants.PASTALOG);
		ParteFixaV7 pf = new ParteFixaV7();
		StringBuffer sql = new StringBuffer();		
		String transacao = (bai.getTipoPessoa().equals("F")?"SPCSBXC":"SIASBAX");
		
		try {		
			conn = this.getConnection();
			
			sql.append("EXEC [dbo].[SpInsertBaixasNegativacao]"+
					   " @CodTransacao = N'"+transacao+"',"+
					   " @layout = "+Integer.parseInt(pf.getVersao())+","+
					   " @Entidade = N'"+GlobalConstants.entidade.trim()+"',"+ 
					   " @Firma = N'" +GlobalConstants.firma.trim()+"',"+
					   " @SenhaOrigem = N'"+GlobalConstants.senha.trim()+"',"+
					   " @PracaCredito = N'"+pf.getPraca().trim()+"',"+ 
					   " @ViaAcesso = N'"+pf.getVia().trim()+"',"+ 
					   " @Terminal = N'"+pf.getTerminal().trim()+"',"+
					   " @Contrato = N'"+bai.getDadosBaixa().getContrato()+"',"+
					   " @motivo = N'"+bai.getDadosBaixa().getMotivo()+"',"+
					   " @Nome = N'"+bai.getDadosBaixa().getNome()+"',"+
					   " @DataNasc = N'"/*+Formatador.parseDate(bai.getDadosBaixa().getDataNascimento())*/+"',"+
					   " @CPF = N'"+bai.getDadosBaixa().getCpf()+"',"+
					   " @NumeroDocumento = N'"+bai.getDadosBaixa().getDocumento()+"',"+ 
 					   " @TipoDocumento = N'"+bai.getDadosBaixa().getTipoDocumento()+"',"+
 					   " @Estado = N'"+ bai.getDadosBaixa().getEstado()+"',"+
					   " @ReservadoUsuario  = N'',"+
					   " @DataBaixa = N'"+Formatador.dataHoje()+"',"+ 
					   " @ProtocoloBaixa = N'"+protocolo+"',"+
					   " @IdNegativacao = "+bai.getIdNegativacao()+","+
					   " @ProtocoloCDLRIO = N'"+GlobalConstants.protocoloCDLRio+"'");
					
			stmt = conn.prepareCall(sql.toString());
			stmt.execute();
			
		} catch (Exception e) {
			log.registro(protocolo, "ERRO-GRAVAR NEGATIVACAO-BAIXA", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(stmt);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-GRAVAR NEGATIVACAO-BAIXA", e.getMessage());
			}
		}
	}

	public void consultarLista(Listar listar, String protocolo) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		ListaDocumentos listaDoc = new ListaDocumentos();
		Log log = new Log(false, GlobalConstants.PASTALOG);
		StringBuffer sql = new StringBuffer();
		List<ListaDocumentos> itens = new ArrayList<>();
		int i = 0;
		try {		
			conn = this.getConnection();
			
			sql.append("SELECT ne.id, databaixa, ne.Contrato, ne.NatOperacao, ne.DataAtraso, ne.DataTermino, ne.ValorParcela, ne.Quantidade, ne.Moeda " + 
						", ne.TipoDevedor, ne.Nome, ne.DataNasc, ne.Mae, ne.Sexo, ne.EstadoCivil, ne.Naturalidade, ne.CPF " + 
						", ne.NumeroDocumento, ne.TipoDocumento, ne.Estado, ne.Logradouro, ne.Bairro, ne.Municipio, ne.UF, ne.CEP " + 
						", ne.TipoEndereco, ne.DDDFone1, ne.NumeroFone1, ne.Ramal1, ne.TipoFone1, ne.DDDFone2, ne.NumeroFone2 " + 
						", ne.Ramal2, ne.TipoFone2, ne.ObservacaoDevedor, ne.DataInclusao, ne.ProtocoloInclusao " + 
						"FROM  dbo.Negativacao NE left join dbo.BaixasNegativacao BX on bx.idnegativacao = ne.id " + 
						"WHERE idcliente = ?");
			if(listar.getListaDoc().getDadosDocumento().getTipo().equals("CPF") ||
			   listar.getListaDoc().getDadosDocumento().getTipo().equals("CNPJ")) {
				sql.append("  AND ne.TipoDocumento = ?");
			}
			if(!Util.Empty(listar.getDocumento())) {
				sql.append("  AND ne.NumeroDocumento = ?");								
			}
			if(listar.getStatus().equals("A")) {
				sql.append("  AND databaixa IS NULL ");
			}
			if(listar.getStatus().equals("B")) {
				sql.append("  AND databaixa IS NOT NULL ");
			}
			if(!Util.Empty(listar.getDataInicio()) && !Util.Empty(listar.getDataFim())){
				sql.append("  AND ne.DataInclusao >= ? " +
						   "  AND ne.DataInclusao <= ?");
			}
			sql.append("  ORDER BY ne.DataInclusao DESC");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(++i, Integer.parseInt(listar.getCodigo()));
			if(listar.getListaDoc().getDadosDocumento().getTipo().equals("CPF") ||
			   listar.getListaDoc().getDadosDocumento().getTipo().equals("CNPJ")) {
				ps.setString(++i, listar.getListaDoc().getDadosDocumento().getTipo());				
			}
			if(!Util.Empty(listar.getDocumento())) {
				ps.setString(++i, listar.getDocumento());								
			}
			if(!Util.Empty(listar.getDataInicio()) && !Util.Empty(listar.getDataFim())){			
				ps.setDate(++i, Formatador.parseDate(listar.getDataInicio()));
				ps.setDate(++i, Formatador.parseDate(listar.getDataFim()));
			}
			rs = ps.executeQuery();
			
			if (rs.next()) {
				itens = new ArrayList<ListaDocumentos>();
				
				do {
					listaDoc = new ListaDocumentos();

					listaDoc.setIdNegativacao(rs.getInt("id"));
					if (rs.getDate("databaixa") != null) {
						listaDoc.setDataBaixa(Formatador.formatarData(rs.getDate("databaixa")));
					} else {
						listaDoc.setDataBaixa("null");
					}
					listaDoc.setDataInclusao(Formatador.formatarData(rs.getDate("DataInclusao")));
					listaDoc.getDadosContrato().setContrato(rs.getString("contrato"));
					listaDoc.getDadosContrato().setNaturezaOperacao(rs.getString("NatOperacao"));
					listaDoc.getDadosContrato().setDataAtTraso(Formatador.formatarData(rs.getDate("DataAtraso")));
					listaDoc.getDadosContrato().setDataTermino(Formatador.formatarData(rs.getDate("DataTermino")));
					listaDoc.getDadosContrato().setValorParcela(Formatador.getFormatarValor(rs.getDouble("ValorParcela")));
					listaDoc.getDadosContrato().setQuantidade(Integer.toString(rs.getInt("Quantidade")));
					listaDoc.getDadosContrato().setMoeda(rs.getString("Moeda"));
					listaDoc.getDadosDevedor().setTipoDevedor(rs.getString("TipoDevedor"));
					listaDoc.getDadosDevedor().setNome(rs.getString("Nome"));
					listaDoc.getDadosDevedor().setDataNascimento(Formatador.formatarData(rs.getDate("DataNasc")));
					listaDoc.getDadosDevedor().setFiliacaoMae(rs.getString("Mae"));
					listaDoc.getDadosDevedor().setSexo(Integer.toString(rs.getInt("Sexo")));
					listaDoc.getDadosDevedor().setEstadoCivil(rs.getBigDecimal("EstadoCivil").toString());
					listaDoc.getDadosDevedor().setNaturalidade(rs.getString("Naturalidade"));
					listaDoc.getDadosDevedor().setCpf(rs.getString("CPF").toString());
					listaDoc.getDadosDocumento().setNumero(rs.getString("NumeroDocumento"));
					listaDoc.getDadosDocumento().setTipo(rs.getString("TipoDocumento"));
					listaDoc.getDadosDocumento().setEstado(rs.getString("Estado"));
					listaDoc.getDadosEndereco().setLogradouro(rs.getString("Logradouro"));
					listaDoc.getDadosEndereco().setBairro(rs.getString("Bairro"));
					listaDoc.getDadosEndereco().setMunicipio(rs.getString("Municipio"));
					listaDoc.getDadosEndereco().setEstado(rs.getString("UF"));
					listaDoc.getDadosEndereco().setCep(rs.getString("CEP"));
					listaDoc.getDadosEndereco().setTipo(Integer.toString(rs.getInt("TipoEndereco")));
					listaDoc.getDadosTelefone().setDdd1(rs.getString("DDDFone1"));
					listaDoc.getDadosTelefone().setNumero1(rs.getString("NumeroFone1"));
					listaDoc.getDadosTelefone().setRamal1(rs.getString("Ramal1"));
					listaDoc.getDadosTelefone().setTipo1(rs.getBigDecimal("TipoFone1").toString());
					listaDoc.getDadosTelefone().setDdd2(rs.getString("DDDFone2"));
					listaDoc.getDadosTelefone().setNumero2(rs.getString("NumeroFone2"));
					listaDoc.getDadosTelefone().setRamal2(rs.getString("Ramal2"));
					listaDoc.getDadosTelefone().setTipo2(rs.getBigDecimal("TipoFone2").toString());
					
					itens.add(listaDoc);
				} while (rs.next());
				
				listar.setListagem(itens);
				listar.setCodigoRetorno("1");
				listar.setMensagemRetorno("Lista realizada com sucesso");				
			}else {
				listar.setCodigoRetorno("0");
				listar.setMensagemRetorno("Nenhuma informacao encontrada");				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.registro(protocolo, "ERRO-NEGATIVACAO-LISTAR", e.getMessage());
		} finally {
			try {
				this.closeResultSet(rs);
				this.closeStatement(ps);
				this.closeConnection(conn);
			} catch (Exception e) {
				log.registro(protocolo, "ERRO-NEGATIVACAO-LISTAR", e.getMessage());
			}
		}
	}
	
}
