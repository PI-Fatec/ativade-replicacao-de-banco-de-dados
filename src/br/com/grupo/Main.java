package br.com.grupo;

import br.com.grupo.db.DatabaseManager;
import br.com.grupo.repository.ClienteRepository;
import br.com.grupo.repository.PedidoItemRepository;
import br.com.grupo.repository.PedidoRepository;
import br.com.grupo.repository.ProdutoRepository;
import br.com.grupo.service.DemoService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Inicializando Sistema de Replicação de Banco de Dados...");

        try {
            DatabaseManager dbManager = new DatabaseManager();
            ClienteRepository clienteRepo = new ClienteRepository(dbManager);
            ProdutoRepository produtoRepo = new ProdutoRepository(dbManager);
            PedidoRepository pedidoRepo = new PedidoRepository(dbManager);
            PedidoItemRepository pedidoItemRepo = new PedidoItemRepository(dbManager);

            DemoService demoService = new DemoService(clienteRepo, produtoRepo, pedidoRepo, pedidoItemRepo);

            // Executar demonstração
            demoService.executarDemonstracao();
            
        } catch (Exception e) {
            System.err.println("Erro crítico na execução: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
