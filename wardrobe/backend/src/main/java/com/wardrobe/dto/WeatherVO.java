package com.wardrobe.dto;

import lombok.Data;

@Data
public class WeatherVO {

    private String city;

    /** 温度 ℃ */
    private Double temp;

    /** 体感温度 ℃ */
    private Double feelsLike;

    /** 天气描述，如 晴/多云/小雨 */
    private String condition;

    /** 天气图标代码 */
    private String icon;

    /** 穿衣建议 */
    private String suggestion;

    /** 数据来源：api / mock */
    private String source;
}
