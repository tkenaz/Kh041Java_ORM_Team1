package ConnectionToDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPull {

    //private DBConnection dbConnection = new DBConnection();
    private static int INITIAL_POOL_SIZE = 10;
    private List<Connection> connectionPool = new ArrayList<>(INITIAL_POOL_SIZE);
    private List<Connection> usedConnections = new ArrayList<>(INITIAL_POOL_SIZE);

    public ConnectionPull() {
        create();
    }


    public void create(){
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            connectionPool.add(new DBConnection().connect());
        }
    }

    public Connection getConnection() {
        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }


    //    //testing connection
    public static void main(String[] args) throws SQLException {
        ConnectionPull connectionPull = new ConnectionPull();
        Connection connection = connectionPull.getConnection();

        System.out.println("Creating statement...");
        Statement stmt = connection.createStatement();
        String sql;
        sql = "SELECT * FROM users";
        ResultSet rs = stmt.executeQuery(sql);

        if (rs == null) System.out.println( "null");
        while (rs.next()) {

            int id = rs.getInt("id");
            int age = rs.getInt("age");
            String name = rs.getString("name");

            System.out.print("ID: " + id);
            System.out.print(", Age: " + age);
            System.out.print(", First: " + name);
            System.out.println("");
        }
        connectionPull.releaseConnection(connection);

    }
}
