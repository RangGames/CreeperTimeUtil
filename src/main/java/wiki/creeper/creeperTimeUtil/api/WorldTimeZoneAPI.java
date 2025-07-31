package wiki.creeper.creeperTimeUtil.api;

import org.bukkit.World;
import wiki.creeper.creeperTimeUtil.core.TimeKernel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 월드별 시간대를 관리하는 API
 * 각 월드는 서버 시간과 다른 오프셋을 가질 수 있습니다.
 */
public class WorldTimeZoneAPI {
    
    private static final Map<String, Long> worldTimeOffsets = new ConcurrentHashMap<>();
    private static final Map<String, Double> worldTimeSpeed = new ConcurrentHashMap<>();
    
    /**
     * 월드의 시간 오프셋을 설정합니다.
     * @param world 월드
     * @param offsetMinutes 서버 시간과의 차이(분)
     */
    public static void setWorldTimeOffset(World world, long offsetMinutes) {
        worldTimeOffsets.put(world.getName(), offsetMinutes);
    }
    
    /**
     * 월드의 시간 오프셋을 반환합니다.
     * @param world 월드
     * @return 오프셋(분)
     */
    public static long getWorldTimeOffset(World world) {
        return worldTimeOffsets.getOrDefault(world.getName(), 0L);
    }
    
    /**
     * 월드의 시간 속도를 설정합니다.
     * @param world 월드
     * @param speed 속도 배율 (1.0 = 기본)
     */
    public static void setWorldTimeSpeed(World world, double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("시간 속도는 0보다 커야 합니다");
        }
        worldTimeSpeed.put(world.getName(), speed);
    }
    
    /**
     * 월드의 시간 속도를 반환합니다.
     * @param world 월드
     * @return 속도 배율
     */
    public static double getWorldTimeSpeed(World world) {
        return worldTimeSpeed.getOrDefault(world.getName(), 1.0);
    }
    
    /**
     * 특정 월드의 현재 시간(분)을 반환합니다.
     * @param world 월드
     * @return 월드의 총 시간(분)
     */
    public static long getWorldTotalMinutes(World world) {
        long serverMinutes = ServerClockAPI.getTotalMinutes();
        long offset = getWorldTimeOffset(world);
        double speed = getWorldTimeSpeed(world);
        
        // 속도를 고려한 시간 계산
        return (long)(serverMinutes * speed) + offset;
    }
    
    /**
     * 특정 월드의 현재 일차를 반환합니다.
     * @param world 월드
     * @return 일차
     */
    public static int getWorldDay(World world) {
        return (int) (getWorldTotalMinutes(world) / 1440) + 1;
    }
    
    /**
     * 특정 월드의 현재 시간을 반환합니다.
     * @param world 월드
     * @return 시간 (0-23)
     */
    public static int getWorldHour(World world) {
        long minutesInDay = getWorldTotalMinutes(world) % 1440;
        return (int) (minutesInDay / 60);
    }
    
    /**
     * 특정 월드의 현재 분을 반환합니다.
     * @param world 월드
     * @return 분 (0-59)
     */
    public static int getWorldMinute(World world) {
        return (int) (getWorldTotalMinutes(world) % 60);
    }
    
    /**
     * 특정 월드의 포맷된 시간을 반환합니다.
     * @param world 월드
     * @return 포맷된 시간
     */
    public static String getWorldFormattedTime(World world) {
        return String.format("%d일차 %02d:%02d", 
            getWorldDay(world), 
            getWorldHour(world), 
            getWorldMinute(world));
    }
    
    /**
     * 월드를 특정 시간대로 설정합니다.
     * @param world 월드
     * @param timeZoneName 시간대 이름
     */
    public static void setWorldTimeZone(World world, TimeZone timeZoneName) {
        setWorldTimeOffset(world, timeZoneName.getOffsetMinutes());
    }
    
    /**
     * 미리 정의된 시간대
     */
    public enum TimeZone {
        // 서버 시간 기준 오프셋
        SERVER_TIME("서버 시간", 0),
        EARLY_MORNING("이른 아침", -360),    // -6시간
        MORNING("아침", -180),               // -3시간
        AFTERNOON("오후", 180),              // +3시간
        EVENING("저녁", 360),                // +6시간
        NIGHT("밤", 540),                    // +9시간
        MIDNIGHT("자정", 720),               // +12시간
        
        // 실제 시간대 스타일
        UTC_MINUS_12("UTC-12", -17280),     // -12일
        UTC_MINUS_6("UTC-6", -8640),        // -6일
        UTC("UTC", 0),
        UTC_PLUS_6("UTC+6", 8640),          // +6일
        UTC_PLUS_12("UTC+12", 17280),       // +12일
        
        // 판타지 시간대
        ETERNAL_DAY("영원한 낮", 0) {
            @Override
            public int getHourOverride() {
                return 12; // 항상 정오
            }
        },
        ETERNAL_NIGHT("영원한 밤", 0) {
            @Override
            public int getHourOverride() {
                return 0; // 항상 자정
            }
        },
        REVERSED_TIME("역행하는 시간", 0) {
            @Override
            public long calculateOffset(long serverMinutes) {
                return -serverMinutes * 2; // 시간이 거꾸로 흐름
            }
        };
        
        private final String koreanName;
        private final long offsetMinutes;
        
        TimeZone(String koreanName, long offsetMinutes) {
            this.koreanName = koreanName;
            this.offsetMinutes = offsetMinutes;
        }
        
        public String getKoreanName() {
            return koreanName;
        }
        
        public long getOffsetMinutes() {
            return offsetMinutes;
        }
        
        // 오버라이드 가능한 메소드들
        public int getHourOverride() {
            return -1; // -1이면 오버라이드 없음
        }
        
        public long calculateOffset(long serverMinutes) {
            return offsetMinutes;
        }
    }
    
    /**
     * 월드의 시각적 시간을 업데이트합니다.
     * @param world 월드
     */
    public static void updateWorldVisualTime(World world) {
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        
        long worldMinutes = getWorldTotalMinutes(world);
        long minutesInDay = worldMinutes % 1440;
        long ticks = (minutesInDay * 24000) / 1440;
        
        // 특수 시간대 처리
        String worldName = world.getName();
        if (worldTimeOffsets.containsKey(worldName)) {
            // 영원한 낮/밤 같은 특수 효과 체크
            for (TimeZone tz : TimeZone.values()) {
                if (tz.getHourOverride() >= 0) {
                    // 특정 시간으로 고정
                    int fixedHour = tz.getHourOverride();
                    ticks = (fixedHour * 1000) % 24000;
                    break;
                }
            }
        }
        
        world.setTime(ticks);
    }
    
    /**
     * 모든 월드의 시간대 설정을 초기화합니다.
     */
    public static void resetAllTimeZones() {
        worldTimeOffsets.clear();
        worldTimeSpeed.clear();
    }
}