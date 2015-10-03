/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jaishreeganesh
 */
public class DatabaseUtil {

    public static Connection connect = null;
    public static String roletest = "";

    public static Connection readDataBase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/cloud_project?" + "user=root&password=");
        } catch (Exception ex) {
        }
        return connect;
    }

    public static String[] getAuthentication(String username, String password) {
        String[] name = new String[3];
        Connection conn = readDataBase();
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        password = Encryption.getMD5(password);
        ResultSet resultSet = null;
        ResultSet resultSet1 = null;
        try {
            String sql = "SELECT fname, lname, username, password FROM users WHERE username = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("password").equals(password)) {
                    name[2] = resultSet.getString("username");
                    name[0] = resultSet.getString("fname").concat(" ").concat(resultSet.getString("lname"));
                    if (username.equalsIgnoreCase("admin")) {
                        name[1] = "admin";
                    } else {
                        name[1] = "";
                        roletest = ("(");
                        String sql1 = "SELECT rolename FROM user_roles WHERE username = ?";
                        preparedStatement1 = conn.prepareStatement(sql1);
                        preparedStatement1.setString(1, username);
                        resultSet1 = preparedStatement1.executeQuery();
                        while (resultSet1.next()) {
                            String role = resultSet1.getString("rolename");
                            name[1] = name[1].concat(", ").concat(role);
                        }

                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet1 != null) {
                    resultSet1.close();
                }
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return name;
    }

    public static List<Song> getSongList(String roles) {
        List<Song> songs = new ArrayList<Song>();
        DbConnect mydb = new DbConnect();
        songs = mydb.createtempDisplay(roles);
        //System.out.println(answer);
        return songs;
    }

    public static boolean forgotPassword(String username, String password) {
        boolean flag = false;
        Connection conn = readDataBase();
        PreparedStatement statement = null;
        flag = checkUserExists(conn, username);
        password = Encryption.getMD5(password);
        if (flag) {
            try {
                String sql = "Update users SET password = ? where username = ?";
                statement = conn.prepareStatement(sql);
                statement.setString(1, password);
                statement.setString(2, username);
                statement.executeUpdate();
                System.out.println("here");
                flag = true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            return flag;
        }
        return flag;
    }

    public static boolean registerUser(String fname, String lname, String username,
            String password, List<String> roles) {
        boolean flag = false;
        Connection conn = readDataBase();
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement1 = null;
        flag = checkUserExists(conn, username);
        password = Encryption.getMD5(password);
        String publicKey = Encryption.GeneratePublicKey();
        if (!flag) {
            try {
                String sql = "Insert into users (fname, lname, username, password, publickey) values "
                        + "(?,?,?,?,?)";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, fname);
                preparedStatement.setString(2, lname);
                preparedStatement.setString(3, username);
                preparedStatement.setString(4, password);
                preparedStatement.setString(5, publicKey);
                preparedStatement.executeUpdate();
                flag = true;
                if (flag) {
                    flag = false;
                    for (int i = 0; i < roles.size(); i++) {
                        String sql1 = "Insert into user_roles (username, rolename) values (?, ?)";
                        preparedStatement1 = conn.prepareStatement(sql1);
                        preparedStatement1.setString(1, username);
                        preparedStatement1.setString(2, roles.get(i));
                        preparedStatement1.executeUpdate();
                    }
                    flag = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (preparedStatement1 != null) {
                        preparedStatement1.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else {
            return flag;
        }
        return flag;
    }

    public static boolean checkUserExists(Connection conn, String username) {
        boolean flag = false;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            String sql = "Select username from users";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                if (resultSet.getString("username").equals(username)) {
                    flag = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return flag;
    }
}
