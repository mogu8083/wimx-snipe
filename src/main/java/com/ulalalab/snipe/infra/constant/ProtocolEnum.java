package com.ulalalab.snipe.infra.constant;

public enum ProtocolEnum {
    TCP("TCP"), HTTP("HTTP"), MQTT("MQTT");

    private String protocol;

    ProtocolEnum(String protocol) {
        this.protocol = protocol;
    }
}
