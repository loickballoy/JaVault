package fr.epita.assistants.utils.front;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A timer class to help us apply our syntaxic theme in a fluid manner
 *
 * @author remy.decourcelle@epita.fr loick.balloy@epita.fr
 * @version 1.0
 */
public class UpdateTimer {
    // Variable globale à mettre à jour
    public static boolean globalVariable = false;

    public UpdateTimer(){
        startTimer();
    }


    // Fonction pour mettre à jour la variable globale après 2 secondes
    public static void updateGlobalVariable() {
        // Effectuer des opérations ou des calculs nécessaires ici

        // Mettre à jour la variable globale
        globalVariable = !globalVariable;
    }

    // Fonction pour démarrer le timer et lancer la mise à jour après 2 secondes
    public static void startTimer() {
        // Créer un objet Timer
        Timer timer = new Timer();

        // Créer une tâche TimerTask pour effectuer la mise à jour de la variable globale
        TimerTask task = new TimerTask() {
            public void run() {
                updateGlobalVariable();
            }
        };

        // Planifier la tâche pour s'exécuter après 2 secondes (2000 millisecondes)
        timer.schedule(task, 2000);
    }
}
