package CarFleet.Config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import CarFleet.Model.*;

@Component
public class CarFleetLocationWebSocketHandler extends TextWebSocketHandler  {
	
	public CarFleetLocationWebSocketHandler() {}
	private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from clients (if needed)
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastLocationUpdate(Location updatedLocation) throws Exception {
        String jsonLocation = convertLocationToJson(updatedLocation);

        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(jsonLocation));
        }
    }

    private String convertLocationToJson(Location location) {
        // Convert Location object to JSON string
        // You can use a JSON library like Jackson or Gson for this
        // Example: return gson.toJson(location);
    	return "GOOD";
    }
}
