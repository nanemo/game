package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Validated
public class PLayerService {
    @Autowired
    private PlayerRepository playerRepository;
    private static final String BAD_REQUEST_MESSAGE = "Given ID is invalid: ";
    private static final String NOT_FOUND_REQUEST_MESSAGE = "ID is not found: ";

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Integer count() {
        return (int) playerRepository.count();
    }

    public Player create(Player player) {
// NonNull ilə playerin fieldlərini yoxla, nulldırsa create eləmə
        player.setLevel(getCurrentLevel(player));
        player.setUntilNextLevel(getUntilNextLevel(player));
        return playerRepository.save(player);
    }

    public Player getPlayerById(String sID) {
        Long id = catchException(sID);
        return playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_REQUEST_MESSAGE + sID));
    }

    public Player update(Player newPlayer, String sID) {
        Long id = catchException(sID);
        return playerRepository.findById(id).map(player -> {
            if (newPlayer.getName() != null) {
                player.setName(newPlayer.getName());
            }
            if (newPlayer.getTitle() != null) {
                player.setTitle(newPlayer.getTitle());
            }
            if (newPlayer.getRace() != null) {
                player.setRace(newPlayer.getRace());
            }
            if (newPlayer.getProfession() != null) {
                player.setProfession(newPlayer.getProfession());
            }
            if (newPlayer.getLevel() != null) {
                player.setLevel(getCurrentLevel(player));
            }
            if (newPlayer.getUntilNextLevel() != null) {
                player.setUntilNextLevel(getUntilNextLevel(player));
            }
            if (newPlayer.getBirthday() != null) {
                player.setBirthday(newPlayer.getBirthday());
            }
            if (newPlayer.getBanned() != null) {
                player.setBanned(newPlayer.getBanned());
            }
            return playerRepository.save(player);
        }).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_REQUEST_MESSAGE + sID);
        });
    }

    public void delete(String sID) {
        Player playerById = getPlayerById(sID);
        playerRepository.delete(playerById);
    }

    private Integer getCurrentLevel(Player player) {
        return ((int) (Math.sqrt((player.getExperience() * 200) + 2500)) - 50) / 100;
    }

    private Integer getUntilNextLevel(Player player) {
        Integer lvl = player.getLevel();
        return ((50 * (lvl + 1)) * (lvl + 2)) - player.getExperience();
    }

    private Long catchException(String sID) {
        try {
            long id = Long.parseLong(sID);
            if (id < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + sID);
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + sID);
        }
    }

}
