package it.unibo.agar.server;

import it.unibo.agar.model.*;
import it.unibo.agar.view.GlobalView; // Importa la vista

import javax.swing.SwingUtilities;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections; // Per liste vuote
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServerMain {

    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;
    private static final int NUM_FOODS = 100;
    private static final long GAME_TICK_MS = 30;

    public static void main(String[] args) {
        try {
            System.out.println("Inizializzazione del gioco...");

            // 1. Creiamo un mondo con SOLO CIBO, NIENTE PLAYER INIZIALI
            final List<Food> initialFoods = GameInitializer.initialFoods(NUM_FOODS, WORLD_WIDTH, WORLD_HEIGHT);
            // Lista giocatori vuota all'inizio
            final World initialWorld = new World(WORLD_WIDTH, WORLD_HEIGHT, Collections.emptyList(), initialFoods);

            final GameStateManager gameManager = new DefaultGameStateManager(initialWorld);

            System.out.println("Aggiunta dei Bot...");
            gameManager.addPlayer("bot1");
            gameManager.addPlayer("bot2");

            // 2. Configura RMI
            System.out.println("Avvio del registro RMI...");
            RemoteGameStateManager remoteManager = new RemoteGameStateManagerImpl(gameManager);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("AgarGame", remoteManager);
            System.out.println("Server RMI pronto! In attesa di client...");

            // 3. AVVIA LA GLOBAL VIEW (Schermata Server)
            SwingUtilities.invokeLater(() -> {
                GlobalView view = new GlobalView(gameManager);
                view.setVisible(true);

                // Aggiungiamo il timer per aggiornare la grafica e la fisica insieme
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        AIMovement.moveAI("bot1", gameManager);
                        AIMovement.moveAI("bot2", gameManager);
                        // Fisica
                        gameManager.tick();

                        // Grafica Server
                        view.repaintView();
                    }
                }, 0, GAME_TICK_MS);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}