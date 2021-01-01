package com.digitalstork.tictactoe.controller;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.dto.PlayGame;
import com.digitalstork.tictactoe.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v0/tictactoe")
public class GameController {
    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }


    @GetMapping("/new")
    public ResponseEntity<Game> createNewGame() {

        LOG.info("Create new Tic Tac Toe Game.");
        Game game = gameService.createGame();
        LOG.info("Game Id: {}\n", game.getId());

        return ResponseEntity.ok(game);
    }


    @PostMapping("/play")
    public ResponseEntity playGame(@RequestBody PlayGame playGame) {

        LOG.info("Play game {}", playGame);
        if (playGame.getCol() < 0 || playGame.getCol() > 2 || playGame.getRow() < 0 || playGame.getRow() > 2) {
            throw new IllegalArgumentException("Wrong row or column information, they should be between 0 and 2!");
        }
        if (!"X".equals(playGame.getPlayer()) && !"O".equals(playGame.getPlayer())) {
            throw new IllegalArgumentException("Wrong player name, it should be X or O.");
        }
        return ResponseEntity.ok(gameService.play(playGame));
    }
}
