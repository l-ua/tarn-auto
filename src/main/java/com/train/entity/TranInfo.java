package com.train.entity;

import com.train.enums.SeatEnum;
import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * @autor 10758
 * @system train12306
 * @Time 2018/12/1
 */
@Data
@ToString
public class TranInfo {

    private boolean isSelect;

    private Integer index;
    private String tranCode;

    private String fromCode;

    private String fromName;

    private String toName;
    private String toCode;
    private String costTime;

    private String shangWuZuoTeDengZuo;

    private String yiDengZuo;

    private String erDengZuo;

    private String gaoJiRuanWo;

    private String ruanWo;

    private String dongWo;

    private String yingWo;

    private String ruanZuo;

    private String yingZuo;

    private String wuZuo;

    private String tranInfoMsg;

    public boolean isHaveTicket(String seatName, TranInfo tranInfo) {
        SeatEnum seatEnum = SeatEnum.getEnumByName(seatName);

        try {
            Field field = TranInfo.class.getDeclaredField(seatEnum.getField());
            field.setAccessible(true);
            try {
                String yuPiao = (String) field.get(tranInfo);
                if ("有".equals(yuPiao)) {
                    return true;
                }

                if ("无".equals(yuPiao)) {
                    return false;
                }

                if ("--".equals(yuPiao)) {
                    return false;
                }

                if(Integer.valueOf(yuPiao)>=5){
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }
}
