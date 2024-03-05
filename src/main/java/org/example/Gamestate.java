package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gamestate {
    public void setTurnNo(Integer turnNo) {
        this.turnNo = turnNo;
    }

    public void setWinner(List<String> winner) {
        this.winner = winner;
    }

    public void setGameOver(Boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setMoves(String[] moves){
        this.moves = moves;
    }

    public void setMove(Pair<Integer, String> val) {
        this.moves[val.getValue0()] = val.getValue1();
    }

    private Integer turnNo;
    private List<String> winner;
    private Boolean gameOver;
    private String[] moves;
    @JsonIgnore
    private Gamestate gamestate;

    public Gamestate() {
        this.turnNo = 0;
        this.winner = new ArrayList<String>();
        this.gameOver = false;
        this.moves = new String[2];
    }

    public Integer getTurnNo() {
        return turnNo;
    }

    public List<String> getWinner() {
        return winner;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public String[] getMoves() {
        return moves;
    }
    @JsonCreator
    public Gamestate(
            @JsonProperty("turnNo") Integer turnNo,
            @JsonProperty("winner") List<String> winner,
            @JsonProperty("gameOver") Boolean gameOver,
            @JsonProperty("moves") String[] moves) {
        this.turnNo = turnNo;
        this.winner = winner;
        this.gameOver = gameOver;
        this.moves = moves;
    }

    public Gamestate updateGamestate(Object newVal){
        if (newVal instanceof Integer && newVal != this.turnNo) {
            setTurnNo((Integer) newVal);
        } else if (newVal instanceof String) {
            winner.add(newVal.toString());
        } else if (newVal instanceof Boolean && newVal != this.gameOver) {
            setGameOver((Boolean) newVal);
        } else if (newVal instanceof Pair && !Objects.equals(((Pair<Integer, String>) newVal).getValue1(), this.moves[((Pair<Integer, String>) newVal).getValue0()])) {
            setMove((Pair<Integer, String>) newVal);
        }

        return this;
    }

    @JsonCreator
    public static Gamestate fromJson(
            @JsonProperty("turnNo") Integer turnNo,
            @JsonProperty("winner") List<String> winner,
            @JsonProperty("gameOver") Boolean gameOver,
            @JsonProperty("moves") String[] moves) {
        Gamestate gamestate = new Gamestate();
        gamestate.setTurnNo(turnNo);
        gamestate.setWinner(winner);
        gamestate.setGameOver(gameOver);
        gamestate.setMoves(moves);
        return gamestate;
    }

    public byte[] gamestateToBytes(Gamestate gamestate){
        try {
            String jsonString = gamestate.asString();

            return jsonString.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String asString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(this);
        return jsonString;
    }

    public Gamestate getGamestate(){
        return this;
    }

}
