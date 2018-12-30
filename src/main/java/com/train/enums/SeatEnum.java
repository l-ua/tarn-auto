package com.train.enums;

/**
 * @autor 10758
 * @system train12306
 * @Time 2018/12/1
 */
public enum SeatEnum {
    SHANG_WU_ZUO_TE_DENG_ZUO("shangWuZuoTeDengZuo", "商务座特等座", "9"),
    YI_DENG_ZUO("yiDengZuo", "一等座", "M"),
    ER_DENG_ZUO("erDengZuo", "二等座", "0"),
    GAO_JI_RUAN_WO("gaoJiRuanWo", "高级軟卧", "6"),
    RUAN_WO("ruanWo", "软卧", "4"),
    DONG_WO("dongWo", "动卧", ""),
    YING_WO("yingWo", "硬卧", "3"),
    RUAN_ZUO("ruanZuo", "软座", "2"),
    YING_ZUO("yingZuo", "硬座", "1"),
    WU_ZUO("wuZuo", "无座", "0");

    //对应字段
    private String field;

    // 席别名称
    private String name;

    // 席别code
    private String code;

    SeatEnum(String field, String name, String code) {
        this.field = field;
        this.code = code;
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static SeatEnum getEnumByName(String name) {
        for (SeatEnum seatEnum : SeatEnum.values()) {
            if (seatEnum.getName().equals(name)) {
                return seatEnum;
            }
        }

        return null;
    }
}
