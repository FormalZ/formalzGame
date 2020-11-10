package formalz.connection;

import org.java_websocket.WebSocket;

public interface ClientFactory {
    public static final ClientFactory DEFAULT = new ClientFactory() {
        public Client constructClient(WebSocket socket) {
            return new Client(socket);
        }
    };

    public Client constructClient(WebSocket socket);
}
