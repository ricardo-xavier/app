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

public class PedidoDao {

	//http://ricardoxavier.no-ip.org/soft-ws2/softws/pedido/TECNOFLEX/2019-05-10/57/334
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
		
			String codArea = getString(cursor, "COD_AREA");
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
			String codProduto = getString(cursor, "COD_PRODUTO");
			String subCodigo = getString(cursor, "SUB_CODIGO");
			int qtde = cursor.getInt("QTD_ITEM");
			String desMedidas = getString(cursor, "DES_MEDIDAS");
			double preco = cursor.getDouble("VLR_PRECO");
			String desProduto = getString(cursor, "DES_PRODUTO");
			String txtProduto = getString(cursor, "TXT_PRODUTO");
			String codEspecificos = getString(cursor, "COD_ESPECIFICOS");
			
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
		
			String codVendedor = getString(cursor, "COD_VENDEDOR");
			codCliente = getString(cursor, "COD_CLIENTE");
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
		
			String observacao = getString(cursor, "OBSERVACAO");
			if (observacao != null) {
				pedido.setObservacao(observacao.replace("\r\n", "<br>"));
			}
			
		}
		
		cursor.close();
		cmd.close();
		
		Cliente cliente = null;
		if (codCliente != null) {
			
			// dados do cliente
			sql = String.format("select NOM_PARCEIRO, NRO_CPF_CNPJ, NRO_INSCRICAO_ESTADUAL, NRO_INSCRICAO_MUNICIPAL, "
				+ "DES_LOGRADOURO, NRO_ENDERECO, DES_COMPLEMENTO, NOM_BAIRRO, NOM_CIDADE, COD_ESTADO, NRO_CEP, "
				+ "DES_LOGRADOURO_ENTREGA, NRO_ENDERECO_ENTREGA, DES_COMPLEMENTO_ENTREGA, NOM_BAIRRO_ENTREGA, NOM_CIDADE_ENTREGA, COD_ESTADO_ENTREGA, NRO_CEP_ENTREGA, "
				+ "NRO_FONE1, NRO_FONE2, NRO_CELULAR, DES_EMAIL "
				+ "from PARCEIROS "
				+ "where COD_PARCEIRO='%s'",
				codCliente);

			cmd = bd.prepareStatement(sql);

			cursor = cmd.executeQuery();
		
			if (cursor.next()) {
				
				cliente = new Cliente();
		
				String nomParceiro = getString(cursor, "NOM_PARCEIRO");
				String cpfCnpj = getString(cursor, "NRO_CPF_CNPJ");
				String ie = getString(cursor, "NRO_INSCRICAO_ESTADUAL");
				String im = getString(cursor, "NRO_INSCRICAO_MUNICIPAL");
				String rua = getString(cursor, "DES_LOGRADOURO");
				String numero = getString(cursor, "NRO_ENDERECO");
				String complemento = getString(cursor, "DES_COMPLEMENTO");
				String bairro = getString(cursor, "NOM_BAIRRO");
				String cidade = getString(cursor, "NOM_CIDADE");
				String estado = getString(cursor, "COD_ESTADO");
				String cep = getString(cursor, "NRO_CEP");
				String ruaEntrega = getString(cursor, "DES_LOGRADOURO_ENTREGA");
				String numeroEntrega = getString(cursor, "NRO_ENDERECO_ENTREGA");
				String complementoEntrega = getString(cursor, "DES_COMPLEMENTO_ENTREGA");
				String bairroEntrega = getString(cursor, "NOM_BAIRRO_ENTREGA");
				String cidadeEntrega = getString(cursor, "NOM_CIDADE_ENTREGA");
				String estadoEntrega = getString(cursor, "COD_ESTADO_ENTREGA");
				String cepEntrega = getString(cursor, "NRO_CEP_ENTREGA");
				String fone1 = getString(cursor, "NRO_FONE1");
				String fone2 = getString(cursor, "NRO_FONE2");
				String celular = getString(cursor, "NRO_CELULAR");
				String email = getString(cursor, "DES_EMAIL");
				
				cliente.setNome(nomParceiro);
				cliente.setCpfCnpj(cpfCnpj);
				cliente.setIe(ie);
				cliente.setIm(im);
				cliente.setEndereco(rua + " - " + numero + " - " + complemento 
						+ " - " + bairro + " - " + cidade + " - " + estado + " - " + cep);
				cliente.setEnderecoEntrega(ruaEntrega + " - " + numeroEntrega + " - " + complementoEntrega 
						+ " - " + bairroEntrega + " - " + cidadeEntrega + " - " + estadoEntrega + " - " + cepEntrega);
				cliente.setFone(fone1 + " - " + fone2 + " - " + celular);
				cliente.setEmail(email);
				
				pedido.setCliente(cliente);
			
			}
		
			cursor.close();
			cmd.close();
		}

		if (cliente != null) {

			// contatos do cliente			
			List<Contato> contatos = new ArrayList<Contato>(); 
			
			sql = String.format("select NOM_CONTATO, DES_PAPEL, "
				+ "NRO_FONE1, NRO_FONE2, NRO_CELULAR, DES_EMAIL "
				+ "from CONTATOS "
				+ "where COD_PARCEIRO='%s'",
				codCliente);

			cmd = bd.prepareStatement(sql);

			cursor = cmd.executeQuery();
		
			while (cursor.next()) {
				
				Contato contato = new Contato();
		
				String nome = getString(cursor, "NOM_CONTATO");
				String papel = getString(cursor, "DES_PAPEL");
				String fone1 = getString(cursor, "NRO_FONE1");
				String fone2 = getString(cursor, "NRO_FONE2");
				String celular = getString(cursor, "NRO_CELULAR");
				String email = getString(cursor, "DES_EMAIL");
				
				contato.setNome(nome);
				contato.setPapel(papel);
				contato.setFone(fone1 + " - " + fone2 + " - " + celular);
				contato.setEmail(email);
				
				contatos.add(contato);
			
			}
			
			cliente.setContatos(contatos);
		
			cursor.close();
			cmd.close();
		}
		
		pedido.setCliente(cliente);
		
		bd.close();
		
		return pedido;

	}

	private String getString(ResultSet cursor, String nome) throws SQLException {
		String s = cursor.getString(nome);
		return s != null ? s : "";
	}
		
}
