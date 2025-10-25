package clientPackage;

import serverPackage.Operation;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Socket socket = new Socket("localhost", 1234);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connecté au serveur sur " + socket.getRemoteSocketAddress());

            // Lecture du message d'accueil
            String welcomeMessage = (String) in.readObject();
            System.out.println(welcomeMessage);

            // Boucle d'échange de messages
            while (true) {
                System.out.print("Vous : ");
                String message = sc.nextLine();
                
                if (message.equalsIgnoreCase("quit")) {
                    out.writeObject("quit");
                    out.flush();
                    break;
                }
                
                // Parser l'opération
                String[] parts = message.split(" ");
                if (parts.length != 3) {
                    System.out.println("Format invalide! ");
                    continue;
                }
                
                try {
                    double op1 = Double.parseDouble(parts[0]);
                    char opr = parts[1].charAt(0);
                    double op2 = Double.parseDouble(parts[2]);
                    
                    // Créer et envoyer l'opération
                    Operation operation = new Operation(op1, op2, opr);
                    out.writeObject(operation);
                    out.flush();
                    
                    // Recevoir le résultat
                    Operation resultOp = (Operation) in.readObject();
                    
                    if (Double.isNaN(resultOp.getR())) {
                        System.out.println("Serveur : Erreur");
                    } else {
                        System.out.println("Serveur : " + resultOp);
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Nombres invalides!");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}