package com.ulalalab.snipe.infra.constant;

import java.util.Arrays;

public enum DeviceCodeEnum {

    WICON_4CH("WICON_4CH", "SENSOR", 0x0100),
    WICON_L("WICON_L", "SENSOR", 0x0101),
    UC11_TH("UC11_TH", "SENSOR", 0x0300),
    EM300_TH("EM300_TH", "LORA", 0x0301),
    EM300_LD("EM300_LD", "LORA", 0x0302),
    EM500_CO2("EM500_CO2", "LORA", 0x0303),
    EM500_SMTC("EM500_SMTC", "LORA", 0x0304),
    EM500_LGT("EM500_LGT", "LORA", 0x0305),
    UC501("UC501", "LORA", 0x0306),
    WICON_2CH("WICON_2CH", "SERIAL", 0x0500),
    WICON_R("WICON_R", "SERIAL", 0x0501),
    WICON_MX("WICON_MX", "SERIAL", 0x0502),
    WICON_L_SERIAL("WICON_L_SERIAL", "SERIAL", 0x0502);

    private String device;
    private String deviceType;
    private int code;

    DeviceCodeEnum(String device, String deviceType, int code) {
        this.device = device;
        this.deviceType = deviceType;
        this.code = code;
    }

    public String getDevice() {
        return this.device;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public int getCode() {
        return this.code;
    }

    public static DeviceCodeEnum codeToDevice(final short code) {
        DeviceCodeEnum deviceCode = Arrays.stream(DeviceCodeEnum.values())
                .filter(d -> d.getCode()==code).findFirst().orElse(null);
        return deviceCode;
    }
}
