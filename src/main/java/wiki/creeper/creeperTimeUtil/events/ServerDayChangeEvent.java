package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버의 날짜가 바뀔 때마다 발생하는 이벤트
 * 일일 초기화 로직 등에 활용할 수 있습니다.
 */
public class ServerDayChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int newDay;
    
    public ServerDayChangeEvent(int newDay) {
        this.newDay = newDay;
    }
    
    /**
     * 새로운 일차를 반환합니다.
     * @return 일차 (1부터 시작)
     */
    public int getNewDay() {
        return newDay;
    }
    
    /**
     * 이전 일차를 반환합니다.
     * @return 이전 일차
     */
    public int getPreviousDay() {
        return Math.max(1, newDay - 1);
    }
    
    /**
     * 서버가 시작된 지 며칠이 지났는지 반환합니다.
     * @return 경과 일수 (0부터 시작)
     */
    public int getDaysSinceStart() {
        return newDay - 1;
    }
    
    /**
     * 현재 주차를 반환합니다.
     * @return 주차 (1부터 시작)
     */
    public int getWeek() {
        return ((newDay - 1) / 7) + 1;
    }
    
    /**
     * 현재 요일을 반환합니다.
     * @return 요일 (1=월요일, 7=일요일)
     */
    public int getDayOfWeek() {
        int dayInWeek = ((newDay - 1) % 7) + 1;
        return dayInWeek;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}