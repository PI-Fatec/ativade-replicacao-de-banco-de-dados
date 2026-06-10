package br.com.grupo.db;

import br.com.grupo.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private final List<String> replicaUrls;
    private final AtomicInteger currentReplicaIndex = new AtomicInteger(0);

    public DatabaseManager() {
        this.replicaUrls = AppConfig.getReplicaUrls();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found.", e);
        }
    }

    public Connection getWriteConnection() throws SQLException {
        if (AppConfig.PRIMARY_URL == null || AppConfig.PRIMARY_USER == null || AppConfig.PRIMARY_PASSWORD == null) {
            throw new SQLException("Primary database environment variables are missing.");
        }
        return DriverManager.getConnection(AppConfig.PRIMARY_URL, AppConfig.PRIMARY_USER, AppConfig.PRIMARY_PASSWORD);
    }

    public Connection getReadConnection() throws SQLException {
        if (replicaUrls.isEmpty()) {
            return getWriteConnection();
        }

        int index = currentReplicaIndex.getAndIncrement() % replicaUrls.size();
        String url = replicaUrls.get(index);
        
        return DriverManager.getConnection(url, AppConfig.PRIMARY_USER, AppConfig.PRIMARY_PASSWORD);
    }
}
