package it.unibo.agar.client;

import it.unibo.agar.model.*;

import java.rmi.RemoteException;
import java.util.Collections;

public class ClientGameStateManager implements GameStateManager {

    private final RemoteGameStateManager remoteManager;
    private World lastKnownWorld;

    public ClientGameStateManager(RemoteGameStateManager remoteManager) {
        this.remoteManager = remoteManager;
        // Stato iniziale di fallback in caso di errore immediato
        this.lastKnownWorld = new World(0, 0, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public World getWorld() {
        try {
            // Chiediamo al server lo stato attuale
            this.lastKnownWorld = remoteManager.getWorld();
            return this.lastKnownWorld;
        } catch (RemoteException e) {
            System.err.println("Errore di comunicazione con il server: " + e.getMessage());
            // In caso di errore, restituiamo l'ultimo stato valido per non far crashare la GUI
            return this.lastKnownWorld;
        }
    }

    @Override
    public void setPlayerDirection(String playerId, double dx, double dy) {
        try {
            remoteManager.setPlayerDirection(playerId, dx, dy);
        } catch (RemoteException e) {
            System.err.println("Impossibile inviare comandi al server: " + e.getMessage());
        }
    }

    @Override
    public void tick() {
        // IMPORTANTE: Il client NON calcola la fisica.
        // Il metodo tick() sul client non deve fare nulla.
        // La fisica è gestita interamente dal Server.
    }

    @Override
    public void addPlayer(String playerId) {
        // Il client locale non aggiunge player direttamente, lo chiede al server
        try {
            remoteManager.registerPlayer(playerId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayer(String playerId) {

    }
}