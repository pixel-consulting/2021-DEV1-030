package com.digitalstork.tictactoe.service;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.dto.PlayGame;
import com.digitalstork.tictactoe.model.GameEntity;
import com.digitalstork.tictactoe.model.MarkerEnum;
import com.digitalstork.tictactoe.repository.GameRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GameServiceTest {

    @InjectMocks
    GameService gameService;

    @Mock
    GameRepository gameRepository;

    @Test
    public void should_create_new_game_and_return_Game_with_full_information() {

        // Given
        GameEntity gameEntity = new GameEntity().toBuilder()
                .id(UUID.randomUUID())
                .nextPlayer(MarkerEnum.BLANK)
                .endGame(false)
                .build();

        // When
        when(gameRepository.save(any())).thenReturn(gameEntity);
        Game game = gameService.createGame();

        // Then
        assertNotNull(game);
        assertNotNull(game.getId());
        assertFalse(game.isEndGame());
        assertEquals("X can start the game", game.getNextPlayer());

    }


    @Test
    public void should_throw_IllegalArgumentException_when_first_player_is_not_X() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("O")
                .build();

        Optional<GameEntity> gameById = Optional.of(new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.O)
                .row0column0(MarkerEnum.BLANK)
                .build());

        // When
        when(gameRepository.findById(any())).thenReturn(gameById);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The first player should be: X", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_player_name_not_equal_nextPlayer() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = GameEntity.builder()
                .id(uuid)
                .nextPlayer(MarkerEnum.O)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The next player should be: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: No one", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_box_isNotBlank() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("O")
                .col(0)
                .row(0)
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .row0column0(MarkerEnum.O)
                .nextPlayer(MarkerEnum.O)
                .endGame(false)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The asked box is not Blank row = 0, col = 0 ", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_winner_is_O_with_any_case() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("O")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.O)
                .row0column0(MarkerEnum.O)
                .row0column1(MarkerEnum.O)
                .row0column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_winner_is_X_with_any_case() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column0(MarkerEnum.O)
                .row0column1(MarkerEnum.O)
                .row0column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_10_11_12() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row1column0(MarkerEnum.O)
                .row1column1(MarkerEnum.O)
                .row1column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_20_21_22() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row2column0(MarkerEnum.O)
                .row2column1(MarkerEnum.O)
                .row2column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_00_10_20() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column0(MarkerEnum.O)
                .row1column0(MarkerEnum.O)
                .row2column0(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_01_11_21() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column1(MarkerEnum.O)
                .row1column1(MarkerEnum.O)
                .row2column1(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_02_12_22() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column2(MarkerEnum.O)
                .row1column2(MarkerEnum.O)
                .row2column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_00_11_22() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column0(MarkerEnum.O)
                .row1column1(MarkerEnum.O)
                .row2column2(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_throw_IllegalArgumentException_when_the_game_is_end_and_match_case_02_11_20() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("X")
                .build();

        GameEntity gameById = new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.X)
                .row0column2(MarkerEnum.O)
                .row1column1(MarkerEnum.O)
                .row2column0(MarkerEnum.O)
                .endGame(true)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.of(gameById));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals("The game is end and the winner was: O", exception.getMessage());
    }

    @Test
    public void should_return_Game_with_isWinner_true() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("O")
                .row(0)
                .col(2)
                .build();

        Optional<GameEntity> gameById = Optional.of(new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.O)
                .row0column0(MarkerEnum.O)
                .row0column1(MarkerEnum.O)
                .build());

        // When
        when(gameRepository.findById(any())).thenReturn(gameById);
        when(gameRepository.save(any())).thenReturn(Mockito.mock(GameEntity.class));
        Game response = gameService.play(playGame);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("X Player", response.getNextPlayer());
        assertTrue(response.isEndGame());
    }

    @Test
    public void should_return_Game_with_isGameTie_true() {

        // Given
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .player("O")
                .row(1)
                .col(1)
                .build();

        Optional<GameEntity> gameById = Optional.of(new GameEntity().toBuilder()
                .id(uuid)
                .nextPlayer(MarkerEnum.O)
                .row0column0(MarkerEnum.O)
                .row0column1(MarkerEnum.X)
                .row0column2(MarkerEnum.O)
                .row1column0(MarkerEnum.O)
                .row1column2(MarkerEnum.X)
                .row2column0(MarkerEnum.X)
                .row2column1(MarkerEnum.O)
                .row2column2(MarkerEnum.X)
                .build());

        // When
        when(gameRepository.findById(any())).thenReturn(gameById);
        when(gameRepository.save(any())).thenReturn(Mockito.mock(GameEntity.class));
        Game response = gameService.play(playGame);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("X Player", response.getNextPlayer());
        assertTrue(response.isEndGame());
    }

    @Test
    public void should_throw_exception_when_Game_Id_not_found() {

        // Given
        PlayGame playGame = PlayGame.builder()
                .id(UUID.randomUUID().toString())
                .player("O")
                .row(1)
                .col(1)
                .build();

        // When
        when(gameRepository.findById(any())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameService.play(playGame)
        );

        // Then
        assertEquals(String.format("Game with Id %s is not found!", playGame.getId()), exception.getMessage());
    }


}