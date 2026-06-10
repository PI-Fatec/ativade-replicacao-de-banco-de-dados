package br.com.grupo.repository;

import br.com.grupo.config.AppConfig;
import br.com.grupo.db.DatabaseManager;
import br.com.grupo.model.Produto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {
    private final DatabaseManager dbManager;

    public ProdutoRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public long criarProduto(String descricao, String categoria, BigDecimal valor, int estoque) {
        String sql = "INSERT INTO produto (descricao, categoria, valor, estoque, criado_por) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getWriteConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, descricao);
            stmt.setString(2, categoria);
            stmt.setBigDecimal(3, valor);
            stmt.setInt(4, estoque);
            stmt.setString(5, AppConfig.GROUP_NAME);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar produto: " + e.getMessage());
        }
        return -1;
    }

    public List<Produto> buscarProdutosAleatorios(int quantidade) {
        String sql = "SELECT * FROM produto ORDER BY RAND() LIMIT ?";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapResultSetToProduto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos aleatórios: " + e.getMessage());
        }
        return produtos;
    }

    public List<Produto> buscarProdutosBaixoEstoque() {
        String sql = "SELECT * FROM produto WHERE estoque < 10";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos com baixo estoque: " + e.getMessage());
        }
        return produtos;
    }

    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("descricao"),
                rs.getString("categoria"),
                rs.getBigDecimal("valor"),
                rs.getInt("estoque"),
                rs.getTimestamp("criado_em"),
                rs.getString("criado_por")
        );
    }
}
