package com.digitalstork.tictactoe.util;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.model.GameEntity;
import com.digitalstork.tictactoe.model.MarkerEnum;

import java.util.function.Function;

public class GameEntityTransformer implements Function<GameEntity, Game> {

    @Override
    public Game apply(GameEntity game) {
        String nextPlayer = game.getNextPlayer() == MarkerEnum.BLANK ? "X can start the game" : String.format("%s Player", game.getNextPlayer());
        return Game.builder()
                .id(game.getId())
                .endGame(game.isEndGame())
                .nextPlayer(nextPlayer)
                .build();
    }
}