package config;

public class Config {
    
    public static final String DB_URL = "jdbc:mysql://localhost:3306/laptrinhmang";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";

    public static final String WEBSOCKET_HOST = "localhost";
    public static final int WEBSOCKET_PORT = 8085;
    public static final String WEBSOCKET_PATH = "/ConvertPDFToWord/socket";

    public static final String WEBSOCKET_URL = "ws://" + WEBSOCKET_HOST + ":" + WEBSOCKET_PORT + WEBSOCKET_PATH;
}
