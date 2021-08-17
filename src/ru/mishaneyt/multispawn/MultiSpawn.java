package ru.mishaneyt.multispawn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiSpawn extends JavaPlugin {
   
    private FileConfiguration config;
    private File file;
    private Map<String, Location> spawns = new HashMap<String, Location>();
    private String error = ChatColor.translateAlternateColorCodes('&', "&cУ вас нет прав!");
   
    public void onEnable() {
        loadConfig();
        getCommand("spawn").setExecutor(this);
        getCommand("setspawn").setExecutor(this);
        System.out.println("[MultiSpawn] Plugin was enabled.");
    }
   
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("This commands for players");
            return false;
        }
        Player p = (Player) sender;
        switch (command.getName()) {
        case "spawn":
            if (p.hasPermission("multispawn.spawn")) {
                if (spawns.containsKey(p.getWorld().getName())) {
                    p.teleport(spawns.get(p.getWorld().getName()));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6MultiSpawn &8| &fВы были телепортированы на спавн!"));
                } else p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cВ этом мире не указана точка спавна!"));
            } else p.sendMessage(error);
            break;
        case "setspawn":
            if (p.hasPermission("multispawn.setspawn")) {
                setSpawn(p.getLocation());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6MultiSpawn &8| &fВы установили точку спавна, в мире: &a" + p.getWorld().getName()));
            } else p.sendMessage(error);
            break;

        default:
            break;
        }
        return false;
    }
   
    public void loadConfig() {
        config = new YamlConfiguration();
        file = new File(getDataFolder(), "spawns.yml");
        if (!file.exists()) {
            config.set("spawns." + getServer().getWorlds().get(0).getName(), getServer().getWorlds().get(0).getSpawnLocation().getBlockX() + " " +  getServer().getWorlds().get(0).getSpawnLocation().getBlockY() + " " +  getServer().getWorlds().get(0).getSpawnLocation().getBlockZ() + " " +  getServer().getWorlds().get(0).getSpawnLocation().getYaw() + " " +  getServer().getWorlds().get(0).getSpawnLocation().getPitch());
            try {
                config.save(file);
            } catch (IOException  e) {}
        } else {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {}
            Location spawnLoc = null;
            for (String world : config.getConfigurationSection("spawns").getKeys(false)) {
                String value = config.getString("spawns." + world);
                int x = Integer.parseInt(value.split(" ")[0]);
                int y = Integer.parseInt(value.split(" ")[1]);
                int z = Integer.parseInt(value.split(" ")[2]);
                float yaw = Float.parseFloat(value.split(" ")[3]);
                float pitch = Float.parseFloat(value.split(" ")[4]);
                spawnLoc = new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
                spawns.put(world, spawnLoc);
            }
        }
    }
   
    public void setSpawn(Location spawnLoc) {
        config.set("spawns." + spawnLoc.getWorld().getName(), spawnLoc.getBlockX() + " " +  spawnLoc.getBlockY() + " " +  spawnLoc.getBlockZ() + " " +  spawnLoc.getYaw() + " " +  spawnLoc.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {}
        spawns.put(spawnLoc.getWorld().getName(), spawnLoc);
    }

}
