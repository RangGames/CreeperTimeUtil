package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 특정 시간대에 도달했을 때 발생하는 이벤트
 * 새벽, 아침, 정오, 저녁, 밤 등의 시간대 변경 시 발생합니다.
 */
public class ServerTimeOfDayEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    
    public enum TimeOfDay {
        DAWN(5, "새벽", "해가 뜨기 시작합니다"),          // 05:00
        MORNING(6, "아침", "아침이 밝았습니다"),          // 06:00
        NOON(12, "정오", "태양이 가장 높이 떠있습니다"),  // 12:00
        DUSK(18, "황혼", "해가 지기 시작합니다"),         // 18:00
        NIGHT(22, "밤", "깊은 밤이 찾아왔습니다"),        // 22:00
        MIDNIGHT(0, "자정", "하루가 끝나고 새로운 날이 시작됩니다"); // 00:00
        
        private final int hour;
        private final String koreanName;
        private final String description;
        
        TimeOfDay(int hour, String koreanName, String description) {
            this.hour = hour;
            this.koreanName = koreanName;
            this.description = description;
        }
        
        public int getHour() {
            return hour;
        }
        
        public String getKoreanName() {
            return koreanName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final TimeOfDay timeOfDay;
    private final int day;
    
    public ServerTimeOfDayEvent(TimeOfDay timeOfDay, int day) {
        this.timeOfDay = timeOfDay;
        this.day = day;
    }
    
    /**
     * 현재 시간대를 반환합니다.
     * @return 시간대
     */
    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }
    
    /**
     * 현재 일차를 반환합니다.
     * @return 일차
     */
    public int getDay() {
        return day;
    }
    
    /**
     * 시간대의 시작 시간을 반환합니다.
     * @return 시간 (0-23)
     */
    public int getHour() {
        return timeOfDay.getHour();
    }
    
    /**
     * 시간대의 한글 이름을 반환합니다.
     * @return 한글 이름
     */
    public String getKoreanName() {
        return timeOfDay.getKoreanName();
    }
    
    /**
     * 시간대의 설명을 반환합니다.
     * @return 설명
     */
    public String getDescription() {
        return timeOfDay.getDescription();
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}