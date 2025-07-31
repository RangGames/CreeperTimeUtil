package wiki.creeper.creeperTimeUtil.api;

import org.bukkit.entity.Player;
import wiki.creeper.creeperTimeUtil.core.TimeKernel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플레이어별 시간 관련 데이터를 관리하는 API
 */
public class PlayerTimeAPI {
    
    private static final Map<UUID, Long> playerJoinTime = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> playerTotalPlayTime = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> playerJoinGameMinutes = new ConcurrentHashMap<>();
    
    /**
     * 플레이어가 서버에 접속했을 때 호출
     * @param player 플레이어
     */
    public static void onPlayerJoin(Player player) {
        UUID uuid = player.getUniqueId();
        playerJoinTime.put(uuid, System.currentTimeMillis());
        playerJoinGameMinutes.put(uuid, ServerClockAPI.getTotalMinutes());
        
        // 기존 플레이 시간이 없으면 0으로 초기화
        playerTotalPlayTime.putIfAbsent(uuid, 0L);
    }
    
    /**
     * 플레이어가 서버에서 나갈 때 호출
     * @param player 플레이어
     */
    public static void onPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();
        Long joinTime = playerJoinTime.remove(uuid);
        playerJoinGameMinutes.remove(uuid);
        
        if (joinTime != null) {
            long sessionTime = System.currentTimeMillis() - joinTime;
            playerTotalPlayTime.merge(uuid, sessionTime, Long::sum);
        }
    }
    
    /**
     * 플레이어의 현재 세션 플레이 시간을 반환합니다.
     * @param player 플레이어
     * @return 현재 세션 플레이 시간(초)
     */
    public static long getSessionPlayTime(Player player) {
        Long joinTime = playerJoinTime.get(player.getUniqueId());
        if (joinTime == null) {
            return 0;
        }
        return (System.currentTimeMillis() - joinTime) / 1000L;
    }
    
    /**
     * 플레이어의 총 플레이 시간을 반환합니다.
     * @param player 플레이어
     * @return 총 플레이 시간(초)
     */
    public static long getTotalPlayTime(Player player) {
        UUID uuid = player.getUniqueId();
        long totalTime = playerTotalPlayTime.getOrDefault(uuid, 0L) / 1000L;
        
        // 현재 세션 시간 추가
        Long joinTime = playerJoinTime.get(uuid);
        if (joinTime != null) {
            totalTime += (System.currentTimeMillis() - joinTime) / 1000L;
        }
        
        return totalTime;
    }
    
    /**
     * 플레이어가 경험한 게임 내 일수를 반환합니다.
     * @param player 플레이어
     * @return 플레이어가 경험한 일수
     */
    public static int getPlayerDay(Player player) {
        Long joinGameMinutes = playerJoinGameMinutes.get(player.getUniqueId());
        if (joinGameMinutes == null) {
            return 0;
        }
        
        long currentMinutes = ServerClockAPI.getTotalMinutes();
        long playedMinutes = currentMinutes - joinGameMinutes;
        return (int) (playedMinutes / 1440) + 1;
    }
    
    /**
     * 플레이어의 현재 게임 시간(시)을 반환합니다.
     * @param player 플레이어
     * @return 시간 (0-23)
     */
    public static int getPlayerHour(Player player) {
        Long joinGameMinutes = playerJoinGameMinutes.get(player.getUniqueId());
        if (joinGameMinutes == null) {
            return ServerClockAPI.getHour();
        }
        
        long currentMinutes = ServerClockAPI.getTotalMinutes();
        long playedMinutes = currentMinutes - joinGameMinutes;
        return (int) ((playedMinutes % 1440) / 60);
    }
    
    /**
     * 플레이어의 현재 게임 시간(분)을 반환합니다.
     * @param player 플레이어
     * @return 분 (0-59)
     */
    public static int getPlayerMinute(Player player) {
        Long joinGameMinutes = playerJoinGameMinutes.get(player.getUniqueId());
        if (joinGameMinutes == null) {
            return ServerClockAPI.getMinute();
        }
        
        long currentMinutes = ServerClockAPI.getTotalMinutes();
        long playedMinutes = currentMinutes - joinGameMinutes;
        return (int) (playedMinutes % 60);
    }
    
    /**
     * 플레이어의 게임 시간을 포맷된 문자열로 반환합니다.
     * @param player 플레이어
     * @return 포맷된 시간 (예: "3일차 15:30")
     */
    public static String getPlayerFormattedGameTime(Player player) {
        return String.format("%d일차 %02d:%02d", 
            getPlayerDay(player), 
            getPlayerHour(player), 
            getPlayerMinute(player));
    }
    
    /**
     * 플레이어가 서버에 접속한 실제 게임 시간을 반환합니다.
     * @param player 플레이어
     * @return 접속 시점의 서버 게임 시간
     */
    public static String getPlayerJoinServerTime(Player player) {
        Long joinGameMinutes = playerJoinGameMinutes.get(player.getUniqueId());
        if (joinGameMinutes == null) {
            return "알 수 없음";
        }
        
        int joinDay = (int) (joinGameMinutes / 1440) + 1;
        int joinHour = (int) ((joinGameMinutes % 1440) / 60);
        int joinMinute = (int) (joinGameMinutes % 60);
        
        return String.format("%d일차 %02d:%02d", joinDay, joinHour, joinMinute);
    }
    
    /**
     * 플레이어의 플레이 시간을 포맷된 문자열로 반환합니다.
     * @param player 플레이어
     * @return 포맷된 플레이 시간 (예: "2일 5시간 30분")
     */
    public static String getFormattedPlayTime(Player player) {
        long totalSeconds = getTotalPlayTime(player);
        
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append("일 ");
        if (hours > 0) result.append(hours).append("시간 ");
        if (minutes > 0) result.append(minutes).append("분 ");
        if (seconds > 0 || result.length() == 0) result.append(seconds).append("초");
        
        return result.toString().trim();
    }
    
    /**
     * 플레이어별 쿨타임을 설정합니다.
     * @param player 플레이어
     * @param cooldownType 쿨타임 타입
     * @param durationInSeconds 지속 시간(초)
     */
    public static void setPlayerCooldown(Player player, String cooldownType, long durationInSeconds) {
        String uniqueId = cooldownType + "_" + player.getUniqueId();
        ServerClockAPI.setCooldown(uniqueId, durationInSeconds);
    }
    
    /**
     * 플레이어의 쿨타임이 끝났는지 확인합니다.
     * @param player 플레이어
     * @param cooldownType 쿨타임 타입
     * @return 쿨타임이 끝났으면 true
     */
    public static boolean isPlayerCooldownOver(Player player, String cooldownType) {
        String uniqueId = cooldownType + "_" + player.getUniqueId();
        return ServerClockAPI.isCooldownOver(uniqueId);
    }
    
    /**
     * 플레이어의 남은 쿨타임을 반환합니다.
     * @param player 플레이어
     * @param cooldownType 쿨타임 타입
     * @return 남은 시간(초)
     */
    public static long getPlayerRemainingCooldown(Player player, String cooldownType) {
        String uniqueId = cooldownType + "_" + player.getUniqueId();
        return ServerClockAPI.getRemainingCooldownSeconds(uniqueId);
    }
    
    /**
     * 모든 플레이어 시간 데이터를 저장합니다.
     * 서버 종료 시 호출되어야 합니다.
     */
    public static void saveAllPlayerData() {
        // 현재 온라인인 모든 플레이어의 세션 시간을 총 플레이 시간에 추가
        for (Map.Entry<UUID, Long> entry : playerJoinTime.entrySet()) {
            UUID uuid = entry.getKey();
            long joinTime = entry.getValue();
            long sessionTime = System.currentTimeMillis() - joinTime;
            playerTotalPlayTime.merge(uuid, sessionTime, Long::sum);
        }
        playerJoinTime.clear();
    }
    
    /**
     * 플레이어의 총 플레이 시간 데이터를 가져옵니다.
     * @return 플레이어별 총 플레이 시간 맵
     */
    public static Map<UUID, Long> getAllPlayerPlayTime() {
        return new ConcurrentHashMap<>(playerTotalPlayTime);
    }
    
    /**
     * 플레이어의 총 플레이 시간 데이터를 설정합니다.
     * @param playTimeData 플레이어별 총 플레이 시간 맵
     */
    public static void setAllPlayerPlayTime(Map<UUID, Long> playTimeData) {
        playerTotalPlayTime.clear();
        playerTotalPlayTime.putAll(playTimeData);
    }
}