//STEP 1. Import required packages

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import sun.misc.BASE64Encoder;

public class DbConnect {

    //JDBC driver name and database URL
    Preferences prefs = Preferences.userNodeForPackage(CloudUtil.class);
    static Statement stmt = null;
    static PreparedStatement pStmt = null;
    ResultSet rs = null;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/cloud_project";
    //Database credentials
    static final String USER = "root";
    static final String PASS = "";
    static String indexer = null;
    static String title = null;
    static String copyright = null;
    static String genre = null;
    static String lyrics = null;
    static String artist = null;
    static String duration = null;
    static String roles = null;
    static String database_name = "songs";
    static String table_name = "song";

    public static void main(String[] args) {
        Connection conn = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main

    public void createTable() {
        try {
            //STEP 4: Execute a query
            System.out.println("Creating table in given database...");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "CREATE TABLE IF NOT EXISTS " + table_name + " (indexer VARCHAR(255), title VARCHAR(255), copyright VARCHAR(255), genre VARCHAR(255), lyrics VARCHAR(255), artist VARCHAR(255), duration VARCHAR(255), roles VARCHAR(255), PRIMARY KEY (indexer))";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            System.out.println("Created table in given database...");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean insertTable() {
        boolean result = false;
        try {
            int ret = -1;
            System.out.println("Insert values in given table......");
            //Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Connection conn = DatabaseUtil.readDataBase();
            String sql = "INSERT INTO " + table_name + "(indexer,title,copyright,genre,lyrics,artist,duration,roles) VALUES (" + '"' + indexer + '"' + "," + '"' + title + '"' + "," + '"' + copyright + '"' + "," + '"' + genre + '"' + "," + '"' + lyrics + '"' + "," + '"' + artist + '"' + "," + '"' + duration + '"' + "," + '"' + roles + '"' + ")";
            System.out.println(sql);
            stmt = conn.createStatement();
            ret = stmt.executeUpdate(sql);
            if (ret >= 0) {
                result = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String GetPublicKey() throws SQLException {
        String key = null;
        String username = prefs.get("USER_NAME", "");
        System.out.println("username : " + username);
        //Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
        Connection con = DatabaseUtil.readDataBase();
        String query = "SELECT publickey FROM users WHERE username = ?";
        pStmt = con.prepareStatement(query);
        pStmt.setString(1, username);
        rs = pStmt.executeQuery();
        System.out.println("size : " + rs.getFetchSize());
        while (rs.next()) {
            System.out.println("pub : ");
            System.out.println("here : " + rs.getString("publickey"));
            key = rs.getString("publickey");
        }
        return key;
    }

    public void InsertKeyManager(String songID, String key) {
        try {
            //Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Connection conn = DatabaseUtil.readDataBase();
            String query = "INSERT INTO KeyManager VALUES(" + "\"" + songID + "\",\"" + key + "\");";
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String GetEncryptedKey(String songID) {
        String encryptedKey = null;
        try {
            Connection conn = DatabaseUtil.readDataBase();
            String query = "SELECT * FROM keymanager WHERE songID = " + "\"" + songID + "\";";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {

                encryptedKey = rs.getString("PublicKey");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encryptedKey;
    }

    public ArrayList<Song> createtempDisplay(String role) {
        ArrayList<Song> lstSongs = new ArrayList<Song>();
        try {
            //STEP 4: Execute a query
            String[] roles = role.split(",");
            System.out.println("Creating temporary display table...");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * from " + table_name + " s WHERE ";
            for (int i = 0; i < roles.length; i++) {
                if (i > 0) {
                    sql += "OR ";
                }
                sql += "s.roles like '%" + roles[i].trim() + "%' ";
            }
            System.out.println("query is : " +sql);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Song objSong = new Song();
                objSong.setArtist(rs.getString("artist"));
                objSong.setCopyright(rs.getString("copyright"));
                objSong.setDuration(rs.getString("duration"));
                objSong.setGenre(rs.getString("genre"));
                objSong.setLyrics(rs.getString("lyrics"));
                objSong.setIndex(rs.getString("indexer"));
                objSong.setTitle(rs.getString("title"));
                objSong.setRoles(rs.getString("roles"));
                objSong = new Encryption().GetDecryptedData(objSong);
                lstSongs.add(objSong);
            }
            System.out.println("Created temporary table...");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lstSongs;
    }
}//end JDBCExample
