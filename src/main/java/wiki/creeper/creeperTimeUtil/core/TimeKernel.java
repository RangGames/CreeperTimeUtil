package wiki.creeper.creeperTimeUtil.core;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import wiki.creeper.creeperTimeUtil.events.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class TimeKernel {
    private static TimeKernel instance;
    private final JavaPlugin plugin;
    
    private long totalMinutes = 0;
    private int lastHour = -1;
    private int lastDay = -1;
    private int lastWeek = -1;
    private int lastMonth = -1;
    private ServerTimeOfDayEvent.TimeOfDay lastTimeOfDay = null;
    
    private BukkitTask timerTask;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<String, Long> cooldownDurations = new ConcurrentHashMap<>();
    
    private static final long TICKS_PER_SECOND = 20L;
    private static final long DEFAULT_REAL_SECONDS_PER_MINECRAFT_MINUTE = 17L;
    private static final String DATA_FILE_NAME = "timedata.dat";
    
    private double timeSpeed = 1.0; // 시간 속도 배율 (1.0 = 기본속도)
    private boolean timePaused = false; // 시간 일시정지 여부
    private long realSecondsPerMinute = DEFAULT_REAL_SECONDS_PER_MINECRAFT_MINUTE;
    
    public TimeKernel(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }
    
    public static TimeKernel getInstance() {
        return instance;
    }
    
    public void start() {
        loadTimeData();
        startTimer();
        updateLastValues();
    }
    
    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        saveTimeData();
    }
    
    private void startTimer() {
        updateTimerInterval();
    }
    
    private void updateTimerInterval() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        
        if (timePaused) {
            return;
        }
        
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                incrementMinute();
            }
        }.runTaskTimerAsynchronously(plugin, TICKS_PER_SECOND * realSecondsPerMinute, 
                                             TICKS_PER_SECOND * realSecondsPerMinute);
    }
    
    private void incrementMinute() {
        totalMinutes++;
        
        // Fire minute change event
        Bukkit.getScheduler().runTask(plugin, () -> {
            ServerMinuteChangeEvent minuteEvent = new ServerMinuteChangeEvent(totalMinutes);
            Bukkit.getPluginManager().callEvent(minuteEvent);
            
            // Update visual time
            updateVisualTime();
            
            // Check for hour change
            int currentHour = getHour();
            if (currentHour != lastHour) {
                lastHour = currentHour;
                ServerHourChangeEvent hourEvent = new ServerHourChangeEvent(currentHour, getDay());
                Bukkit.getPluginManager().callEvent(hourEvent);
            }
            
            // Check for day change
            int currentDay = getDay();
            if (currentDay != lastDay) {
                lastDay = currentDay;
                ServerDayChangeEvent dayEvent = new ServerDayChangeEvent(currentDay);
                Bukkit.getPluginManager().callEvent(dayEvent);
                
                // Check for week change (every Monday)
                if (dayEvent.getDayOfWeek() == 1) {
                    int currentWeek = dayEvent.getWeek();
                    if (currentWeek != lastWeek) {
                        lastWeek = currentWeek;
                        ServerWeekChangeEvent weekEvent = new ServerWeekChangeEvent(currentWeek, currentDay);
                        Bukkit.getPluginManager().callEvent(weekEvent);
                    }
                }
                
                // Check for month change (every 30 days)
                int currentMonth = getMonth();
                if (currentMonth != lastMonth) {
                    lastMonth = currentMonth;
                    int currentYear = getYear();
                    ServerMonthChangeEvent monthEvent = new ServerMonthChangeEvent(currentMonth, currentYear);
                    Bukkit.getPluginManager().callEvent(monthEvent);
                }
            }
            
            // Check for time of day changes
            checkTimeOfDay(currentHour);
        });
    }
    
    private void updateVisualTime() {
        // Update each world based on its timezone
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                // Check if world has custom timezone
                if (wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI.getWorldTimeOffset(world) != 0 ||
                    wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI.getWorldTimeSpeed(world) != 1.0) {
                    // Use world-specific time
                    wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI.updateWorldVisualTime(world);
                } else {
                    // Use server time
                    long minutesInDay = totalMinutes % 1440;
                    long ticks = (minutesInDay * 24000) / 1440;
                    world.setTime(ticks);
                }
            }
        }
    }
    
    private void updateLastValues() {
        lastHour = getHour();
        lastDay = getDay();
        lastWeek = getWeek();
        lastMonth = getMonth();
    }
    
    private void checkTimeOfDay(int hour) {
        ServerTimeOfDayEvent.TimeOfDay currentTimeOfDay = null;
        
        // Determine current time of day
        if (hour == 0) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.MIDNIGHT;
        else if (hour == 5) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.DAWN;
        else if (hour == 6) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.MORNING;
        else if (hour == 12) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.NOON;
        else if (hour == 18) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.DUSK;
        else if (hour == 22) currentTimeOfDay = ServerTimeOfDayEvent.TimeOfDay.NIGHT;
        
        // Fire event if time of day changed
        if (currentTimeOfDay != null && currentTimeOfDay != lastTimeOfDay) {
            lastTimeOfDay = currentTimeOfDay;
            ServerTimeOfDayEvent timeOfDayEvent = new ServerTimeOfDayEvent(currentTimeOfDay, getDay());
            Bukkit.getPluginManager().callEvent(timeOfDayEvent);
        }
    }
    
    // Time calculation methods
    public long getTotalMinutes() {
        return totalMinutes;
    }
    
    public int getDay() {
        return (int) (totalMinutes / 1440) + 1; // Day starts at 1
    }
    
    public int getHour() {
        long minutesInDay = totalMinutes % 1440;
        return (int) (minutesInDay / 60);
    }
    
    public int getMinute() {
        return (int) (totalMinutes % 60);
    }
    
    public String getFormattedTime() {
        return String.format("%d일차 %02d:%02d", getDay(), getHour(), getMinute());
    }
    
    public int getWeek() {
        return ((getDay() - 1) / 7) + 1;
    }
    
    public int getMonth() {
        return ((getDay() - 1) / 30) + 1;
    }
    
    public int getYear() {
        return ((getDay() - 1) / 360) + 1;
    }
    
    // Cooldown management - New methods with duration
    public void setCooldown(String uniqueId, long durationInSeconds) {
        cooldowns.put(uniqueId, System.currentTimeMillis());
        cooldownDurations.put(uniqueId, durationInSeconds * 1000L); // Convert to milliseconds
    }
    
    public boolean isCooldownOver(String uniqueId) {
        Long startTime = cooldowns.get(uniqueId);
        Long duration = cooldownDurations.get(uniqueId);
        if (startTime == null || duration == null) {
            return true;
        }
        return (System.currentTimeMillis() - startTime) >= duration;
    }
    
    public long getRemainingCooldownSeconds(String uniqueId) {
        Long startTime = cooldowns.get(uniqueId);
        Long duration = cooldownDurations.get(uniqueId);
        if (startTime == null || duration == null) {
            return 0;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= duration) {
            return 0;
        }
        
        return (duration - elapsed) / 1000L; // Convert back to seconds
    }
    
    public void removeCooldown(String uniqueId) {
        cooldowns.remove(uniqueId);
        cooldownDurations.remove(uniqueId);
    }
    
    // Legacy cooldown methods (game time based)
    public void setGameTimeCooldown(String uniqueId) {
        cooldowns.put(uniqueId, totalMinutes);
    }
    
    public boolean isGameTimeCooldownOver(String uniqueId, long durationInMinutes) {
        Long startTime = cooldowns.get(uniqueId);
        if (startTime == null) {
            return true;
        }
        return (totalMinutes - startTime) >= durationInMinutes;
    }
    
    public long getRemainingGameTimeCooldownMinutes(String uniqueId, long totalDurationInMinutes) {
        Long startTime = cooldowns.get(uniqueId);
        if (startTime == null) {
            return 0;
        }
        
        long elapsed = totalMinutes - startTime;
        if (elapsed >= totalDurationInMinutes) {
            return 0;
        }
        
        return totalDurationInMinutes - elapsed;
    }
    
    // Data persistence
    private void loadTimeData() {
        File dataFile = new File(plugin.getDataFolder(), DATA_FILE_NAME);
        if (!dataFile.exists()) {
            totalMinutes = 0;
            return;
        }
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(dataFile))) {
            totalMinutes = dis.readLong();
            
            // Load cooldowns
            int cooldownCount = dis.readInt();
            for (int i = 0; i < cooldownCount; i++) {
                String id = dis.readUTF();
                long time = dis.readLong();
                cooldowns.put(id, time);
            }
            
            plugin.getLogger().info("시간 데이터 로드 완료: " + getFormattedTime());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "시간 데이터 로드 실패", e);
            totalMinutes = 0;
        }
    }
    
    private void saveTimeData() {
        File dataFile = new File(plugin.getDataFolder(), DATA_FILE_NAME);
        
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(dataFile))) {
            dos.writeLong(totalMinutes);
            
            // Save cooldowns
            dos.writeInt(cooldowns.size());
            for (Map.Entry<String, Long> entry : cooldowns.entrySet()) {
                dos.writeUTF(entry.getKey());
                dos.writeLong(entry.getValue());
            }
            
            plugin.getLogger().info("시간 데이터 저장 완료: " + getFormattedTime());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "시간 데이터 저장 실패", e);
        }
    }
    
    // 시간 제어 메소드
    public void setTimeSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("시간 속도는 0보다 커야 합니다");
        }
        this.timeSpeed = speed;
        this.realSecondsPerMinute = (long) (DEFAULT_REAL_SECONDS_PER_MINECRAFT_MINUTE / speed);
        updateTimerInterval();
    }
    
    public double getTimeSpeed() {
        return timeSpeed;
    }
    
    public void pauseTime() {
        this.timePaused = true;
        updateTimerInterval();
    }
    
    public void resumeTime() {
        this.timePaused = false;
        updateTimerInterval();
    }
    
    public boolean isTimePaused() {
        return timePaused;
    }
    
    public void setTime(int day, int hour, int minute) {
        if (day < 1) {
            throw new IllegalArgumentException("일차는 1 이상이어야 합니다");
        }
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("시간은 0-23 사이여야 합니다");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("분은 0-59 사이여야 합니다");
        }
        
        this.totalMinutes = ((day - 1) * 1440L) + (hour * 60L) + minute;
        updateVisualTime();
        updateLastValues();
    }
    
    // 공개 메소드 - 자동 저장용
    public void saveData() {
        saveTimeData();
    }
}