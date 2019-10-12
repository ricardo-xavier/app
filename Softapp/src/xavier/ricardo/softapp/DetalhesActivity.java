package xavier.ricardo.softapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import xavier.ricardo.softapp.tasks.PedidoTask;

public class DetalhesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes);
		
		Intent intent = getIntent();
		String fornecedor = intent.getStringExtra("fornecedor");
		String data = intent.getStringExtra("data");
		String orcamento = intent.getStringExtra("orcamento");
		String pedido = intent.getStringExtra("pedido");
		
		new PedidoTask(this, fornecedor, data, orcamento, pedido).execute();
		
	}

	public void resultado(Pedido pedido) {

		StringBuilder html = new StringBuilder("<html><body>");
				
		html.append("<h3>Dados do Pedido</h3>");
				
		html.append("<table border>");

		html.append("<tr><td>Fornecedor</td><td>" + pedido.getFornecedor() + "</td></tr>");
		html.append("<tr><td>Data</td><td>" + pedido.getData() + "</td></tr>");
		html.append("<tr><td>Orçamento</td><td>" + pedido.getCodOrcamento() + "</td></tr>");
		html.append("<tr><td>Pedido</td><td>" + pedido.getCodPedido() + "</td></tr>");

		html.append("</table>");
		
		if (pedido.getObservacao() != null) {
			html.append("<h3>Observações</h3>");
			html.append("<br>" + pedido.getObservacao() + "<br>");
		}
				
		if (pedido.getCliente() != null) {
			html.append("<table border>");
			Cliente cliente = pedido.getCliente();
			html.append("<h3>Dados do Cliente</h3>");
			html.append("<tr><td>Cliente</td><td>" + cliente.getNome() + "</td></tr>");
			html.append("<tr><td>CPF/CNPJ</td><td>" + cliente.getCpfCnpj() + "</td></tr>");
			html.append("<tr><td>Insc. Est.</td><td>" + cliente.getIe() + "</td></tr>");
			html.append("<tr><td>Insc. Mun,</td><td>" + cliente.getIm() + "</td></tr>");
			html.append("<tr><td>Endereço</td><td>" + cliente.getEndereco() + "</td></tr>");
			html.append("<tr><td>Entrega</td><td>" + cliente.getEnderecoEntrega() + "</td></tr>");
			html.append("<tr><td>Fones</td><td>" + cliente.getFone() + "</td></tr>");
			html.append("<tr><td>Email</td><td>" + cliente.getEmail() + "</td></tr>");
			html.append("</table>");
			/*
			html.append("<h4>Contatos</h4>");
			for (Contato contato : cliente.getContatos()) {
				
				html.append("<table border>");
				
				html.append("<tr><td>Nome</td><td>" + contato.getNome() + "</td></tr>");
				html.append("<tr><td>Sub-Código</td><td>" + contato.getPapel() + "</td></tr>");
				html.append("<tr><td>Fones</td><td>" + contato.getFone() + "</td></tr>");
				html.append("<tr><td>Email</td><td>" + contato.getEmail() + "</td></tr>");

				html.append("</table>");
				
			}
			*/
		}
		
		html.append("<h3>Itens</h3>");

		for (Area area : pedido.getAreas()) {
		
			html.append("<h4>Área: " + area.getCodigo() + "</h4>");
		
			for (Item item : area.getItens()) {
			
				html.append("<table border>");
				
				html.append("<tr><td>Código</td><td>" + item.getCodProduto() + "</td></tr>");
				html.append("<tr><td>Sub-Código</td><td>" + item.getSubCodigo() + "</td></tr>");
				html.append("<tr><td>Descrição</td><td>" + item.getDescricao() + "</td></tr>");
				html.append("<tr><td>Quantidade</td><td>" + item.getQtde() + "</td></tr>");
				html.append("<tr><td>Preço</td><td>" + item.getPreco() + "</td></tr>");
				html.append("<tr><td>Medidas</td><td>" + item.getMedidas() + "</td></tr>");
				html.append("<tr><td>Específicos</td><td>" + item.getCodEspecificos() + "</td></tr>");

				html.append("</table>");
				
				/*
				if (item.getTexto() != null) {
					String texto = item.getTexto();
					System.out.println(texto);
					html.append("<br>" + item.getTexto() + "<br>");
				}
				*/
			
			}

		}
				
		html.append("</body></html>");
		
		WebView wvRegras = (WebView) findViewById(R.id.wvDelathes);
		wvRegras.loadData(html.toString(), "text/html", "UTF-8");		
		
	}

}
