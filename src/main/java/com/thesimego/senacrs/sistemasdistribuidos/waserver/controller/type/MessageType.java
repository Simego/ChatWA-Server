package com.thesimego.senacrs.sistemasdistribuidos.waserver.controller.type;

/**
 *
 * @author drafaelli
 */
public enum MessageType {
    
    USER("U"),
    GROUP("G");
    
    private final String value;

    private MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    public static MessageType parse(String value) {
        for(MessageType type : MessageType.values()) {
            if(type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
    
}
