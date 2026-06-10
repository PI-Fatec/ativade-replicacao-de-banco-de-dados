package br.com.grupo.api;

import br.com.grupo.model.Pedido;
import br.com.grupo.model.Produto;
import br.com.grupo.model.RelatorioVendas;
import br.com.grupo.repository.PedidoRepository;
import br.com.grupo.repository.ProdutoRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class ApiServer {

    private final ProdutoRepository produtoRepo;
    private final PedidoRepository pedidoRepo;

    public ApiServer(ProdutoRepository produtoRepo, PedidoRepository pedidoRepo) {
        this.produtoRepo = produtoRepo;
        this.pedidoRepo = pedidoRepo;
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/pedidos/", new PedidoByIdHandler());
        server.createContext("/clientes/", new ClientePedidosHandler());
        server.createContext("/produtos/baixo-estoque", new ProdutosBaixoEstoqueHandler());
        server.createContext("/relatorios/vendas", new RelatorioVendasHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("\n[API REST] Servidor rodando na porta " + port);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // GET /pedidos/{id}
    class PedidoByIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 3) { // /pedidos/123
                try {
                    int id = Integer.parseInt(parts[2]);
                    Pedido p = pedidoRepo.buscarPedidoPorId(id);
                    if (p != null) {
                        String json = String.format(
                            "{\"id\": %d, \"cliente_id\": %d, \"cliente_nome\": \"%s\", \"valor_total\": %.2f, \"status\": \"%s\"}",
                            p.getId(), p.getClienteId(), p.getCliente().getNome(), p.getValorTotal(), p.getStatus()
                        );
                        sendResponse(exchange, 200, json.replace(',', '.')); // simplificando formatação de ponto flutuante no JSON
                    } else {
                        sendResponse(exchange, 404, "{\"error\": \"Pedido não encontrado\"}");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
                }
            } else {
                sendResponse(exchange, 400, "{\"error\": \"Rota inválida\"}");
            }
        }
    }

    // GET /clientes/{id}/pedidos
    class ClientePedidosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 4 && "pedidos".equals(parts[3])) { // /clientes/123/pedidos
                try {
                    int id = Integer.parseInt(parts[2]);
                    List<Pedido> pedidos = pedidoRepo.buscarHistoricoCliente(id);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < pedidos.size(); i++) {
                        Pedido p = pedidos.get(i);
                        json.append(String.format("{\"id\": %d, \"valor_total\": %.2f, \"status\": \"%s\"}", p.getId(), p.getValorTotal(), p.getStatus()));
                        if (i < pedidos.size() - 1) json.append(", ");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString().replace(',', '.').replace("}. {", "}, {"));
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
                }
            } else {
                sendResponse(exchange, 400, "{\"error\": \"Rota inválida\"}");
            }
        }
    }

    // GET /produtos/baixo-estoque
    class ProdutosBaixoEstoqueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                return;
            }

            List<Produto> produtos = produtoRepo.buscarProdutosBaixoEstoque();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < produtos.size(); i++) {
                Produto p = produtos.get(i);
                json.append(String.format("{\"id\": %d, \"descricao\": \"%s\", \"estoque\": %d, \"valor\": %.2f}", p.getId(), p.getDescricao(), p.getEstoque(), p.getValor()));
                if (i < produtos.size() - 1) json.append(", ");
            }
            json.append("]");
            sendResponse(exchange, 200, json.toString().replace(',', '.').replace("}. {", "}, {"));
        }
    }

    // GET /relatorios/vendas
    class RelatorioVendasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
                return;
            }

            RelatorioVendas r = pedidoRepo.gerarRelatorio();
            String json = String.format(
                "{\"quantidade_total_pedidos\": %d, \"valor_medio_pedidos\": %.2f, \"valor_total_vendido\": %.2f}",
                r.getQuantidadeTotalPedidos(), r.getValorMedioPedidos(), r.getValorTotalVendido()
            );
            sendResponse(exchange, 200, json.replace(',', '.'));
        }
    }
}
