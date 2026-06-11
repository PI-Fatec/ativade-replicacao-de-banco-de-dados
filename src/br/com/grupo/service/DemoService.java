package br.com.grupo.service;

import br.com.grupo.model.*;
import br.com.grupo.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DemoService {
    private final ClienteRepository clienteRepo;
    private final ProdutoRepository produtoRepo;
    private final PedidoRepository pedidoRepo;
    private final PedidoItemRepository pedidoItemRepo;

    public DemoService(ClienteRepository clienteRepo, ProdutoRepository produtoRepo, PedidoRepository pedidoRepo, PedidoItemRepository pedidoItemRepo) {
        this.clienteRepo = clienteRepo;
        this.produtoRepo = produtoRepo;
        this.pedidoRepo = pedidoRepo;
        this.pedidoItemRepo = pedidoItemRepo;
    }

    public void executarDemonstracao() {
        System.out.println("\n--- INICIANDO DEMONSTRAÇÃO ---");

        // 1. Cadastro de Clientes
        for (int i = 1; i <= 3; i++) {
            String nome = "Cliente " + i + " " + UUID.randomUUID().toString().substring(0, 5);
            String email = "cliente" + i + "_" + UUID.randomUUID().toString().substring(0, 5) + "@email.com";

            long id = clienteRepo.criarCliente(nome, email);
            System.out.println("[WRITE] Cliente criado: ID=" + id + " | Nome=" + nome + " | Email=" + email);
        }

        // 2. Cadastro de Produtos
        String[] nomesProdutos = {"Notebook", "Mouse", "Monitor", "Teclado"};
        BigDecimal[] valoresProdutos = {new BigDecimal("3500.00"), new BigDecimal("150.00"), new BigDecimal("1200.00"), new BigDecimal("300.00")};
        int[] estoquesProdutos = {10, 50, 15, 30};

        for (int i = 0; i < nomesProdutos.length; i++) {
            long id = produtoRepo.criarProduto(nomesProdutos[i], "Eletrônicos", valoresProdutos[i], estoquesProdutos[i]);
            System.out.println("[WRITE] Produto criado: ID=" + id + " | Descrição=" + nomesProdutos[i] + " | Valor=" + valoresProdutos[i] + " | Estoque=" + estoquesProdutos[i]);
        }

        // 3. Loop contínuo de criação de pedidos
        int ciclo = 1;
        while (true) {
            System.out.println("\n========== CICLO " + ciclo + " ==========");

            Cliente cliente = clienteRepo.buscarClienteAleatorio();
            if (cliente == null) {
                System.out.println("[WARN] Nenhum cliente encontrado. Aguardando...");
            } else {
                Random random = new Random();
                int qtdProdutos = random.nextInt(3) + 1; // de 1 a 3 produtos
                List<Produto> produtosSelecionados = produtoRepo.buscarProdutosAleatorios(qtdProdutos);

                if (!produtosSelecionados.isEmpty()) {
                    BigDecimal valorTotalPedido = BigDecimal.ZERO;
                    for (Produto p : produtosSelecionados) {
                        valorTotalPedido = valorTotalPedido.add(p.getValor());
                    }

                    long pedidoId = pedidoRepo.criarPedido(cliente.getId(), valorTotalPedido, "FINALIZADO");
                    System.out.println("\n[WRITE] Pedido criado: ID=" + pedidoId + " | Cliente=" + cliente.getNome() + " | Valor Total=" + valorTotalPedido + " | Quantidade de Itens=" + produtosSelecionados.size());

                    for (Produto p : produtosSelecionados) {
                        pedidoItemRepo.criarPedidoItem((int) pedidoId, p.getId(), 1, p.getValor());
                        System.out.println("[WRITE] PedidoItem criado para Pedido " + pedidoId + ": Produto=" + p.getDescricao());
                    }

                    demonstrarConsultasNaReplica((int) pedidoId, cliente.getId());
                }
            }

            ciclo++;
            try {
                Thread.sleep(3000); // aguarda 3 segundos entre ciclos
            } catch (InterruptedException e) {
                System.out.println("Demonstração interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void demonstrarConsultasNaReplica(int pedidoId, int clienteId) {
        System.out.println("\n--- CONSULTAS NA RÉPLICA ---");

        // Buscar Pedido por ID
        Pedido pedido = pedidoRepo.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.println("\n[READ] Buscar pedido por ID:");
            System.out.println("Pedido " + pedido.getId());
            System.out.println("Cliente " + pedido.getCliente().getNome());
            System.out.println("Valor Total " + pedido.getValorTotal());
            System.out.println("Status " + pedido.getStatus());
        }

        // Buscar Itens do Pedido
        List<PedidoItem> itens = pedidoItemRepo.buscarItensPedido(pedidoId);
        System.out.println("\n[READ] Buscar itens do pedido:");
        for (PedidoItem item : itens) {
            System.out.println("PedidoItem " + item.getId() + " | Produto " + item.getProduto().getDescricao() + " | Quantidade " + item.getQuantidade());
        }

        // Histórico do Cliente
        List<Pedido> historico = pedidoRepo.buscarHistoricoCliente(clienteId);
        System.out.println("\n[READ] Histórico do cliente:");
        for (Pedido p : historico) {
            System.out.println("Pedido " + p.getId() + " - R$ " + p.getValorTotal());
        }

        // Relatório Agregado
        RelatorioVendas relatorio = pedidoRepo.gerarRelatorio();
        System.out.println("\n[READ] Relatório agregado:");
        System.out.println("Quantidade Total de Pedidos: " + relatorio.getQuantidadeTotalPedidos());
        System.out.println("Valor Médio dos Pedidos: R$ " + relatorio.getValorMedioPedidos());
        System.out.println("Valor Total Vendido: R$ " + relatorio.getValorTotalVendido());
    }
}