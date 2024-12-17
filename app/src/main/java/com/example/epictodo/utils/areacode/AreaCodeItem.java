package com.example.epictodo.utils.areacode;

/**
 * AreaCodeItem
 *
 * @author 31112
 * @date 2024/12/4
 */
public class AreaCodeItem {
    private String countryCode;
    private String areaCode;

    public AreaCodeItem(String countryCode, String areaCode) {
        this.countryCode = countryCode;
        this.areaCode = areaCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }
}
