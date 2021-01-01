package com.digitalstork.tictactoe.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Game {
    private UUID id;
    private String nextPlayer;
    private boolean endGame;
}