package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버의 월이 바뀔 때마다 발생하는 이벤트
 * 30일마다 발생합니다.
 */
public class ServerMonthChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final int newMonth;
    private final int year;
    
    public ServerMonthChangeEvent(int newMonth, int year) {
        this.newMonth = newMonth;
        this.year = year;
    }
    
    /**
     * 새로운 월을 반환합니다.
     * @return 월 (1부터 시작)
     */
    public int getNewMonth() {
        return newMonth;
    }
    
    /**
     * 이전 월을 반환합니다.
     * @return 이전 월
     */
    public int getPreviousMonth() {
        if (newMonth == 1) {
            return 12;
        }
        return newMonth - 1;
    }
    
    /**
     * 현재 년도를 반환합니다.
     * @return 년도 (1부터 시작)
     */
    public int getYear() {
        return year;
    }
    
    /**
     * 이번 달의 첫날이 몇 일차인지 반환합니다.
     * @return 이번 달 1일의 일차
     */
    public int getFirstDayOfMonth() {
        return ((year - 1) * 360) + ((newMonth - 1) * 30) + 1;
    }
    
    /**
     * 이번 달의 마지막날이 몇 일차인지 반환합니다.
     * @return 이번 달 마지막날의 일차
     */
    public int getLastDayOfMonth() {
        return getFirstDayOfMonth() + 29;
    }
    
    /**
     * 서버가 시작된 지 몇 개월이 지났는지 반환합니다.
     * @return 경과 개월수 (0부터 시작)
     */
    public int getMonthsSinceStart() {
        return ((year - 1) * 12) + (newMonth - 1);
    }
    
    /**
     * 현재 분기를 반환합니다.
     * @return 분기 (1-4)
     */
    public int getQuarter() {
        return ((newMonth - 1) / 3) + 1;
    }
    
    /**
     * 계절을 반환합니다.
     * @return 계절 (봄, 여름, 가을, 겨울)
     */
    public String getSeason() {
        switch ((newMonth - 1) / 3) {
            case 0: return "봄";
            case 1: return "여름";
            case 2: return "가을";
            case 3: return "겨울";
            default: return "알 수 없음";
        }
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}