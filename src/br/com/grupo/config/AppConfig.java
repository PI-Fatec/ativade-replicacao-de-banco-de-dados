package br.com.grupo.config;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    public static final String PRIMARY_URL = System.getenv("PRIMARY_URL");
    public static final String PRIMARY_USER = System.getenv("PRIMARY_USER");
    public static final String PRIMARY_PASSWORD = System.getenv("PRIMARY_PASSWORD");
    public static final String GROUP_NAME = System.getenv("GROUP_NAME") != null ? System.getenv("GROUP_NAME") : "GrupoDefault";

    public static List<String> getReplicaUrls() {
        String replicaEnv = System.getenv("REPLICA_URLS");
        List<String> replicas = new ArrayList<>();
        if (replicaEnv != null && !replicaEnv.trim().isEmpty()) {
            String[] urls = replicaEnv.split(",");
            for (String url : urls) {
                if (!url.trim().isEmpty()) {
                    replicas.add(url.trim());
                }
            }
        }
        return replicas;
    }
}
