/*
 * Copyright (C) 2017 davis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author davis
 */
public class FriendRequestManager {
        
        private DBConnector     m_conn;
        
        public FriendRequestManager(DBConnector conn) {
                m_conn = conn;
                try {
                        Statement s = m_conn.get_connection().createStatement();
                        s.executeUpdate("create table if not exists friend_request_manager("
                                + "uid_a integer,"
                                + "uid_b integer,"
                                + "primary key (uid_a, uid_b));");
                        // @todo: add trigger to handle user removal.
                } catch (SQLException ex) {
                        Logger.getLogger(FriendRequestManager.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        private boolean has_request(Integer uid_a, Integer uid_b) throws SQLException {
                Statement s = m_conn.get_connection().createStatement();
                ResultSet result = s.executeQuery("select 1 from friend_request_manager "
                        + "where (uid_a = " + uid_a + ") and (uid_b = " + uid_b + ");");
                return result.next();
        }
        
        public boolean create_friend_request(Integer uid_a, Integer uid_b) {
                try {
                        if (has_request(uid_a, uid_b))
                                return false;
                        Statement s = m_conn.get_connection().createStatement();
                        // Friend request is anti-symmetric.
                        int r = s.executeUpdate("insert into friend_request_manager "
                                + "(uid_a, uid_b) values (" + uid_a + "," + uid_b + ");");
                        return r != 0;
                } catch (SQLException ex) {
                        Logger.getLogger(FriendRequestManager.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                }
        }
        
        public boolean remove_request(Integer uid_a, Integer uid_b) {
                try {
                        Statement s = m_conn.get_connection().createStatement();
                        // Friend request is anti-symmetric.
                        int r = s.executeUpdate("delete from friend_request_manager "
                                + "where (uid_a = " + uid_a + " and uid_b = " + uid_b + ");");
                        return r != 0;
                } catch (SQLException ex) {
                        Logger.getLogger(FriendRequestManager.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                }
        }
        
        public ArrayList<Integer> request_from(Integer uid) {
                try {
                        Statement s = m_conn.get_connection().createStatement();
                        ResultSet result = s.executeQuery("select uid_b from friend_request_manager "
                                + "where uid_a = " + uid + ";");
                        ArrayList<Integer> requests = new ArrayList<>();
                        while (result.next()) {
                                requests.add(result.getInt("uid_b"));
                        }
                        return requests;
                } catch (SQLException ex) {
                        Logger.getLogger(FriendRequestManager.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }
        }
        
        public ArrayList<Integer> request_to(Integer uid) {
                try {
                        Statement s = m_conn.get_connection().createStatement();
                        ResultSet result = s.executeQuery("select uid_a from friend_request_manager "
                                + "where uid_b = " + uid + ";");
                        ArrayList<Integer> requests = new ArrayList<>();
                        while (result.next()) {
                                requests.add(result.getInt("uid_a"));
                        }
                        return requests;
                } catch (SQLException ex) {
                        Logger.getLogger(FriendRequestManager.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }
        }
}
