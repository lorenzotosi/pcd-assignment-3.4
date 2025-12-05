package it.unibo.agar.client;

import it.unibo.agar.model.RemoteGameStateManager;
import it.unibo.agar.view.LocalView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ClientMain {
    public static void main(String[] args) {
        try {
            String playerId = args.length > 0 ? args[0] : "p1";
            String host = args.length > 1 ? args[1] : "localhost";

            Registry registry = LocateRegistry.getRegistry(host, 1099);
            RemoteGameStateManager remoteManager = (RemoteGameStateManager) registry.lookup("AgarGame");
            ClientGameStateManager clientManager = new ClientGameStateManager(remoteManager);

            // *** PASSAGGIO FONDAMENTALE: REGISTRAZIONE ***
            System.out.println("Richiesta di spawn per: " + playerId);
            clientManager.addPlayer(playerId); // Questo chiama registerPlayer sul server

            SwingUtilities.invokeLater(() -> {
                LocalView view = new LocalView(clientManager, playerId);
                view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                view.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        try {
                            System.out.println("Chiusura in corso... rimozione dal server.");
                            // Chiamata remota per rimuovere il player
                            remoteManager.leaveGame(playerId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            // Chiudi l'applicazione
                            System.exit(0);
                        }
                    }
                });
                view.setVisible(true);

                new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {
                    @Override
                    public void run() {
                        view.repaintView();
                    }
                }, 0, 30);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}