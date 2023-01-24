package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static java.util.Objects.isNull;

@Service
@Validated
public class PLayerService {
    private final PlayerRepository playerRepository;
    private static final String BAD_REQUEST_MESSAGE = "Given ID is invalid: ";
    private static final String NOT_FOUND_REQUEST_MESSAGE = "ID is not found: ";

    public PLayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    /**
     * method can not search by name, title, after, before....
     */
    public List<Player> findAll(Map<String, String> playerParams) {
        return playerRepository.findAllByParams(playerParams);
    }

    public Integer count(@RequestParam Map<String, String> playerParams) {
        return Math.toIntExact(playerRepository.countByParams(playerParams));
    }

    public Player create(Player player) {
/**
 * We do not create a player if:
 * - all parameters from Data Params are not specified (except for prohibited ones);
 * - the value of the parameter "name" or "name"
 * the size fits the fields in the database (12 and 30 characters);
 * - the value of the "name" parameter is an empty string;
 * - experience is outside the specified limits;
 * - "birthday": [Long] < 0;
 * - the date of registration is outside the specified limits.
 * In the case of all of the above, you must answer
 * error code 400.
 * */

        if (isPlayerEmpty(player)
                || player.getName().isEmpty()
                || player.getExperience() < 0
                || player.getExperience() > 10000000
                || player.getBirthday().getTime() < 0
                || player.getTitle().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + player.getId());
        }
        player.setLevel(getCurrentLevel(player));
        player.setUntilNextLevel(getUntilNextLevel(player));
        return playerRepository.save(player);
    }

    public Player getPlayerById(String sID) {
        Long id = catchException(sID);
        return playerRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_REQUEST_MESSAGE + sID));
    }

    public ResponseEntity<Object> update(String sID, Player newPlayer1) {

        Long id = catchException(sID);
        Optional<Player> byId = playerRepository.findById(id);
        if (byId.isPresent() && isPlayerEmpty(newPlayer1)) {
            return ResponseEntity.ok(byId.get());
        }
        if ((newPlayer1.getExperience() != null
                && (newPlayer1.getExperience() < 0
                || newPlayer1.getExperience() > 10000000))
                || (newPlayer1.getBirthday() != null && newPlayer1.getBirthday().getTime() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + sID);
        }

        return ResponseEntity.ok(byId.map(player -> {
            player.setName(newPlayer1.getName() != null ? newPlayer1.getName() : player.getName());
            player.setTitle(newPlayer1.getTitle() != null ? newPlayer1.getTitle() : player.getTitle());
            player.setRace(newPlayer1.getRace() != null ? newPlayer1.getRace() : player.getRace());
            player.setProfession(newPlayer1.getProfession() != null ? newPlayer1.getProfession() : player.getProfession());
            player.setBirthday(newPlayer1.getBirthday() != null ? newPlayer1.getBirthday() : player.getBirthday());
            player.setBanned(newPlayer1.getBanned() != null ? newPlayer1.getBanned() : false);

            if (!isNull(newPlayer1.getExperience())) {
                player.setExperience(newPlayer1.getExperience());
                player.setLevel(getCurrentLevel(player));
                player.setUntilNextLevel(getUntilNextLevel(player));
            }
            return playerRepository.save(player);
        }).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_REQUEST_MESSAGE + sID);
        }));
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
            if (id < 0 || id == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + sID);
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, BAD_REQUEST_MESSAGE + sID);
        }
    }

    private boolean isPlayerEmpty(Player player) {
        return player.getBanned() == null &&
                player.getId() == null &&
                player.getRace() == null &&
                player.getProfession() == null &&
                player.getBirthday() == null &&
                player.getTitle() == null &&
                player.getLevel() == null &&
                player.getName() == null &&
                player.getUntilNextLevel() == null;
    }
}
