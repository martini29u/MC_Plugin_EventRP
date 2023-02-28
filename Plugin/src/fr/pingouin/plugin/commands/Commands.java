package fr.pingouin.plugin.commands;

/* ****************************************** */
/*         Auteur : Martini Florent           */
/* ****************************************** */

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        /* ****************************************** */
        /*           Commande d'activité              */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("ping")) sender.sendMessage(ChatColor.BLUE + "EventOrderRP is enable !");

        /* ****************************************** */
        /*     Commande de création de l'event        */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("eventCreate")) {
            if(args.length < 2) { //Si le nombre de paramètre est < à 2
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Ajoutez au moins deux membres de l'event en paramètre.");
                return true;
            }
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getMainScoreboard();

            Objective objective = null;
            String eventName = " ";
            for(int i = 0; i<Data.listEvent.size()+1; i++) { //Boucle infinie
                if(scoreboard.getObjective("event"+ i)==null) { //On cherche le premier event disponible
                    eventName = "event"+i;  //Nom de l'event
                    ArrayList<String> eventArray = new ArrayList<>();   //Création de l'array qui va stocker l'event
                    eventArray.add(eventName);                          //On ajoute le nom de l'event au début
                    eventArray.add(args[0]);                            //On ajoute le Joueur qui doit jouer ensuite
                    eventArray.addAll(Arrays.asList(args));             //On ajoute finalement tous les joueurs donnés en paramètre.
                    Data.listEvent.add(eventArray);           //On ajoute l'array dans notre array global.
                    objective = scoreboard.registerNewObjective(eventName, "dummy");    //On créer l'event dans MC
                    break;
                }
            }

            assert objective != null;

            //Affichage du scoreboard
            objective.setDisplayName(ChatColor.DARK_RED + "Ordre " + eventName);    //Titre
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);                          //Type d'affichage

            Score score;
            for(int i=0; i<args.length; i++) { //On parcourt tous les joueurs de l'event
                if(i==0) { //Juste pour le premier joueur -> A lui de jouer
                    score = objective.getScore(ChatColor.RED + args[i]);    //Affichage du pseudo en rouge
                }
                else score = objective.getScore(args[i]);                      //Pour les autres, on affiche sans couleur
                score.setScore(args.length-i);                                 //On ajoute le score dans l'ordre décroissant -> Gère l'ordre d'affichage.
                if(Bukkit.getPlayer(args[i]) == null) sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Joueur " + args[i] + " non reconnue."); //Si le joueur n'est pas trouvé
                else {
                    Bukkit.getPlayer(args[i]).setScoreboard(scoreboard); //Sinon on lui affiche le scoreboard
                }
            }
        }

        /* ****************************************** */
        /*     Commande pour supprimer l'event        */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("eventEnd")) {
            if(args.length != 1) { //Paramètre différent de 1
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Ajoutez uniquement le nom de l'event à supprimer en paramètre.");
                return true;
            }
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getMainScoreboard();

            if(scoreboard.getObjective(args[0]) == null) { //Si event inconnu
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Event non reconnue.");
                return true;
            }

            for(int i = 0; i<Data.listEvent.size(); i++) { //On parcourt tous les event qu'on connait
                if(Data.listEvent.get(i).get(0).equals(args[0])) { //Une fois qu'on trouve le bon
                    Data.listEvent.remove(i); //On le supprime
                    break;
                }
            }

            scoreboard.getObjective(args[0]).unregister(); //On supprime l'event sur MC
            sender.sendMessage(ChatColor.BLUE + "Event supprimé.");
        }

        /* ****************************************** */
        /*  Commande pour passer au joueur suivant    */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("eventNext")) {
            if(args.length != 1) { //Paramètre différent de 1
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Ajoutez uniquement le nom de l'event associé en paramètre.");
                return true;
            }
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getMainScoreboard();
            scoreboard.getObjective(args[0]).unregister(); //On supprime l'event MC...

            //Pour le refaire
            affichageScoreboard(scoreboard, args[0], sender, true);
        }

        /* ****************************************** */
        /*      Commande pour rejoindre l'event       */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("eventJoin")) {
            if(!(sender instanceof Player)) return true;
            Player p = (Player) sender;

            if(args.length != 1) { //Paramètre différent de 1
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Ajoutez uniquement le nom de l'event associé en paramètre.");
                return true;
            }

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getMainScoreboard();

            if(scoreboard.getObjective(args[0]) == null) { //Si event inconnu
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Event non reconnue.");
                return true;
            }

            for(int i = 0; i<Data.listEvent.size(); i++) { //On parcourt tous les event qu'on connait
                if(Data.listEvent.get(i).get(0).equals(args[0])) { //Une fois qu'on trouve le bon
                    Data.listEvent.get(i).add(p.getDisplayName()); //On ajoute le nouveau Joueur
                    break;
                }
            }

            scoreboard.getObjective(args[0]).unregister(); //On supprime l'event MC...
            //Pour le refaire
            affichageScoreboard(scoreboard, args[0], sender, false);
        }

        /* ****************************************** */
        /*       Commande pour quitter l'event.       */
        /* ****************************************** */

        if(cmd.getName().equalsIgnoreCase("eventLeave")) {
            if(!(sender instanceof Player)) return true;
            Player p = (Player) sender;

            if(args.length != 1) { //Paramètre différent de 1
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Ajoutez uniquement le nom de l'event associé en paramètre.");
                return true;
            }

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getMainScoreboard();

            if(scoreboard.getObjective(args[0]) == null) { //Si event inconnu
                sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Event non reconnue.");
                return true;
            }

            for(int i = 0; i<Data.listEvent.size(); i++) { //On parcourt tous les event qu'on connait
                if(Data.listEvent.get(i).get(0).equals(args[0])) { //Une fois qu'on trouve le bon
                    for(int j=2; j<Data.listEvent.get(i).size(); j++) { //On parcourt les joueurs de l'event
                        if(Data.listEvent.get(i).get(j).equals(p.getDisplayName())) { //Une fois qu'on trouve le bon
                            if(Data.listEvent.get(i).get(1).equals(Data.listEvent.get(i).get(j))) { //On regarde si il est le joueur courant, si oui, on fait un next avant
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eventNext " + Data.listEvent.get(i).get(0));
                            }
                            Data.listEvent.get(i).remove(j); //Et on le supprime
                            break;
                        }
                    }
                    break;
                }
            }

            scoreboard.getObjective(args[0]).unregister(); //On supprime l'event MC...
            //Pour le refaire
            affichageScoreboard(scoreboard, args[0], sender, false);
        }

        return false;
    }

    private void affichageScoreboard(Scoreboard scoreboard, String eventname, CommandSender sender, boolean next) {
        Objective objective = scoreboard.registerNewObjective(eventname, "dummy");
        objective.setDisplayName(ChatColor.DARK_RED + "Ordre " + eventname);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int indice = 0;
        for(int i = 0; i<Data.listEvent.size(); i++) { //On récupère l'indice de l'event en question dans notre array
            if(Data.listEvent.get(i).get(0).equals(eventname)) {
                indice = i;
                break;
            }
        }

        String currentPlayer = Data.listEvent.get(indice).get(1);
        if(next) { //On passe au joueur suivant
            for (int i = 2; i < Data.listEvent.get(indice).size(); i++) { //On récupère l'indice du prochain joueur qui devra jouer
                if (Data.listEvent.get(indice).get(1).equals(Data.listEvent.get(indice).get(i))) {
                    if (i == Data.listEvent.get(indice).size() - 1) currentPlayer = Data.listEvent.get(indice).get(2);
                    else currentPlayer = Data.listEvent.get(indice).get(i+1);
                    break;
                }
            }
        }

        Score score;
        for(int i = 2; i<Data.listEvent.get(indice).size(); i++) { //On parcourt tous les joueurs de l'event
            if(Data.listEvent.get(indice).get(i).equals(currentPlayer)) { //Juste pour joueur qui doit jouer
                score = objective.getScore(ChatColor.RED + Data.listEvent.get(indice).get(i));  //Affichage du pseudo en rouge
                Data.listEvent.get(indice).set(1, Data.listEvent.get(indice).get(i));              //On met à jour le joueur qui doit jouer dans l'array
            }
            else score = objective.getScore(Data.listEvent.get(indice).get(i));                    //Pour les autres, on affiche sans couleur
            score.setScore(Data.listEvent.get(indice).size()-i);                                   //On ajoute le score dans l'ordre décroissant -> Gère l'ordre d'affichage.
            if(Bukkit.getPlayer(Data.listEvent.get(indice).get(i)) == null) sender.sendMessage(ChatColor.DARK_BLUE + "Erreur : Joueur " + Data.listEvent.get(indice).get(i) + " non reconnu."); //Si le joueur n'est pas trouvé
            else {
                Bukkit.getPlayer(Data.listEvent.get(indice).get(i)).setScoreboard(scoreboard); //Sinon on lui affiche son scoreboard
            }
        }
    }
}
