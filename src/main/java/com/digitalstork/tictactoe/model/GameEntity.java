package com.digitalstork.tictactoe.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;
    private MarkerEnum row0column0 = MarkerEnum.BLANK;
    private MarkerEnum row0column1 = MarkerEnum.BLANK;
    private MarkerEnum row0column2 = MarkerEnum.BLANK;
    private MarkerEnum row1column0 = MarkerEnum.BLANK;
    private MarkerEnum row1column1 = MarkerEnum.BLANK;
    private MarkerEnum row1column2 = MarkerEnum.BLANK;
    private MarkerEnum row2column0 = MarkerEnum.BLANK;
    private MarkerEnum row2column1 = MarkerEnum.BLANK;
    private MarkerEnum row2column2 = MarkerEnum.BLANK;
    private MarkerEnum nextPlayer = MarkerEnum.BLANK;
    private boolean endGame;

    public String drawBoard() {
        return new StringBuilder()
                .append(System.getProperty("line.separator"))
                .append("+---+---+---+")
                .append(System.getProperty("line.separator"))
                .append(String.format("| %s | %s | %s |", row0column0.getValue(), row0column1.getValue(), row0column2.getValue()))
                .append(System.getProperty("line.separator"))
                .append("+---+---+---+")
                .append(System.getProperty("line.separator"))
                .append(String.format("| %s | %s | %s |", row1column0.getValue(), row1column1.getValue(), row1column2.getValue()))
                .append(System.getProperty("line.separator"))
                .append("+---+---+---+")
                .append(System.getProperty("line.separator"))
                .append(String.format("| %s | %s | %s |", row2column0.getValue(), row2column1.getValue(), row2column2.getValue()))
                .append(System.getProperty("line.separator"))
                .append("+---+---+---+")
                .toString();

    }
}
