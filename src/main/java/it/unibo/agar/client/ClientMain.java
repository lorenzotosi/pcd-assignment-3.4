package it.unibo.agar.client;

import it.unibo.agar.model.RemoteGameStateManager;
import it.unibo.agar.view.LocalView;

import javax.swing.*;
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
                // ... resto del codice uguale a prima ...
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