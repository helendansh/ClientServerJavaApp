package com.project;

import org.json.JSONObject;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket serverSocket;
    private static BufferedReader inR;
    private static BufferedWriter outWr;
    private static int port;
    final private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) throws IOException {
        try {
        File file = new File ("src\\main\\resources\\ipport.properties");
        Properties prop = new Properties();
        prop.load(new FileReader(file));
        port = Integer.parseInt(prop.getProperty("port"));
        } catch (IOException e) {
            System.out.println("Конфигурационный файл не обнаружен");
            e.printStackTrace();
        }

        try {

            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            System.out.println("Клиент подключился");

                    outWr = new BufferedWriter(
                            new OutputStreamWriter(clientSocket.getOutputStream()));
                    inR = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                    while (true) {
                        String clientMes = inR.readLine();
                        if (clientMes.contains("\\exit"))
                            break;
                        JSONObject jsonobj = new JSONObject(clientMes);

                        System.out.println("Принято сообщение от клиента:" + jsonobj.
                                getJSONObject("Request")
                                .getJSONObject("User").get("Login") + ", отправлено: "
                                + jsonobj.getJSONObject("Request")
                                .getJSONObject("Message").get("Timestamp")
                                + ", текст:" + jsonobj.getJSONObject("Request")
                                .getJSONObject("Message").get("Body"));

                        JSONObject response = new JSONObject();
                        response.put("Response", new JSONObject()
                                .put("Message", new JSONObject()
                                        .put("UserLogin", jsonobj.getJSONObject("Request").getJSONObject("User").get("Login"))
                                        .put("Result", "success")
                                        .put("Timestamp", LocalDateTime.now().format(formatter))));

                        outWr.write("Сообщение доставлено. " + response + "\n");
                        outWr.flush();
                    }
                } finally {
                    outWr.close();
                    inR.close();
                    clientSocket.close();
                    serverSocket.close();
                }
    }
}
