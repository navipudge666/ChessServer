package ThreadDispatcher;

import WebServer.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class WebTask extends ThreadedTask
{
    private final Socket socket;
    BufferedReader  in;
    BufferedWriter  out;

    public WebTask(Socket socket) throws IOException
    {
        super("WebTask");
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    protected void doWork()
    {
        try
        {
            while(true)
            {
                String message = in.readLine();
                if (message == null)
                    continue;
                String[] command = message.split(" ");
                System.out.println(command[0]);
                switch (command[0]) {
                    case "setName" -> setName(command[1]);
                    case "move" -> sendMove(command[1], message);
                    case "createGame" -> createGame(command[1]);
                    case "joinGame" -> joinGame(command[1], command[2]);
                    case "getGames" -> getGames();
                    case "finished" -> finished(command[1], command[2]);
                    case "getStats" -> getStats();
                    default -> {
                        out.write("Неизвестная команда." + "\r\n");
                        out.flush();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setName(String username) throws IOException
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance().activePlayers)
        {
            server.activePlayers.put(username, this.socket);
            out.write("ok" + "\r\n");
            out.flush();
        }
    }

    private void sendMove(String username, String message) throws Exception
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance().activeGames)
        {
            System.out.println("Server got this move: " + message);
            String otherPlayer = server.activeGames.get(username);
            if (otherPlayer == null)
                throw new Exception("Game not found");
            Socket socket = server.activePlayers.get(otherPlayer);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(message + "\r\n");
            writer.flush();
            System.out.println("And sent it to " + otherPlayer);
        }
    }

    private void createGame(String username) throws IOException
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance().waitingGames)
        {
            server.waitingGames.put(username, null);
            out.write("ok" + "\r\n");
            out.flush();
        }
    }

    private void joinGame(String hostUsername, String username) throws IOException
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance().waitingGames)
        {
            if (!server.waitingGames.containsKey(hostUsername))
            {
                out.write("error" + "\r\n");
                out.flush();
                return;
            }
            server.waitingGames.remove(hostUsername);
            server.activeGames.put(hostUsername, username);
            server.activeGames.put(username, hostUsername);
            out.write("ok" + "\r\n");
            out.flush();
            Socket socket = server.activePlayers.get(hostUsername);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message = "joined " + username;
            writer.write(message + "\r\n");
            writer.flush();
            System.out.println("Join message: " + message);
        }
    }

    private void getGames() throws IOException
    {
        WebServer server = WebServer.getInstance();
        StringBuilder builder = new StringBuilder();
        synchronized (WebServer.getInstance().waitingGames)
        {
            for (String name : server.waitingGames.keySet())
            {
                if (server.waitingGames.get(name) == null)
                {
                    builder.append(name);
                    builder.append(" ");
                }
            }
        }
        out.write(builder.toString() + "\r\n");
        out.flush();
    }

    private void finished(String winner, String loser) throws IOException
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance())
        {
            if (server.activeGames.containsKey(winner))
            {
                server.activeGames.remove(winner);
                server.activeGames.remove(loser);

                if (server.playersWins.containsKey(winner)) {
                    Double wins = server.playersWins.get(winner);
                    server.playersWins.put(winner, wins + 1);
                } else {
                    server.playersWins.put(winner, 1.0);
                    server.playersLoses.put(winner, 0.0);
                }
                if (server.playersLoses.containsKey(loser)) {
                    Double loses = server.playersLoses.get(loser);
                    server.playersLoses.put(loser, loses + 1);
                } else {
                    server.playersLoses.put(loser, 1.0);
                    server.playersWins.put(loser, 0.0);
                }

                Gson gson = new Gson();
                BufferedWriter writer = new BufferedWriter(new FileWriter("playersWins.json"));
                String json = gson.toJson(server.playersWins);
                writer.write(json);
                writer.close();

                writer = new BufferedWriter(new FileWriter("playersLoses.json"));
                json = gson.toJson(server.playersLoses);
                writer.write(json);
                writer.close();
            }
            out.write("ok" + "\r\n");
            out.flush();
        }
    }

    private void getStats() throws IOException
    {
        WebServer server = WebServer.getInstance();
        synchronized (WebServer.getInstance())
        {
            Gson gson = new Gson();
            BufferedReader reader = new BufferedReader(new FileReader("playersWins.json"));
            String json = reader.readLine();
            reader.close();

            //Type type = new TypeToken<HashMap<String, Integer>>(){}.getClass();
            server.playersWins = gson.fromJson(json, server.playersWins.getClass());

            reader = new BufferedReader(new FileReader("playersLoses.json"));
            json = reader.readLine();
            reader.close();

            //type = new TypeToken<HashMap<String, Integer>>(){}.getClass();
            server.playersLoses = gson.fromJson(json, server.playersLoses.getClass());

            StringBuilder builder = new StringBuilder();
            for (String name : server.playersWins.keySet())
            {
                builder.append(name);
                builder.append(' ');
                builder.append(server.playersWins.get(name));
                builder.append(' ');
                builder.append(server.playersLoses.get(name));
                builder.append(' ');
            }

            out.write(builder.toString() + "\r\n");
            out.flush();

        }
    }
}

