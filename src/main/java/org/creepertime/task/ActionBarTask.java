package org.creepertime.task;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import wiki.creeper.creeperTimeUtil.CreeperTimeUtil;
import org.creepertime.manager.ActionBarManager;
import wiki.creeper.creeperTimeUtil.api.ServerClockAPI;
import wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI;

public class ActionBarTask extends BukkitRunnable {
    private final CreeperTimeUtil plugin;
    private final ActionBarManager actionBarManager;
    
    public ActionBarTask(CreeperTimeUtil plugin, ActionBarManager actionBarManager) {
        this.plugin = plugin;
        this.actionBarManager = actionBarManager;
    }
    
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!actionBarManager.isEnabledForWorld(player.getWorld())) {
                continue;
            }
            
            String format = actionBarManager.getFormat(player.getWorld());
            String message = formatMessage(format, player);
            
            sendActionBar(player, message);
        }
    }
    
    private String formatMessage(String format, Player player) {
        String message = format;
        
        // 기본 플레이스홀더 처리
        message = replacePlaceholders(message, player);
        
        // 색상 코드 변환
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        return message;
    }
    
    private String replacePlaceholders(String message, Player player) {
        // 현재 시간 가져오기
        int day = ServerClockAPI.getDay();
        int hour = ServerClockAPI.getHour();
        int minute = ServerClockAPI.getMinute();
        
        // 기본 플레이스홀더 처리
        message = message.replace("%creepertimeutil_day%", String.valueOf(day));
        message = message.replace("%creepertimeutil_hour%", String.valueOf(hour));
        message = message.replace("%creepertimeutil_minute%", String.valueOf(minute));
        message = message.replace("%creepertimeutil_formatted_time%", String.format("%02d:%02d", hour, minute));
        
        // 12시간 형식
        String period = hour < 12 ? "AM" : "PM";
        int hour12 = hour;
        if (hour == 0) {
            hour12 = 12;
        } else if (hour > 12) {
            hour12 = hour - 12;
        }
        message = message.replace("%creepertimeutil_period%", period);
        message = message.replace("%creepertimeutil_formatted_time_12h%", String.format("%d:%02d %s", hour12, minute, period));
        
        // 요일, 월, 연도
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        message = message.replace("%creepertimeutil_day_name%", dayNames[day % 7]);
        message = message.replace("%creepertimeutil_month%", String.valueOf((day / 30) + 1));
        message = message.replace("%creepertimeutil_year%", String.valueOf((day / 365) + 1));
        
        // 월드별 시간
        if (player.getWorld() != null && WorldTimeZoneAPI.isWorldTimeZoneEnabled(player.getWorld().getName())) {
            int[] worldTime = WorldTimeZoneAPI.getWorldTime(player.getWorld().getName());
            message = message.replace("%creepertimeutil_world_time%", 
                String.format("%02d:%02d", worldTime[1], worldTime[2]));
        } else {
            message = message.replace("%creepertimeutil_world_time%", 
                String.format("%02d:%02d", hour, minute));
        }
        
        return message;
    }
    
    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}