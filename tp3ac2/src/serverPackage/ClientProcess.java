package serverPackage;

import java.io.*;
import java.net.*;

class ClientProcess extends Thread {
    private Socket socket;
    private int n;

    public ClientProcess(Socket socket, int n) {
        this.socket = socket;
        this.n = n;
    }

    @Override
    public void run() {
        try {
            // Création des flux d'entrée/sortie pour objets
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Message d'accueil envoyé au client
            out.writeObject("Bienvenue ! Vous êtes le client n°" + n + "Tapez 'quit' pour quitter");
            out.flush();

            // Lecture des opérations envoyées par le client
            Object message;
            while ((message = in.readObject()) != null) {
                if (message instanceof String) {
                    String strMessage = (String) message;
                    if (strMessage.equalsIgnoreCase("quit")) {
                        System.out.println("Client n°" + n + " a demandé à quitter.");
                        break;
                    }
                } else if (message instanceof Operation) {
                    Operation operation = (Operation) message;
                    
                    // Traiter l'opération
                    processOperation(operation);
                    
                    System.out.println("Client " + n + " : " + operation);
                    
                    // Afficher le compteur global
                    synchronized (ServeurMT.class) {
                        int currentCount = ServeurMT.getGlobalCounter();
                        System.out.println("Compteur global: " + currentCount + " opérations traitées");
                    }
                    
                    // Renvoyer l'opération avec résultat au client
                    out.writeObject(operation);
                    out.flush();
                }
            }

            // Fermeture des ressources
            in.close();
            out.close();
            socket.close();
            System.out.println("Client n°" + n + " déconnecté.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur avec le client n°" + n + " : " + e.getMessage());
        }
    }
    
    private void processOperation(Operation operation) {
        double op1 = operation.getOp1();
        double op2 = operation.getOp2();
        char opr = operation.getOpr();
        double r = 0;
        boolean validOperation = true;
        
        // Incrémenter le compteur global de manière synchronisée
        synchronized (ServeurMT.class) {
            int oprId = ServeurMT.getNextOperationId();
            operation.setOprId(oprId);
        }
        
		switch (opr) {
            case '+':
                r = op1 + op2;
                break;
            case '-':
                r = op1 - op2;
                break;
            case '*':
                r = op1 * op2;
                break;
            case '/':
                if (op2 != 0) {
                    r = op1 / op2;
                } else {
                    r = Double.NaN;
                    validOperation = false;
                }
                break;
            default:
                r = Double.NaN;
                validOperation = false;
                break;
        }
        
        operation.setR(r);
        
        if (!validOperation) {
            System.out.println("Opération invalide de Client " + n + ": " + op1 + " " + opr + " " + op2);
        }
    }
}