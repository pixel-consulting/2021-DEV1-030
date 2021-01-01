package com.digitalstork.tictactoe.service;

import com.digitalstork.tictactoe.dto.Game;
import com.digitalstork.tictactoe.dto.PlayGame;
import com.digitalstork.tictactoe.model.GameEntity;
import com.digitalstork.tictactoe.model.MarkerEnum;
import com.digitalstork.tictactoe.repository.GameRepository;
import com.digitalstork.tictactoe.util.GameEntityTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

@Service
@Transactional
public class GameService {

    private GameRepository gameRepository;
    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
    private static final GameEntityTransformer GAME_ENTITY_TRANSFORMER = new GameEntityTransformer();

    /**
     * All possible combinations to win.
     */
    private static final List<List<String>> WINNER_COMBINATIONS = Arrays.asList(
            Arrays.asList("00", "01", "02"), Arrays.asList("10", "11", "12"), Arrays.asList("20", "21", "22"),
            Arrays.asList("00", "10", "20"), Arrays.asList("01", "11", "21"), Arrays.asList("02", "12", "22"),
            Arrays.asList("00", "11", "22"), Arrays.asList("02", "11", "20")
    );

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame() {
        GameEntity game = gameRepository.save(new GameEntity());

        LOG.info("Initial game: {}\n", game.drawBoard());

        return GAME_ENTITY_TRANSFORMER.apply(game);
    }

    public Game play(PlayGame playGame) {

        Optional<GameEntity> gameById = gameRepository.findById(UUID.fromString(playGame.getId()));

        if (gameById.isPresent()) {

            GameEntity game = gameById.get();

            // Check if the first player is X.
            if (!playGame.getPlayer().equals("X") && isGameEmpty(game)) {
                throw new IllegalArgumentException("The first player should be: " + MarkerEnum.X);
            }

            // Check if the next player is correct.
            if (game.getNextPlayer() != MarkerEnum.BLANK && !game.getNextPlayer().getValue().equals(playGame.getPlayer())) {
                throw new IllegalArgumentException("The next player should be: " + game.getNextPlayer());
            }

            // Check if the game is end.
            if (game.isEndGame()) {
                throw new IllegalArgumentException(String.format("The game is end and the winner was: %s", getWinner(game)));
            }

            // Check if the asked box is blank.
            if (!isBoxBlank(gameById.get(), playGame.getCol(), playGame.getRow())) {
                throw new IllegalArgumentException(String.format("The asked box is not Blank row = %s, col = %s ", playGame.getRow(), playGame.getCol()));
            }

            // Change entity box value.
            updateBox(game, playGame.getPlayer(), playGame.getRow(), playGame.getCol());

            // Check if there are a winner.
            MarkerEnum nextPlayer = "O".equals(playGame.getPlayer()) ? MarkerEnum.X : MarkerEnum.O;
            game.setNextPlayer(nextPlayer);

            if (isWinner(game) || isGameTie(game)) {
                game.setEndGame(true);
                LOG.info("The winner is: {}", getWinner(game));
            }

            this.gameRepository.save(game);

            LOG.info("Initial game: {}\n", game.drawBoard());
            return GAME_ENTITY_TRANSFORMER.apply(game);
        }

        // throw exception.
        throw new IllegalArgumentException(String.format("Game with Id %s is not found!", playGame.getId()));
    }

    /**
     * Check if a given box is Blank.
     *
     * @param gameEntity
     * @param col
     * @param row
     * @return true if the given box is Blank.
     */
    private boolean isBoxBlank(GameEntity gameEntity, int col, int row) {
        Map<String, MarkerEnum> boxs = Map.of(
                "00", gameEntity.getRow0column0(), "01", gameEntity.getRow0column1(), "02", gameEntity.getRow0column2(),
                "10", gameEntity.getRow1column0(), "11", gameEntity.getRow1column1(), "12", gameEntity.getRow1column2(),
                "20", gameEntity.getRow2column0(), "21", gameEntity.getRow2column1(), "22", gameEntity.getRow2column2()
        );

        return boxs.get(String.format("%d%d", row, col)) == MarkerEnum.BLANK;
    }


    /**
     * Put X or O in the correct box.
     *
     * @param gameEntity
     * @param player
     * @param row
     * @param col
     */
    private void updateBox(GameEntity gameEntity, String player, int row, int col) {
        Map<String, Consumer<MarkerEnum>> boxes = Map.of(
                "00", gameEntity::setRow0column0, "01", gameEntity::setRow0column1, "02", gameEntity::setRow0column2,
                "10", gameEntity::setRow1column0, "11", gameEntity::setRow1column1, "12", gameEntity::setRow1column2,
                "20", gameEntity::setRow2column0, "21", gameEntity::setRow2column1, "22", gameEntity::setRow2column2
        );

        boxes.get(String.format("%d%d", row, col)).accept(MarkerEnum.valueOf(player));
    }

    /**
     * Check each player if he can be a winner.
     *
     * @param gameEntity
     * @return true if X or O win the game.
     */
    private boolean isWinner(GameEntity gameEntity) {
        return isWinnerByName(gameEntity, MarkerEnum.O) || isWinnerByName(gameEntity, MarkerEnum.X);
    }

    /**
     * Check if there are an empty box.
     *
     * @param gameEntity
     * @return true if all boxes are full.
     */
    private boolean isGameTie(GameEntity gameEntity) {
        List<MarkerEnum> boxes = List.of(
                gameEntity.getRow0column0(), gameEntity.getRow0column1(), gameEntity.getRow0column2(),
                gameEntity.getRow1column0(), gameEntity.getRow1column1(), gameEntity.getRow1column2(),
                gameEntity.getRow2column0(), gameEntity.getRow2column1(), gameEntity.getRow2column2()
        );
        return boxes.stream().allMatch(b -> b != MarkerEnum.BLANK);
    }

    /**
     * @param gameEntity
     * @return the name of the winner, if there are no winner then return 'No one'.
     */
    private String getWinner(GameEntity gameEntity) {
        if (isWinnerByName(gameEntity, MarkerEnum.O)) {
            return MarkerEnum.O.getValue();
        }
        if (isWinnerByName(gameEntity, MarkerEnum.X)) {
            return MarkerEnum.X.getValue();
        }
        return "No one";
    }

    /**
     * Check if the player X or O win.
     *
     * @param gameEntity
     * @param player     X or O
     * @return true if there are a winner by name.
     */
    private boolean isWinnerByName(GameEntity gameEntity, MarkerEnum player) {
        Map<String, MarkerEnum> boxes = Map.of(
                "00", gameEntity.getRow0column0(), "01", gameEntity.getRow0column1(), "02", gameEntity.getRow0column2(),
                "10", gameEntity.getRow1column0(), "11", gameEntity.getRow1column1(), "12", gameEntity.getRow1column2(),
                "20", gameEntity.getRow2column0(), "21", gameEntity.getRow2column1(), "22", gameEntity.getRow2column2()
        );
        return WINNER_COMBINATIONS.stream()
                .anyMatch(combination -> combination.stream().allMatch(b -> boxes.get(b) == player));
    }


    /**
     * Check if all boxes are empty.
     *
     * @param gameEntity
     * @return true if all boxes are empty.
     */
    private boolean isGameEmpty(GameEntity gameEntity) {
        List<MarkerEnum> boxes = List.of(
                gameEntity.getRow0column0(), gameEntity.getRow0column1(), gameEntity.getRow0column2(),
                gameEntity.getRow1column0(), gameEntity.getRow1column1(), gameEntity.getRow1column2(),
                gameEntity.getRow2column0(), gameEntity.getRow2column1(), gameEntity.getRow2column2()
        );
        return boxes.stream().allMatch(b -> b == MarkerEnum.BLANK);
    }
}
