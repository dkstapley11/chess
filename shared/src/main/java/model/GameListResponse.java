package model;

import java.util.HashSet;

public record GameListResponse(HashSet<GameData> games) { }
