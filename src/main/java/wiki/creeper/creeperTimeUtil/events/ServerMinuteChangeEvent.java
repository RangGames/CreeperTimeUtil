package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버 시간이 1분 지날 때마다 발생하는 이벤트
 */
public class ServerMinuteChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final long totalMinutes;
    
    public ServerMinuteChangeEvent(long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }
    
    /**
     * 서버 시작부터 현재까지의 총 시간(분)을 반환합니다.
     * @return 총 시간(분)
     */
    public long getTotalMinutes() {
        return totalMinutes;
    }
    
    /**
     * 현재 일차를 반환합니다.
     * @return 일차 (1부터 시작)
     */
    public int getDay() {
        return (int) (totalMinutes / 1440) + 1;
    }
    
    /**
     * 현재 시간을 반환합니다.
     * @return 시간 (0-23)
     */
    public int getHour() {
        long minutesInDay = totalMinutes % 1440;
        return (int) (minutesInDay / 60);
    }
    
    /**
     * 현재 분을 반환합니다.
     * @return 분 (0-59)
     */
    public int getMinute() {
        return (int) (totalMinutes % 60);
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}