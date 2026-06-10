package br.com.grupo.repository;

import br.com.grupo.config.AppConfig;
import br.com.grupo.db.DatabaseManager;
import br.com.grupo.model.Cliente;
import br.com.grupo.model.Pedido;
import br.com.grupo.model.RelatorioVendas;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {
    private final DatabaseManager dbManager;

    public PedidoRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public long criarPedido(int clienteId, BigDecimal valorTotal, String status) {
        String sql = "INSERT INTO pedido (cliente_id, valor_total, status, criado_por) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getWriteConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, clienteId);
            stmt.setBigDecimal(2, valorTotal);
            stmt.setString(3, status);
            stmt.setString(4, AppConfig.GROUP_NAME);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar pedido: " + e.getMessage());
        }
        return -1;
    }

    public Pedido buscarPedidoPorId(int id) {
        String sql = "SELECT p.*, c.nome as cliente_nome, c.email as cliente_email " +
                     "FROM pedido p " +
                     "JOIN cliente c ON p.cliente_id = c.id " +
                     "WHERE p.id = ?";
                     
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = new Pedido(
                            rs.getInt("id"),
                            rs.getInt("cliente_id"),
                            rs.getBigDecimal("valor_total"),
                            rs.getString("status"),
                            rs.getTimestamp("criado_em"),
                            rs.getString("criado_por")
                    );
                    
                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("cliente_id"));
                    cliente.setNome(rs.getString("cliente_nome"));
                    cliente.setEmail(rs.getString("cliente_email"));
                    
                    pedido.setCliente(cliente);
                    return pedido;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pedido por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Pedido> buscarHistoricoCliente(int clienteId) {
        String sql = "SELECT * FROM pedido WHERE cliente_id = ? ORDER BY criado_em DESC LIMIT 5";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new Pedido(
                            rs.getInt("id"),
                            rs.getInt("cliente_id"),
                            rs.getBigDecimal("valor_total"),
                            rs.getString("status"),
                            rs.getTimestamp("criado_em"),
                            rs.getString("criado_por")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar histórico do cliente: " + e.getMessage());
        }
        return pedidos;
    }

    public RelatorioVendas gerarRelatorio() {
        String sql = "SELECT COUNT(*) as qtd, SUM(valor_total) as soma_total, AVG(valor_total) as media_total FROM pedido";
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int qtd = rs.getInt("qtd");
                BigDecimal soma = rs.getBigDecimal("soma_total");
                BigDecimal media = rs.getBigDecimal("media_total");

                if (soma == null) soma = BigDecimal.ZERO;
                if (media == null) media = BigDecimal.ZERO;

                return new RelatorioVendas(qtd, media, soma);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
        }
        return new RelatorioVendas(0, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
