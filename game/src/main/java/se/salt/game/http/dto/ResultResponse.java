package se.salt.game.http.dto;

import java.util.Map;

public record ResultResponse(Map<String, Double> results) {

    public static ResultResponse from(Map<String, Double> results) {
        return new ResultResponse(results);
    }
}
