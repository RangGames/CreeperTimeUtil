package wiki.creeper.creeperTimeUtil.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wiki.creeper.creeperTimeUtil.api.PlayerTimeAPI;

public class PlayerTimeListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerTimeAPI.onPlayerJoin(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerTimeAPI.onPlayerQuit(event.getPlayer());
    }
}