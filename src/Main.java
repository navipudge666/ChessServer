import ThreadDispatcher.ThreadDispatcher;
import WebServer.WebServer;

import java.io.IOException;

public class Main
{
    public static WebServer server;

    public static void main(String[] args)
    {
        ThreadDispatcher.getInstance().setMaxPoolSize(20);
        try {
            server = WebServer.getInstance();
            ThreadDispatcher.getInstance().Add(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
