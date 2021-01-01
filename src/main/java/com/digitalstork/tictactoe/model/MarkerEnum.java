package com.digitalstork.tictactoe.model;

public enum MarkerEnum {

    BLANK(" "),
    X("X"),
    O("O");

    private String value;

    MarkerEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
