package it.unibo.agar.server;

import it.unibo.agar.model.GameStateManager;
import it.unibo.agar.model.RemoteGameStateManager;
import it.unibo.agar.model.World;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteGameStateManagerImpl extends UnicastRemoteObject implements RemoteGameStateManager {

    // Riferimento alla vera logica di gioco locale
    private final GameStateManager localManager;

    public RemoteGameStateManagerImpl(GameStateManager manager) throws RemoteException {
        super();
        this.localManager = manager;
    }

    @Override
    public synchronized World getWorld() throws RemoteException {
        // Il client chiede il mondo, noi glielo diamo dal manager locale
        return localManager.getWorld();
    }

    @Override
    public synchronized void setPlayerDirection(String playerId, double dx, double dy) throws RemoteException {
        // Il client invia un input, noi lo passiamo al manager locale
        localManager.setPlayerDirection(playerId, dx, dy);
    }

    // Aggiungi l'implementazione del nuovo metodo
    @Override
    public synchronized void registerPlayer(String playerId) throws RemoteException {
        System.out.println(">>> NUOVA CONNESSIONE: Il giocatore " + playerId + " è entrato nel gioco!");
        localManager.addPlayer(playerId);
    }
}