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

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author davis
 */
public class CommunicationManager {
        
        private final HashMap<Integer, MessageListener>         m_channels;
        private final MessageManager                            m_mgr;
        
        public CommunicationManager(MessageManager mgr) {
                m_channels = new HashMap<>();
                m_mgr = mgr;
        }
        
        public void register_channel(int user_id, MessageListener listener) {
                m_channels.put(user_id, listener);
                
                // Check any unread messages.
                ArrayList<Message> msgs = m_mgr.get_received_unread_messages(user_id);
                msgs.stream().forEach(listener::on_receive);
                
                m_mgr.clear_unread_state(user_id);
        }
        
        public void close_channel(int user_id) {
                m_channels.remove(user_id);
        }
        
        public boolean send_message(Message msg) {
                MessageListener channel = m_channels.get(msg.receiver());
                if (channel != null) {
                        channel.on_receive(msg);
                }
                return m_mgr.new_message(msg);
        }
}