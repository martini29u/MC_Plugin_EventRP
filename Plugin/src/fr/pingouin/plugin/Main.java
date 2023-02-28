package fr.pingouin.plugin;

import fr.pingouin.plugin.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "EventOrderRP plugin ON");
        getCommand("ping").setExecutor(new Commands());
        getCommand("eventCreate").setExecutor(new Commands());
        getCommand("eventEnd").setExecutor(new Commands());
        getCommand("eventNext").setExecutor(new Commands());
        getCommand("eventJoin").setExecutor(new Commands());
        getCommand("eventLeave").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "EventOrderRP plugin OFF");
    }

}
