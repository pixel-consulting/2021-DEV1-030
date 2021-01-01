package com.digitalstork.tictactoe.util;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.model.GameEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameEntityTransformerTest {

    private static final GameEntityTransformer GAME_ENTITY_TRANSFORMER = new GameEntityTransformer();

    @Test
    public void should_transform_from_GameEntity_to_Game_when_using_default_values() {

        // Given
        GameEntity gameEntity = new GameEntity().toBuilder()
                .id(UUID.randomUUID())
                .build();

        // When
        Game game = GAME_ENTITY_TRANSFORMER.apply(gameEntity);

        // Then
        assertNotNull(game);
        assertNotNull(game.getId());
        assertFalse(game.isEndGame());
        assertEquals("X can start the game", game.getNextPlayer());
    }

}