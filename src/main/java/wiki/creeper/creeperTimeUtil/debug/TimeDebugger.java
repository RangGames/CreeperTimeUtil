package wiki.creeper.creeperTimeUtil.debug;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import wiki.creeper.creeperTimeUtil.api.PlayerTimeAPI;
import wiki.creeper.creeperTimeUtil.api.ServerClockAPI;
import wiki.creeper.creeperTimeUtil.api.WorldTimeZoneAPI;
import wiki.creeper.creeperTimeUtil.core.TimeKernel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 시간 시스템의 디버그 정보를 제공하는 클래스
 */
public class TimeDebugger {
    
    private static boolean debugMode = false;
    private static final Logger logger = Bukkit.getLogger();
    private static final List<String> debugLog = new ArrayList<>();
    private static final int MAX_LOG_SIZE = 1000;
    
    /**
     * 디버그 모드를 활성화/비활성화합니다.
     * @param enabled 활성화 여부
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        if (enabled) {
            log("디버그 모드가 활성화되었습니다.");
        } else {
            log("디버그 모드가 비활성화되었습니다.");
        }
    }
    
    /**
     * 디버그 모드 상태를 반환합니다.
     * @return 디버그 모드 활성화 여부
     */
    public static boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * 디버그 로그를 기록합니다.
     * @param message 로그 메시지
     */
    public static void log(String message) {
        String logMessage = String.format("[%s] %s", 
            ServerClockAPI.getFormattedTime(), 
            message);
        
        debugLog.add(logMessage);
        if (debugLog.size() > MAX_LOG_SIZE) {
            debugLog.remove(0);
        }
        
        if (debugMode) {
            logger.log(Level.INFO, "[CreeperTimeUtil Debug] " + logMessage);
        }
    }
    
    /**
     * 디버그 로그를 기록합니다.
     * @param message 로그 메시지
     * @param args 포맷 인자
     */
    public static void log(String message, Object... args) {
        log(String.format(message, args));
    }
    
    /**
     * 에러 로그를 기록합니다.
     * @param message 에러 메시지
     * @param throwable 예외
     */
    public static void logError(String message, Throwable throwable) {
        String errorMessage = String.format("[ERROR] %s: %s", message, throwable.getMessage());
        log(errorMessage);
        
        if (debugMode) {
            logger.log(Level.SEVERE, "[CreeperTimeUtil Error] " + message, throwable);
        }
    }
    
    /**
     * 현재 시스템 상태를 덤프합니다.
     * @return 시스템 상태 정보
     */
    public static List<String> dumpSystemStatus() {
        List<String> status = new ArrayList<>();
        
        status.add("=== CreeperTimeUtil 시스템 상태 ===");
        status.add("");
        
        // 서버 시간 정보
        status.add("[ 서버 시간 정보 ]");
        status.add("현재 시간: " + ServerClockAPI.getFormattedTime());
        status.add("총 시간(분): " + ServerClockAPI.getTotalMinutes());
        status.add("일차: " + ServerClockAPI.getDay());
        status.add("시간: " + ServerClockAPI.getHour());
        status.add("분: " + ServerClockAPI.getMinute());
        status.add("주차: " + ServerClockAPI.getWeek());
        status.add("월: " + ServerClockAPI.getMonth());
        status.add("년도: " + ServerClockAPI.getYear());
        status.add("");
        
        // 시간 제어 정보
        status.add("[ 시간 제어 정보 ]");
        status.add("시간 속도: " + ServerClockAPI.getTimeSpeed() + "x");
        status.add("일시정지: " + (ServerClockAPI.isTimePaused() ? "예" : "아니오"));
        status.add("");
        
        // 월드별 시간대 정보
        status.add("[ 월드별 시간대 ]");
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                long offset = WorldTimeZoneAPI.getWorldTimeOffset(world);
                double speed = WorldTimeZoneAPI.getWorldTimeSpeed(world);
                String worldTime = WorldTimeZoneAPI.getWorldFormattedTime(world);
                
                status.add(String.format("- %s: %s (오프셋: %d분, 속도: %.2fx)", 
                    world.getName(), worldTime, offset, speed));
            }
        }
        status.add("");
        
        // 플레이어 정보
        status.add("[ 온라인 플레이어 시간 정보 ]");
        Bukkit.getOnlinePlayers().forEach(player -> {
            String gameTime = PlayerTimeAPI.getPlayerFormattedGameTime(player);
            String playTime = PlayerTimeAPI.getFormattedPlayTime(player);
            status.add(String.format("- %s: 게임시간 %s, 플레이시간 %s", 
                player.getName(), gameTime, playTime));
        });
        status.add("");
        
        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        status.add("[ 시스템 리소스 ]");
        status.add(String.format("메모리 사용량: %dMB / %dMB", usedMemory, maxMemory));
        status.add("");
        
        return status;
    }
    
    /**
     * 디버그 정보를 CommandSender에게 전송합니다.
     * @param sender 명령어 전송자
     */
    public static void sendDebugInfo(CommandSender sender) {
        List<String> status = dumpSystemStatus();
        status.forEach(sender::sendMessage);
    }
    
    /**
     * 최근 디버그 로그를 반환합니다.
     * @param lines 반환할 줄 수
     * @return 디버그 로그
     */
    public static List<String> getRecentLogs(int lines) {
        int start = Math.max(0, debugLog.size() - lines);
        return new ArrayList<>(debugLog.subList(start, debugLog.size()));
    }
    
    /**
     * 디버그 로그를 초기화합니다.
     */
    public static void clearLogs() {
        debugLog.clear();
        log("디버그 로그가 초기화되었습니다.");
    }
    
    /**
     * 시간 변경 이벤트를 로깅합니다.
     * @param eventType 이벤트 타입
     * @param details 상세 정보
     */
    public static void logTimeEvent(String eventType, String details) {
        if (debugMode) {
            log("[이벤트] %s: %s", eventType, details);
        }
    }
    
    /**
     * 성능 메트릭을 로깅합니다.
     * @param operation 작업명
     * @param startTime 시작 시간
     */
    public static void logPerformance(String operation, long startTime) {
        if (debugMode) {
            long elapsed = System.currentTimeMillis() - startTime;
            log("[성능] %s 소요시간: %dms", operation, elapsed);
        }
    }
}