package br.com.grupo.repository;

import br.com.grupo.db.DatabaseManager;
import br.com.grupo.model.PedidoItem;
import br.com.grupo.model.Produto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoItemRepository {
    private final DatabaseManager dbManager;

    public PedidoItemRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void criarPedidoItem(int pedidoId, int produtoId, int quantidade, BigDecimal valorUnitario) {
        String sql = "INSERT INTO pedido_item (pedido_id, produto_id, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getWriteConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            stmt.setInt(2, produtoId);
            stmt.setInt(3, quantidade);
            stmt.setBigDecimal(4, valorUnitario);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao criar item do pedido: " + e.getMessage());
        }
    }

    public List<PedidoItem> buscarItensPedido(int pedidoId) {
        String sql = "SELECT pi.*, p.descricao as produto_descricao, p.categoria as produto_categoria " +
                     "FROM pedido_item pi " +
                     "JOIN produto p ON pi.produto_id = p.id " +
                     "WHERE pi.pedido_id = ?";
                     
        List<PedidoItem> itens = new ArrayList<>();
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoItem item = new PedidoItem(
                            rs.getInt("id"),
                            rs.getInt("pedido_id"),
                            rs.getInt("produto_id"),
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("valor_unitario")
                    );
                    
                    Produto produto = new Produto();
                    produto.setId(rs.getInt("produto_id"));
                    produto.setDescricao(rs.getString("produto_descricao"));
                    produto.setCategoria(rs.getString("produto_categoria"));
                    
                    item.setProduto(produto);
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar itens do pedido: " + e.getMessage());
        }
        return itens;
    }
}
