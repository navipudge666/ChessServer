package ThreadDispatcher;

//import Main;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPServerTask extends ThreadedTask
{
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    //private final String path = Main.path;
    private final String path = "123";



    public HTTPServerTask(Socket socket) throws IOException
    {
        super("HTTP");
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
                String command = in.readLine().replace("%20", " ").toLowerCase();
                System.out.println("read: " + command);
                command = getCommand(command);
                System.out.println("path + result: " + path + command);
                if (command != null && command.contains("favicon.ico"))
                {
                }
                else if (command != null && command.contains("."))
                {
                    File file = new File(path + command);
                    System.out.println();
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    socket.getOutputStream().write(getHeader(fileBytes.length).getBytes());
                    socket.getOutputStream().write(fileBytes);
                    socket.getOutputStream().flush();
                    out.flush();
                }
                else
                {
                    File file = new File(path + (command != null? command : ""));
                    List<String> dirNames = new ArrayList<>();
                    List<String> fileNames = new ArrayList<>();
                    for (File f : file.listFiles())
                    {
                        if (f.isDirectory())
                            dirNames.add(f.getName());
                        else
                            fileNames.add(f.getName());
                    }
                    String html = getHtml(dirNames, fileNames);
                    out.write(getHeader(html.length()));
                    out.write(html);
                    out.flush();
                    break;
                }
            }
        }
        catch (IOException e)
        {
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

    private String getCommand(String message)
    {
        Pattern pattern = Pattern.compile("get (.*) http/1.1");
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches())
            return matcher.group(1).trim().replace("/%22", "").replace("/", "\\");
        else
            return null;

    }

    private String getHeader(int contentLength)
    {
        return  "HTTP/1.1 200 OK\r\n" +
                "Server: YarServer/2009-09-09\r\n" +
                "Content-Type: file\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Connection: close\r\n\r\n";
    }

    private String getHtml(List<String> dirNames, List<String> fileNames)
    {
        StringBuilder result = new StringBuilder("<a href=\" / \">/</a>");
        for (String dirName : dirNames)
        {
            result.append("<p><a href=\\\"");
            result.append(dirName);
            result.append("/\">");
            result.append(dirName);
            result.append("</a><p>");
        }
        for (String fileName : fileNames)
        {
            result.append("<p><a href=\"");
            result.append(fileName);
            result.append("\" download>");
            result.append(fileName);
            result.append("</a><p>");
        }

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta http-equiv=\"content - type\" content=\"text / html; charset = utf - 8\">"
                + "</head>"
                + "<body>"
                + "<table><tr><td><h1>HelloWorld</h1></tr>"
                + "</table>"
                + result.toString()
                + "</body>"
                + "</html>";

    }
}
