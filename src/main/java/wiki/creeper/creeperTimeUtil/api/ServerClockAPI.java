package wiki.creeper.creeperTimeUtil.api;

import wiki.creeper.creeperTimeUtil.core.TimeKernel;

/**
 * 서버의 중앙 시간을 관리하는 API
 * 이 클래스의 모든 메소드는 정적(static)이며, 다른 플러그인에서 직접 호출하여 사용할 수 있습니다.
 */
public class ServerClockAPI {
    
    /**
     * 현재 일차를 반환합니다.
     * @return 일차 (1부터 시작)
     */
    public static int getDay() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getDay();
    }
    
    /**
     * 현재 시간을 반환합니다.
     * @return 시간 (0-23)
     */
    public static int getHour() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getHour();
    }
    
    /**
     * 현재 분을 반환합니다.
     * @return 분 (0-59)
     */
    public static int getMinute() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getMinute();
    }
    
    /**
     * 포맷된 시간 문자열을 반환합니다.
     * @return 형식: "3일차 15:30"
     */
    public static String getFormattedTime() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getFormattedTime();
    }
    
    /**
     * 현재 주차를 반환합니다.
     * @return 주차 (1부터 시작)
     */
    public static int getWeek() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getWeek();
    }
    
    /**
     * 현재 월을 반환합니다.
     * @return 월 (1부터 시작)
     */
    public static int getMonth() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getMonth();
    }
    
    /**
     * 현재 년도를 반환합니다.
     * @return 년도 (1부터 시작)
     */
    public static int getYear() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getYear();
    }
    
    /**
     * 서버 시작부터 현재까지 누적된 총 시간(분)을 반환합니다.
     * 모든 쿨타임 및 기간 계산의 절대적인 기준점이 됩니다.
     * @return 서버의 누적 시간(분)
     */
    public static long getTotalMinutes() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getTotalMinutes();
    }
    
    // ===== 실제 시간 기반 쿨타임 (권장) =====
    
    /**
     * 실제 시간 기반으로 쿨타임을 설정합니다.
     * @param uniqueId 고유 ID
     * @param durationInSeconds 쿨타임 지속 시간(초)
     */
    public static void setCooldown(String uniqueId, long durationInSeconds) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.setCooldown(uniqueId, durationInSeconds);
    }
    
    /**
     * 지정된 ID의 쿨타임이 종료되었는지 확인합니다.
     * @param uniqueId 고유 ID
     * @return 쿨타임이 끝났으면 true
     */
    public static boolean isCooldownOver(String uniqueId) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.isCooldownOver(uniqueId);
    }
    
    /**
     * 지정된 ID의 남은 쿨타임(초)을 반환합니다.
     * @param uniqueId 고유 ID
     * @return 남은 시간(초). 쿨타임이 끝났으면 0
     */
    public static long getRemainingCooldownSeconds(String uniqueId) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getRemainingCooldownSeconds(uniqueId);
    }
    
    /**
     * 쿨타임을 제거합니다.
     * @param uniqueId 고유 ID
     */
    public static void removeCooldown(String uniqueId) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.removeCooldown(uniqueId);
    }
    
    // ===== 게임 시간 기반 쿨타임 (레거시) =====
    
    /**
     * 게임 시간 기반으로 쿨타임을 설정합니다.
     * @param uniqueId 고유 ID
     * @deprecated 실제 시간 기반 setCooldown(String, long) 사용을 권장합니다
     */
    @Deprecated
    public static void setGameTimeCooldown(String uniqueId) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.setGameTimeCooldown(uniqueId);
    }
    
    /**
     * 게임 시간 기반 쿨타임이 종료되었는지 확인합니다.
     * @param uniqueId 고유 ID
     * @param durationInMinutes 쿨타임 총 지속 시간(게임 시간 분)
     * @return 쿨타임이 끝났으면 true
     * @deprecated 실제 시간 기반 isCooldownOver(String) 사용을 권장합니다
     */
    @Deprecated
    public static boolean isGameTimeCooldownOver(String uniqueId, long durationInMinutes) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.isGameTimeCooldownOver(uniqueId, durationInMinutes);
    }
    
    /**
     * 게임 시간 기반 남은 쿨타임(분)을 반환합니다.
     * @param uniqueId 고유 ID
     * @param totalDurationInMinutes 쿨타임 총 지속 시간(게임 시간 분)
     * @return 남은 시간(게임 시간 분). 쿨타임이 끝났으면 0
     * @deprecated 실제 시간 기반 getRemainingCooldownSeconds(String) 사용을 권장합니다
     */
    @Deprecated
    public static long getRemainingGameTimeCooldownMinutes(String uniqueId, long totalDurationInMinutes) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getRemainingGameTimeCooldownMinutes(uniqueId, totalDurationInMinutes);
    }
    
    /**
     * 시간의 흐름 속도를 변경합니다.
     * @param speed 속도 배율 (1.0 = 기본, 2.0 = 2배속, 0.5 = 0.5배속)
     */
    public static void setTimeSpeed(double speed) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.setTimeSpeed(speed);
    }
    
    /**
     * 현재 시간 속도를 반환합니다.
     * @return 시간 속도 배율
     */
    public static double getTimeSpeed() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.getTimeSpeed();
    }
    
    /**
     * 시간을 일시정지합니다.
     */
    public static void pauseTime() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.pauseTime();
    }
    
    /**
     * 일시정지된 시간을 재개합니다.
     */
    public static void resumeTime() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.resumeTime();
    }
    
    /**
     * 시간이 일시정지 상태인지 확인합니다.
     * @return 일시정지 상태면 true
     */
    public static boolean isTimePaused() {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        return kernel.isTimePaused();
    }
    
    /**
     * 서버 시간을 특정 시점으로 설정합니다.
     * @param day 일차 (1부터 시작)
     * @param hour 시간 (0-23)
     * @param minute 분 (0-59)
     */
    public static void setTime(int day, int hour, int minute) {
        TimeKernel kernel = TimeKernel.getInstance();
        if (kernel == null) {
            throw new IllegalStateException("TimeKernel이 초기화되지 않았습니다");
        }
        kernel.setTime(day, hour, minute);
    }
}