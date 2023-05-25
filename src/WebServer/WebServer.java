package WebServer;

import ThreadDispatcher.HTTPServerTask;
import ThreadDispatcher.ThreadDispatcher;
import ThreadDispatcher.ThreadedTask;
import ThreadDispatcher.WebTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WebServer extends ThreadedTask
{

    private static WebServer instance;
    public boolean isStopped = false;
    public final HashMap<String, String> activeGames = new HashMap<>();
    public final HashMap<String, String> waitingGames = new HashMap<>();
    public final HashMap<String, Socket> activePlayers = new HashMap<>();
    public HashMap<String, Stats> statsHashMap = new HashMap<>();
    public HashMap<String, Double> playersWins = new HashMap<>();
    public HashMap<String, Double> playersLoses = new HashMap<>();

    private final ServerSocket socket;

    public WebServer(int port) throws IOException
    {
        super("SERVER at" + port);
        this.socket = new ServerSocket(port);
    }

    public static WebServer getInstance() throws IOException
    {
        if (instance == null)
        {
            synchronized (WebServer.class)
            {
                if (instance == null)
                    instance = new WebServer(8081);
            }
        }
        return instance;
    }

    public void doWork()
    {
        while (true)
        {
             try {
                 Socket client = socket.accept();
                 if (socket.getLocalPort() == 8081)
                     ThreadDispatcher.getInstance().Add(new WebTask(client));
             } catch (IOException e) {
                 e.printStackTrace();
             }
        }
    }
}
