package org.ensta.uml.sim.views;

public interface Observable {
    // Méthode permettant d'ajouter (abonner) un observateur.
    public boolean ajouterObservateur(Observateur o);

    // Méthode permettant de supprimer (résilier) un observateur.
    public boolean supprimerObservateur(Observateur o);

    // Méthode qui permet d'avertir tous les observateurs lors d'un changement
    // d'état.
    public void notifierObservateurs();
}
