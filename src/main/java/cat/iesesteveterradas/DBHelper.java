package cat.iesesteveterradas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private final String dbPath;

    public DBHelper(String dbPath) {
        this.dbPath = dbPath;
        ensureDataDir();
    }

    /**
     * Ensure the data directory exists.
     */
    private void ensureDataDir() {
        Path p = Paths.get(dbPath).getParent();
        if (p != null && !Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (Exception e) {
                System.err.println("No s'ha pogut crear el directori data: " + e.getMessage());
            }
        }
    }

    /**
     * Connect to the SQLite database.
     */
    private Connection connect() throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    public void initializeDatabase() {
        try (Connection conn = connect()) {
            if (conn == null)
                return;
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("PRAGMA foreign_keys = ON;");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS Faccio (" +
                        "id INTEGER PRIMARY KEY, " +
                        "nom VARCHAR(15), " +
                        "resum VARCHAR(500)" +
                        ")");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS Personatge (" +
                        "id INTEGER PRIMARY KEY, " +
                        "nom VARCHAR(15), " +
                        "atac REAL, " +
                        "defensa REAL, " +
                        "idFaccio INTEGER, " +
                        "FOREIGN KEY(idFaccio) REFERENCES Faccio(id)" +
                        ")");

                // populate if empty
                if (isTableEmpty(conn, "Faccio")) {
                    populateSampleData(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a table is empty.
     * 
     * @param conn
     * @param table
     * @return
     * @throws SQLException
     */
    private boolean isTableEmpty(Connection conn, String table) throws SQLException {
        String q = "SELECT count(1) FROM " + table;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            if (rs.next())
                return rs.getInt(1) == 0;
        }
        return true;
    }

    /**
     * Populate sample data into the database.
     * 
     * @param conn
     * @throws SQLException
     */
    private void populateSampleData(Connection conn) throws SQLException {
        // Use the exact dataset provided by the user (no explicit ids for
        // autoincrement)
        String insertF = "INSERT INTO Faccio(nom, resum) VALUES(?, ?)";
        String insertP = "INSERT INTO Personatge(nom, atac, defensa, idFaccio) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pf = conn.prepareStatement(insertF);
                PreparedStatement pp = conn.prepareStatement(insertP)) {

            // Factions (user-provided texts)
            pf.setString(1, "Cavallers");
            pf.setString(2,
                    "Though seen as a single group, the Knights are hardly unified. There are many Legions in Ashfeld, the most prominent being The Iron Legion.");
            pf.executeUpdate();

            pf.setString(1, "Vikings");
            pf.setString(2,
                    "The Vikings are a loose coalition of hundreds of clans and tribes, the most powerful being The Warborn.");
            pf.executeUpdate();

            pf.setString(1, "Samurais");
            pf.setString(2,
                    "The Samurai are the most unified of the three factions, though this does not say much as the Daimyos were often battling each other for dominance.");
            pf.executeUpdate();

            // Personatges (user-provided)
            pp.setString(1, "Warden");
            pp.setDouble(2, 1);
            pp.setDouble(3, 3);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Conqueror");
            pp.setDouble(2, 2);
            pp.setDouble(3, 2);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Peacekeep");
            pp.setDouble(2, 2);
            pp.setDouble(3, 3);
            pp.setInt(4, 1);
            pp.executeUpdate();

            pp.setString(1, "Raider");
            pp.setDouble(2, 3);
            pp.setDouble(3, 3);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Warlord");
            pp.setDouble(2, 2);
            pp.setDouble(3, 2);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Berserker");
            pp.setDouble(2, 1);
            pp.setDouble(3, 1);
            pp.setInt(4, 2);
            pp.executeUpdate();

            pp.setString(1, "Kensei");
            pp.setDouble(2, 3);
            pp.setDouble(3, 2);
            pp.setInt(4, 3);
            pp.executeUpdate();

            pp.setString(1, "Shugoki");
            pp.setDouble(2, 2);
            pp.setDouble(3, 1);
            pp.setInt(4, 3);
            pp.executeUpdate();

            pp.setString(1, "Orochi");
            pp.setDouble(2, 3);
            pp.setDouble(3, 2);
            pp.setInt(4, 3);
            pp.executeUpdate();
        }
    }

    /**
     * Get all factions.
     * 
     * @return
     */
    public List<Faccio> getAllFaccions() {
        List<Faccio> list = new ArrayList<>();
        String q = "SELECT id, nom, resum FROM Faccio";
        try (Connection conn = connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                list.add(new Faccio(rs.getInt("id"), rs.getString("nom"), rs.getString("resum")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get all characters.
     * 
     * @return
     */
    public List<Personatge> getAllPersonatges() {
        List<Personatge> list = new ArrayList<>();
        String q = "SELECT id, nom, atac, defensa, idFaccio FROM Personatge";
        try (Connection conn = connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                list.add(new Personatge(rs.getInt("id"), rs.getString("nom"), rs.getDouble("atac"),
                        rs.getDouble("defensa"), rs.getInt("idFaccio")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * * Get characters by faction.
     * 
     * @param idFaccio
     * @return
     */
    public List<Personatge> getPersonatgesByFaccio(int idFaccio) {
        List<Personatge> list = new ArrayList<>();
        String q = "SELECT id, nom, atac, defensa, idFaccio FROM Personatge WHERE idFaccio = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, idFaccio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Personatge(rs.getInt("id"), rs.getString("nom"), rs.getDouble("atac"),
                            rs.getDouble("defensa"), rs.getInt("idFaccio")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get best attacker by faction.
     * 
     * @param idFaccio
     * @return
     */
    public Personatge getBestAtacantByFaccio(int idFaccio) {
        String q = "SELECT id, nom, atac, defensa, idFaccio FROM Personatge WHERE idFaccio = ? ORDER BY atac DESC LIMIT 1";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, idFaccio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Personatge(rs.getInt("id"), rs.getString("nom"), rs.getDouble("atac"),
                            rs.getDouble("defensa"), rs.getInt("idFaccio"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get best defender by faction.
     * 
     * @param idFaccio
     * @return
     */
    public Personatge getBestDefensorByFaccio(int idFaccio) {
        String q = "SELECT id, nom, atac, defensa, idFaccio FROM Personatge WHERE idFaccio = ? ORDER BY defensa DESC LIMIT 1";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, idFaccio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Personatge(rs.getInt("id"), rs.getString("nom"), rs.getDouble("atac"),
                            rs.getDouble("defensa"), rs.getInt("idFaccio"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
