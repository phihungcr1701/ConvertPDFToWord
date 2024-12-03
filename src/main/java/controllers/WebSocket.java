package controllers;

import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import model.Bean.file;

@ServerEndpoint("/socket")
public class WebSocket {
	private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    // Khi client kết nối
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    // Khi client ngắt kết nối
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    // Khi nhận tin nhắn từ client
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received from client: " + message);
    }

    // Khi có lỗi xảy ra
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on session " + session.getId());
        throwable.printStackTrace();
    }

    public static void sendMessageToAll(file messageObject) {
        try {
            // Chuyển đối tượng Java thành chuỗi JSON bằng Gson
            Gson gson = new Gson();
            String messageJson = gson.toJson(messageObject); // Chuyển đối tượng thành JSON

            // Gửi tin nhắn dưới dạng JSON tới tất cả các client
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(messageJson); // Gửi tin nhắn đến client
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
