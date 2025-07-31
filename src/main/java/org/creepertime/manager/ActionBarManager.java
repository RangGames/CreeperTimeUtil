package org.creepertime.manager;

import wiki.creeper.creeperTimeUtil.CreeperTimeUtil;
import org.creepertime.task.ActionBarTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ActionBarManager {
    private final CreeperTimeUtil plugin;
    private boolean enabled;
    private int updateInterval;
    private String defaultFormat;
    private final Map<String, WorldActionBarConfig> worldConfigs;
    private ActionBarTask actionBarTask;
    
    public ActionBarManager(CreeperTimeUtil plugin) {
        this.plugin = plugin;
        this.worldConfigs = new HashMap<>();
        loadConfiguration();
    }
    
    public void loadConfiguration() {
        ConfigurationSection actionBarConfig = plugin.getConfig().getConfigurationSection("actionbar");
        if (actionBarConfig == null) {
            enabled = false;
            return;
        }
        
        enabled = actionBarConfig.getBoolean("enabled", true);
        updateInterval = actionBarConfig.getInt("update-interval", 20);
        defaultFormat = actionBarConfig.getString("format", "&7Day &e%creepertimeutil_day% &7| &f%creepertimeutil_formatted_time_12h%");
        
        // 월드별 설정 로드
        worldConfigs.clear();
        ConfigurationSection worldFormats = actionBarConfig.getConfigurationSection("world-formats");
        if (worldFormats != null) {
            for (String worldName : worldFormats.getKeys(false)) {
                ConfigurationSection worldSection = worldFormats.getConfigurationSection(worldName);
                if (worldSection != null) {
                    boolean worldEnabled = worldSection.getBoolean("enabled", true);
                    String worldFormat = worldSection.getString("format", defaultFormat);
                    worldConfigs.put(worldName, new WorldActionBarConfig(worldEnabled, worldFormat));
                }
            }
        }
    }
    
    public void start() {
        if (!enabled) {
            return;
        }
        
        if (actionBarTask != null) {
            actionBarTask.cancel();
        }
        
        actionBarTask = new ActionBarTask(plugin, this);
        actionBarTask.runTaskTimer(plugin, 0, updateInterval);
    }
    
    public void stop() {
        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }
    }
    
    public void reload() {
        stop();
        loadConfiguration();
        start();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isEnabledForWorld(World world) {
        if (!enabled) {
            return false;
        }
        
        WorldActionBarConfig worldConfig = worldConfigs.get(world.getName());
        if (worldConfig != null) {
            return worldConfig.enabled;
        }
        
        return true; // 월드별 설정이 없으면 기본적으로 활성화
    }
    
    public String getFormat(World world) {
        WorldActionBarConfig worldConfig = worldConfigs.get(world.getName());
        if (worldConfig != null && worldConfig.enabled) {
            return worldConfig.format;
        }
        
        return defaultFormat;
    }
    
    private static class WorldActionBarConfig {
        private final boolean enabled;
        private final String format;
        
        public WorldActionBarConfig(boolean enabled, String format) {
            this.enabled = enabled;
            this.format = format;
        }
    }
}