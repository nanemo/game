package com.game.repository;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerRepositoryCustom {
    List<Player> findAllByParams(Map<String, String> playerParams);

    Long countByParams(Map<String, String> playerParams);
}
