package serverPackage;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ServeurUDP {
    private static final int port = 1234;
    private static final int bufsize = 1024;
    private DatagramSocket socket;
    // Ensemble des adresses clients (thread-safe)
    private Set<SocketAddress> clients = Collections.synchronizedSet(new HashSet<>());

    public ServeurUDP() throws SocketException {
        // Liaison à l’adresse locale et au port (InetSocketAddress)
        socket = new DatagramSocket(new InetSocketAddress("0.0.0.0", port));
        System.out.println("Serveur UDP démarré sur le port " + port);
    }

    public void start() {
        byte[] buffer = new byte[bufsize];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // bloquant

                // Conversion du message reçu en texte
                String received = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
                SocketAddress senderAddr = packet.getSocketAddress();

                // Affichage côté serveur
                System.out.printf("Reçu de %s : %s%n", senderAddr.toString(), received);

                // Ajout du client s’il n’est pas déjà enregistré
                if (!clients.contains(senderAddr)) {
                    clients.add(senderAddr);
                    System.out.println(" Nouveau client ajouté : " + senderAddr);
                }

                // Diffuser le message à tous les autres clients
                broadcastToOthers(received, senderAddr);

            } catch (IOException e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    private void broadcastToOthers(String message, SocketAddress sender) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
        } catch (Exception e) {
            data = message.getBytes();
        }

        synchronized (clients) {
            for (SocketAddress clientAddr : clients) {
                if (!clientAddr.equals(sender)) { // ne pas renvoyer à l’expéditeur
                    try {
                        DatagramPacket out = new DatagramPacket(data, data.length, clientAddr);
                        socket.send(out);
                    } catch (IOException e) {
                        System.err.println("Erreur en envoyant à " + clientAddr + " : " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServeurUDP server = new ServeurUDP();
            server.start();
        } catch (SocketException e) {
            System.err.println("❌ Impossible de démarrer le serveur : " + e.getMessage());
        }
    }
}
