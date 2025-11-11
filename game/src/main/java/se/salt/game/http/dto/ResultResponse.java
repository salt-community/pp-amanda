package se.salt.game.http.dto;

import java.util.Map;

public record ResultResponse(Map<String, Double> players) {
    
    public static ResultResponse from(Map<String, Double> players) {
        return new ResultResponse(players);
    }
}
