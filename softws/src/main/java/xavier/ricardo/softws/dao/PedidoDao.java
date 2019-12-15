package xavier.ricardo.softws.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Area;
import xavier.ricardo.softws.tipos.Cliente;
import xavier.ricardo.softws.tipos.Contato;
import xavier.ricardo.softws.tipos.Item;
import xavier.ricardo.softws.tipos.Pedido;
import xavier.ricardo.softws.utils.Sql;

public class PedidoDao {

	//http://ricardoxavier.no-ip.org/soft-ws3/softws/pedido/TECNOFLEX/2019-05-10/57/334
	@SuppressWarnings("deprecation")
	public Pedido getPedido(String fornecedor, String data, int codOrcamento, int codPedido) throws NamingException, SQLException {

		Connection bd = BancoDados.conecta();

		// itens 
		String sql = String.format("select COD_AREA, SEQ_ITEM, COD_PRODUTO, SUB_CODIGO, QTD_ITEM, "
				+ "DES_MEDIDAS, VLR_PRECO, DES_PRODUTO, TXT_PRODUTO, COD_ESPECIFICOS "
				+ "from ITENS "
				+ "where COD_FORNECEDOR='%s' and DAT_ORCAMENTO=? and COD_ORCAMENTO=%d "
				+ "order by COD_AREA, SEQ_ITEM",
				fornecedor, codOrcamento);

		int ano = Integer.parseInt(data.substring(0, 4));
		int mes = Integer.parseInt(data.substring(5, 7));
		int dia = Integer.parseInt(data.substring(8, 10));
		Date dt = new Date(ano-1900, mes-1, dia, 0, 0, 0);
		
		PreparedStatement cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(dt.getTime()));

		Pedido pedido = new Pedido();
		pedido.setAreas(new ArrayList<Area>());
		ResultSet cursor = cmd.executeQuery();
		
		if (cursor.next()) {
		
			String codArea = Sql.getString(cursor, "COD_AREA");
			Area area = null;
			for (Area a : pedido.getAreas()) {
				if (a.getCodigo().contentEquals(codArea)) {
					area = a;
					break;
				}
			}
			if (area == null) {
				area = new Area();
				area.setCodigo(codArea);
				area.setItens(new ArrayList<Item>());
				pedido.getAreas().add(area);
			}
			
			Item item = new Item();
			
			int seq = cursor.getInt("SEQ_ITEM");
			String codProduto = Sql.getString(cursor, "COD_PRODUTO");
			String subCodigo = Sql.getString(cursor, "SUB_CODIGO");
			int qtde = cursor.getInt("QTD_ITEM");
			String desMedidas = Sql.getString(cursor, "DES_MEDIDAS");
			double preco = cursor.getDouble("VLR_PRECO");
			String desProduto = Sql.getString(cursor, "DES_PRODUTO");
			String txtProduto = Sql.getString(cursor, "TXT_PRODUTO");
			String codEspecificos = Sql.getString(cursor, "COD_ESPECIFICOS");
			
			item.setSeq(seq);
			item.setCodProduto(codProduto);
			item.setSubCodigo(subCodigo);
			item.setQtde(qtde);
			item.setMedidas(desMedidas);
			item.setPreco(preco);
			item.setDescricao(desProduto);
			item.setTexto(txtProduto);
			item.setCodEspecificos(codEspecificos);
			area.getItens().add(item);
			
		}
		
		cursor.close();
		cmd.close();		
		
		// dados do orçamento
		sql = String.format("select COD_VENDEDOR, COD_CLIENTE "
				+ "from ORCAMENTOS "
				+ "where COD_FORNECEDOR='%s' and DAT_ORCAMENTO=? and COD_ORCAMENTO=%d ",
				fornecedor, codOrcamento);

		cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(dt.getTime()));

		cursor = cmd.executeQuery();
		
		String codCliente = null;
				
		if (cursor.next()) {
		
			String codVendedor = Sql.getString(cursor, "COD_VENDEDOR");
			codCliente = Sql.getString(cursor, "COD_CLIENTE");
			pedido.setVendedor(codVendedor);
			
		}
		
		cursor.close();
		cmd.close();
		
		// dados do pedido
		sql = String.format("select OBSERVACAO "
				+ "from PEDIDOS "
				+ "where COD_FORNECEDOR='%s' and DAT_ORCAMENTO=? and COD_ORCAMENTO=%d and COD_PEDIDO=1",
				fornecedor, codOrcamento);

		cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(dt.getTime()));

		cursor = cmd.executeQuery();
		
		if (cursor.next()) {
		
			String observacao = Sql.getString(cursor, "OBSERVACAO");
			if (observacao != null) {
				pedido.setObservacao(observacao.replace("\r\n", "<br>"));
			}
			
		}
		
		cursor.close();
		cmd.close();
		
		// dados do cliente
		Cliente cliente = null;
		if (codCliente != null) {
			cliente = ClienteDao.get(bd, codCliente);
			pedido.setCliente(cliente);
		}

		if (cliente != null) {
			// contatos do cliente			
			List<Contato> contatos = ClienteDao.getContatos(bd, codCliente);
			cliente.setContatos(contatos);
		}
		
		pedido.setCliente(cliente);
		
		bd.close();
		
		return pedido;

	}
		
}
