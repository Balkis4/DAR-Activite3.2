package serverPackage;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServeurMT extends Thread {
    // Compteur pour compter les clients
    private static int c = 0;
    // Compteur global pour les opérations 
    private static AtomicInteger globalCounter = new AtomicInteger(0);

    // pour incrémenter le compteur clients
    private static int clientSuivant() {
        return ++c;
    }
    
    // Méthode pour incrémenter le compteur d'opérations
    public static int getNextOperationId() {
        return globalCounter.incrementAndGet();
    }
    
    // Méthode pour obtenir le compteur global
    public static int getGlobalCounter() {
        return globalCounter.get();
    }

    public static void main(String[] args) {
        // Créer et démarrer le thread du serveur
        new ServeurMT().start();
    }

    @Override
    public void run() {
        try (ServerSocket socketServeur = new ServerSocket(1234)) {
            System.out.println("Serveur Calculatrice Multi-threads démarré sur le port 1234");
            System.out.println("En attente de connexions clients...");

            // Boucle infinie pour accepter plusieurs clients
            while (true) {
                Socket socket = socketServeur.accept(); // Attente d'un client
                int n = clientSuivant(); // Numéro unique

                // Afficher les infos du client
                System.out.println("Client n°" + n + " connecté depuis " + socket.getRemoteSocketAddress());

                // Créer un thread pour ce client
                new ClientProcess(socket, n).start();
            }

        } catch (IOException e) {
            System.out.println("Erreur du serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}