package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버 시간이 정시가 될 때마다 발생하는 이벤트
 */
public class ServerHourChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int hour;
    private final int day;
    
    public ServerHourChangeEvent(int hour, int day) {
        this.hour = hour;
        this.day = day;
    }
    
    /**
     * 현재 시간을 반환합니다.
     * @return 시간 (0-23)
     */
    public int getHour() {
        return hour;
    }
    
    /**
     * 현재 일차를 반환합니다.
     * @return 일차 (1부터 시작)
     */
    public int getDay() {
        return day;
    }
    
    /**
     * 시간이 새벽(0-5시)인지 확인합니다.
     * @return 새벽이면 true
     */
    public boolean isDawn() {
        return hour >= 0 && hour < 6;
    }
    
    /**
     * 시간이 아침(6-11시)인지 확인합니다.
     * @return 아침이면 true
     */
    public boolean isMorning() {
        return hour >= 6 && hour < 12;
    }
    
    /**
     * 시간이 오후(12-17시)인지 확인합니다.
     * @return 오후면 true
     */
    public boolean isAfternoon() {
        return hour >= 12 && hour < 18;
    }
    
    /**
     * 시간이 저녁(18-23시)인지 확인합니다.
     * @return 저녁이면 true
     */
    public boolean isEvening() {
        return hour >= 18;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}