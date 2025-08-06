package wiki.creeper.creeperTimeUtil.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 서버 시간이 수동으로 변경될 때 발생하는 이벤트
 * setTime() API 호출 시 발생합니다.
 */
public class ServerTimeChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final long oldTotalMinutes;
    private final long newTotalMinutes;
    private final int newDay;
    private final int newHour;
    private final int newMinute;
    
    public ServerTimeChangeEvent(long oldTotalMinutes, long newTotalMinutes, int newDay, int newHour, int newMinute) {
        this.oldTotalMinutes = oldTotalMinutes;
        this.newTotalMinutes = newTotalMinutes;
        this.newDay = newDay;
        this.newHour = newHour;
        this.newMinute = newMinute;
    }
    
    /**
     * 변경 전 총 시간(분)을 반환합니다.
     * @return 변경 전 총 시간(분)
     */
    public long getOldTotalMinutes() {
        return oldTotalMinutes;
    }
    
    /**
     * 변경 후 총 시간(분)을 반환합니다.
     * @return 변경 후 총 시간(분)
     */
    public long getNewTotalMinutes() {
        return newTotalMinutes;
    }
    
    /**
     * 새로 설정된 일차를 반환합니다.
     * @return 일차 (1부터 시작)
     */
    public int getNewDay() {
        return newDay;
    }
    
    /**
     * 새로 설정된 시간을 반환합니다.
     * @return 시간 (0-23)
     */
    public int getNewHour() {
        return newHour;
    }
    
    /**
     * 새로 설정된 분을 반환합니다.
     * @return 분 (0-59)
     */
    public int getNewMinute() {
        return newMinute;
    }
    
    /**
     * 시간이 얼마나 변경되었는지 분 단위로 반환합니다.
     * @return 변경된 시간(분), 양수면 미래로, 음수면 과거로
     */
    public long getTimeDifference() {
        return newTotalMinutes - oldTotalMinutes;
    }
    
    /**
     * 포맷된 새 시간 문자열을 반환합니다.
     * @return 형식: "3일차 15:30"
     */
    public String getFormattedNewTime() {
        return String.format("%d일차 %02d:%02d", newDay, newHour, newMinute);
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}