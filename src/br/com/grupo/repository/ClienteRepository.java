package br.com.grupo.repository;

import br.com.grupo.config.AppConfig;
import br.com.grupo.db.DatabaseManager;
import br.com.grupo.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {
    private final DatabaseManager dbManager;

    public ClienteRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public long criarCliente(String nome, String email) {
        String sql = "INSERT INTO cliente (nome, email, criado_por) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getWriteConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, AppConfig.GROUP_NAME);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
        }
        return -1;
    }

    public Cliente buscarClienteAleatorio() {
        String sql = "SELECT * FROM cliente ORDER BY RAND() LIMIT 1";
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente aleatório: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> listarClientes() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = dbManager.getReadConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }
        return clientes;
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getTimestamp("criado_em"),
                rs.getString("criado_por")
        );
    }
}
