package clientPackage;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String ipserveur = "127.0.0.1"; // Adresse du serveur
    private static final int port = 1234;
    private static final int bufsize = 1024;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(ipserveur);
            Scanner sc = new Scanner(System.in);

            System.out.print("Entrez votre nom d’utilisateur : ");
            String username = sc.nextLine();

            // Thread pour recevoir les messages
            Thread receiveThread = new Thread(() -> {
                byte[] buffer = new byte[bufsize];
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String received = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
                        System.out.println("\n " + received);
                        System.out.print(" Vous : ");
                    } catch (IOException e) {
                        System.err.println("Erreur de réception : " + e.getMessage());
                        break;
                    }
                }
            });
            receiveThread.start();

            System.out.println("Connecté au serveur UDP (" + ipserveur + ":" + port + ")");
            System.out.println("Tapez vos messages ");

            // Envoi des messages
            while (true) {
                System.out.print(" Vous : ");
                String msg = sc.nextLine();
                if (msg.equalsIgnoreCase("quit")) {
                    System.out.println(" Déconnexion...");
                    break;
                }

                String fullMsg = "[" + username + "] : " + msg;
                byte[] data = fullMsg.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, port);
                socket.send(packet);
            }

            socket.close();
            sc.close();
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
