package com.game.service;

import com.game.entity.Player;
import com.game.exception.PlayerNotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PLayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Integer count() {
        return (int) playerRepository.count();
    }

    public Player create(Player player) {
        return playerRepository.save(player);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public Player update(Player newPlayer, Long id) {
        return playerRepository.findById(id)
                .map(player -> {
                    if (player.getName() == null) {
                        player.setName(newPlayer.getName());
                    }
                    if (player.getTitle() == null) {
                        player.setTitle(newPlayer.getTitle());
                    }
                    if (player.getRace() == null) {
                        player.setRace(newPlayer.getRace());
                    }
                    if (player.getProfession() == null) {
                        player.setProfession(newPlayer.getProfession());
                    }
                    if (player.getLevel() == null) {
                        player.setLevel(newPlayer.getLevel());
                    }
                    if (player.getUntilNextLevel() == null) {
                        player.setUntilNextLevel(newPlayer.getUntilNextLevel());
                    }
                    if (player.getBirthday() == null) {
                        player.setBirthday(newPlayer.getBirthday());
                    }
                    if (player.getBanned() == null) {
                        player.setBanned(newPlayer.getBanned());
                    }
                    return playerRepository.save(player);
                })
                .orElseThrow(() -> {
                    throw new PlayerNotFoundException(id);
                });
    }

    public void delete(Long id) {
        playerRepository.delete(getPlayerById(id)); // Bu düzgündür ki, mən elə burdakı getPlayerByİd methodunu
                                                    // çağırıram əgər yoxdursa 404 qaytarır yox əgər vardısa silir.
    }



}
