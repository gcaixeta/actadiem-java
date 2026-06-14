package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BancoDeDados {
    private static Connection conn;

    private static final String URL = "jdbc:mysql://localhost:3306/actadiem";
    private static final String USER = "admin";
    private static final String PASSWORD = "senha123";

    private BancoDeDados() {}

    public static Connection getInstance() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco", e);
        }

        return conn;
    }
}