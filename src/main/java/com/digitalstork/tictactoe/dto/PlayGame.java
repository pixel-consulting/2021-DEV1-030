package com.digitalstork.tictactoe.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
public class PlayGame {

    private String id;
    private String player;
    private int row;
    private int col;
}