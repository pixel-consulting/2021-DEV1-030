package com.digitalstork.tictactoe.controller;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.dto.PlayGame;
import com.digitalstork.tictactoe.service.GameService;
import com.digitalstork.tictactoe.exception.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GameService gameService;

    @Test
    public void should_return_OK_when_call_new_game_endpoint() {

        // Given
        String url = "http://localhost:" + port + "/v0/tictactoe/new";
        UUID uuid = UUID.randomUUID();
        Game game = Game.builder()
                .id(uuid)
                .nextPlayer("O or X can start the game")
                .endGame(false)
                .build();

        // When
        when(gameService.createGame()).thenReturn(game);
        ResponseEntity<Game> response = this.restTemplate.exchange(url, HttpMethod.GET, null, Game.class);

        //Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uuid, response.getBody().getId());
        assertEquals("O or X can start the game", response.getBody().getNextPlayer());
        assertFalse(response.getBody().isEndGame());
    }

    @Test
    @DisplayName("The col and row should be between or equals 0 and 2.")
    public void should_throw_IllegalArgumentException_when_row_or_col_are_not_correct() {

        // Given
        String url = "http://localhost:" + port + "/v0/tictactoe/play";
        PlayGame playGame = PlayGame.builder()
                .col(3)
                .row(-1)
                .build();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        // When
        ResponseEntity<Error> response = this.restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity(playGame, headers), Error.class);

        //Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong row or column information, they should be between 0 and 2!", response.getBody().getError());

    }

    @Test
    @DisplayName("The player should be X or O.")
    public void should_throw_IllegalArgumentException_when_player_is_not_X_or_O() {

        // Given
        String url = "http://localhost:" + port + "/v0/tictactoe/play";
        PlayGame playGame = PlayGame.builder()
                .col(2)
                .row(0)
                .player("#")
                .build();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");


        // When
        ResponseEntity<Error> response = this.restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity(playGame, headers), Error.class);

        //Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Wrong player name, it should be X or O.", response.getBody().getError());
    }

    @Test
    @DisplayName("The return OK when all information are correct.")
    public void should_return_OK_when_all_information_are_correct() {

        // Given
        String url = "http://localhost:" + port + "/v0/tictactoe/play";
        UUID uuid = UUID.randomUUID();
        PlayGame playGame = PlayGame.builder()
                .id(uuid.toString())
                .col(2)
                .row(0)
                .player("O")
                .build();

        Game game = Game.builder()
                .id(uuid)
                .endGame(false)
                .nextPlayer("X")
                .build();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        // When
        when(gameService.play(any())).thenReturn(game);
        ResponseEntity<Game> response = this.restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity(playGame, headers), Game.class);

        //Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Game body = response.getBody();
        assertNotNull(body);
        assertEquals(uuid, body.getId());
        assertFalse(body.isEndGame());
        assertEquals("X", body.getNextPlayer());
    }
}