package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버의 주차가 바뀔 때마다 발생하는 이벤트
 * 매주 월요일 0시에 발생합니다.
 */
public class ServerWeekChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int newWeek;
    private final int firstDayOfWeek;
    
    public ServerWeekChangeEvent(int newWeek, int firstDayOfWeek) {
        this.newWeek = newWeek;
        this.firstDayOfWeek = firstDayOfWeek;
    }
    
    /**
     * 새로운 주차를 반환합니다.
     * @return 주차 (1부터 시작)
     */
    public int getNewWeek() {
        return newWeek;
    }
    
    /**
     * 이전 주차를 반환합니다.
     * @return 이전 주차
     */
    public int getPreviousWeek() {
        return Math.max(1, newWeek - 1);
    }
    
    /**
     * 이번 주의 첫날(월요일)이 몇 일차인지 반환합니다.
     * @return 이번 주 월요일의 일차
     */
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }
    
    /**
     * 이번 주의 마지막날(일요일)이 몇 일차인지 반환합니다.
     * @return 이번 주 일요일의 일차
     */
    public int getLastDayOfWeek() {
        return firstDayOfWeek + 6;
    }
    
    /**
     * 서버가 시작된 지 몇 주가 지났는지 반환합니다.
     * @return 경과 주수 (0부터 시작)
     */
    public int getWeeksSinceStart() {
        return newWeek - 1;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}