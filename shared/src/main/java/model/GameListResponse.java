package model;

import java.util.Collections;
import java.util.HashSet;

public record GameListResponse(HashSet<GameData> games) {
    public GameListResponse(HashSet<GameData> games) {
        this.games = new HashSet<>(Collections.unmodifiableSet(games));
    }
}