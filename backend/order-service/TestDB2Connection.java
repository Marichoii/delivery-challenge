import java.sql.*;

public class TestDB2Connection {
    public static void main(String[] args) throws Exception {
        String url = System.getenv("DB2_JDBC_URL");
        String user = System.getenv("DB2_USERNAME");
        String pass = System.getenv("DB2_PASSWORD");

        System.out.println("URL  : " + url);
        System.out.println("User : " + user);
        System.out.println("Connecting...");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected! AutoCommit=" + conn.getAutoCommit());

            // insert
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ORDERS (ID, CUSTOMERNAME, ITEMID, ITEMNAME, QUANTITY, TOTAL, STATUS) VALUES (?,?,?,?,?,?,?)")) {
                ps.setString(1, "test-direct-" + System.currentTimeMillis());
                ps.setString(2, "Teste Direto");
                ps.setString(3, "angus-divino");
                ps.setString(4, "Angus Divino");
                ps.setInt(5, 1);
                ps.setDouble(6, 120.0);
                ps.setString(7, "CREATED");
                int rows = ps.executeUpdate();
                System.out.println("Inserted rows: " + rows);
            }

            // select
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT ID, CUSTOMERNAME, STATUS FROM ORDERS")) {
                System.out.println("Rows in ORDERS:");
                while (rs.next()) {
                    System.out.println("  " + rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
