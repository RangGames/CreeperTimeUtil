package wiki.creeper.creeperTimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import wiki.creeper.creeperTimeUtil.api.PlayerTimeAPI;
import wiki.creeper.creeperTimeUtil.api.ServerClockAPI;
import wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI;
import wiki.creeper.creeperTimeUtil.core.TimeKernel;
import wiki.creeper.creeperTimeUtil.debug.TimeDebugger;
import wiki.creeper.creeperTimeUtil.listeners.PlayerTimeListener;

public final class CreeperTimeUtil extends JavaPlugin {
    
    private TimeKernel timeKernel;

    @Override
    public void onEnable() {
        // Config 로드
        saveDefaultConfig();
        loadConfiguration();
        
        // TimeKernel 초기화 및 시작
        timeKernel = new TimeKernel(this);
        
        // Config에서 시작 시간 설정 적용
        applyStartTimeConfig();
        
        timeKernel.start();
        
        // 플레이어 시간 리스너 등록
        Bukkit.getPluginManager().registerEvents(new PlayerTimeListener(), this);
        
        // 이미 접속 중인 플레이어 처리
        Bukkit.getOnlinePlayers().forEach(PlayerTimeAPI::onPlayerJoin);
        
        // 자동 저장 스케줄러 시작
        startAutoSaveTask();
        
        getLogger().info("CreeperTimeUtil 플러그인이 활성화되었습니다.");
        getLogger().info("현재 서버 시간: " + timeKernel.getFormattedTime());
        
        if (TimeDebugger.isDebugMode()) {
            getLogger().info("디버그 모드가 활성화되어 있습니다.");
        }
    }

    @Override
    public void onDisable() {
        // 플레이어 데이터 저장
        PlayerTimeAPI.saveAllPlayerData();
        
        // TimeKernel 정지 및 데이터 저장
        if (timeKernel != null) {
            timeKernel.stop();
        }
        
        getLogger().info("CreeperTimeUtil 플러그인이 비활성화되었습니다.");
    }
    
    private void loadConfiguration() {
        // 디버그 설정
        boolean debugEnabled = getConfig().getBoolean("debug.enabled", false);
        TimeDebugger.setDebugMode(debugEnabled);
        
        // 시간 설정
        double defaultSpeed = getConfig().getDouble("time.default-speed", 1.0);
        if (defaultSpeed > 0) {
            // 서버 시작 후 적용
            Bukkit.getScheduler().runTaskLater(this, () -> {
                ServerClockAPI.setTimeSpeed(defaultSpeed);
            }, 20L);
        }
        
        // 월드별 시간대 설정
        if (getConfig().getBoolean("worlds.timezone-enabled", true)) {
            loadWorldTimeZones();
        }
    }
    
    private void loadWorldTimeZones() {
        ConfigurationSection worldsSection = getConfig().getConfigurationSection("worlds");
        if (worldsSection == null) return;
        
        for (String worldName : worldsSection.getKeys(false)) {
            if (worldName.equals("timezone-enabled")) continue;
            
            ConfigurationSection worldConfig = worldsSection.getConfigurationSection(worldName);
            if (worldConfig == null || !worldConfig.getBoolean("enabled", false)) continue;
            
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                getLogger().warning("월드를 찾을 수 없습니다: " + worldName);
                continue;
            }
            
            // 시간대 프리셋 확인
            if (worldConfig.contains("timezone")) {
                String timezoneName = worldConfig.getString("timezone");
                try {
                    WorldTimeZoneAPI.TimeZone timezone = WorldTimeZoneAPI.TimeZone.valueOf(timezoneName);
                    WorldTimeZoneAPI.setWorldTimeZone(world, timezone);
                    getLogger().info(worldName + " 월드에 " + timezone.getKoreanName() + " 시간대가 적용되었습니다.");
                } catch (IllegalArgumentException e) {
                    getLogger().warning("알 수 없는 시간대: " + timezoneName);
                }
            } else {
                // 커스텀 오프셋과 속도 적용
                long offset = worldConfig.getLong("offset", 0);
                double speed = worldConfig.getDouble("speed", 1.0);
                
                WorldTimeZoneAPI.setWorldTimeOffset(world, offset);
                WorldTimeZoneAPI.setWorldTimeSpeed(world, speed);
                
                getLogger().info(String.format("%s 월드에 커스텀 시간대가 적용되었습니다. (오프셋: %d분, 속도: %.2fx)", 
                    worldName, offset, speed));
            }
        }
    }
    
    private void applyStartTimeConfig() {
        int startDay = getConfig().getInt("time.start-day", -1);
        int startHour = getConfig().getInt("time.start-hour", -1);
        int startMinute = getConfig().getInt("time.start-minute", -1);
        
        if (startDay > 0 && startHour >= 0 && startMinute >= 0) {
            ServerClockAPI.setTime(startDay, startHour, startMinute);
            getLogger().info(String.format("시작 시간이 설정되었습니다: %d일차 %02d:%02d", 
                startDay, startHour, startMinute));
        }
    }
    
    private void startAutoSaveTask() {
        int interval = getConfig().getInt("data.auto-save-interval", 30);
        if (interval <= 0) return;
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (timeKernel != null) {
                timeKernel.saveData();
                TimeDebugger.log("자동 저장이 완료되었습니다.");
            }
        }, interval * 60L * 20L, interval * 60L * 20L);
    }
    
    public void reloadConfiguration() {
        reloadConfig();
        WorldTimeZoneAPI.resetAllTimeZones();
        loadConfiguration();
        getLogger().info("설정이 다시 로드되었습니다.");
    }
}
