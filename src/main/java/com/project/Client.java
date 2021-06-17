package com.project;

import org.json.JSONObject;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader inR;
    private static BufferedWriter outWr;
    private static String host;
    private static int port;
    final private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void main(String[] args) {
        try {
            File file = new File("src\\main\\resources\\ipport.properties");
            Properties prop = new Properties();
            prop.load(new FileReader(file));
            host = prop.getProperty("host");
            port = Integer.parseInt(prop.getProperty("port"));
        } catch (IOException e) {
            System.out.println("Конфигурационный файл не обнаружен");
            e.printStackTrace();
        }

        try {
            try {
                try {
                clientSocket = new Socket(host, port);
                } catch (IOException e) {
                    System.out.println("Произошла ошибка, ваш сеанс будет завершен");
                    System.exit(0);
                }

                System.out.println("Вы подключены");


                inR = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                outWr = new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()));
                Scanner scanner = new Scanner(System.in);

                System.out.println("Введите имя:");
                String Log = scanner.nextLine();

                while (true) {
                    System.out.println("Введите сообщение:");
                    String message = scanner.nextLine();
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("Request", new JSONObject()
                            .put("User", new JSONObject()
                                    .put("Login", Log))
                            .put("Message", new JSONObject()
                                    .put("Body", message)
                                            .put("Timestamp", LocalDateTime.now().format(formatter))));

                    outWr.write(jsonobj+ "\n");
                    outWr.flush();
                    if (message.contains("\\exit"))
                        break;
                    String response = inR.readLine(); // получаем ответ от сервера "Сообщение доставлено"
                    System.out.println(response);
                }

            } finally {
                outWr.close();
                inR.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
