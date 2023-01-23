package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        int pageNumber = playerParams.get("pageNumber") != null ? Integer.parseInt(playerParams.get("pageNumber")) : 0;
        //DECLARING INITIAL VARIABLES FOR FILTERING
        String name = playerParams.get("name");
        String title = playerParams.get("title");
        Race race = playerParams.get("race") != null ? Race.valueOf(playerParams.get("race")) : null;
        Profession profession = playerParams.get("profession") != null ? Profession.valueOf(playerParams.get("profession").toUpperCase()) : null;
        long after = playerParams.get("after") != null ? Long.parseLong(playerParams.get("after")) : 0;
        long before = playerParams.get("before") != null ? Long.parseLong(playerParams.get("before")) : 0;
        Boolean banned = playerParams.get("banned") != null ? Boolean.parseBoolean(playerParams.get("banned")) : null;
        int minExperience = playerParams.get("minExperience") != null ? Integer.parseInt(playerParams.get("minExperience")) : 0;
        int maxExperience = playerParams.get("maxExperience") != null ? Integer.parseInt(playerParams.get("maxExperience")) : 0;
        int minLevel = playerParams.get("minLevel") != null ? Integer.parseInt(playerParams.get("minLevel")) : 0;
        int maxLevel = playerParams.get("maxLevel") != null ? Integer.parseInt(playerParams.get("maxLevel")) : 0;
        int pageSize = playerParams.get("pageSize") != null ? Integer.parseInt(playerParams.get("pageSize")) : 3;
        String playerOrder = PlayerOrder.valueOf(playerParams.get("order") != null ? playerParams.get("order") : "NAME").getFieldName();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(playerOrder));
        List<Player> players = playerRepository.findAll(pageable).toList();


        if (playerParams.size() == 3) {
            return players;
        }

        if (name != null) {
            players = players
                    .stream()
                    .filter(player -> player.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (title != null) {
            players = players
                    .stream()
                    .filter(player -> player.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (after != 0) {
            players = players
                    .stream()
                    .filter(player -> player.getBirthday().getTime() > after)
                    .collect(Collectors.toList());
        }
        if (before != 0) {
            players = players.stream().filter(player -> player.getBirthday().getTime() < before).collect(Collectors.toList());
        }
        if (race != null) {
            players = players.stream().
                    filter(player -> player.getRace().equals(race)).collect(Collectors.toList());
        }
        if (profession != null) {
            players = players.stream().filter(player -> player.getProfession().equals(profession)).collect(Collectors.toList());
        }
        if (banned != null) {
            players = players.stream().filter(player -> player.getBanned().equals(banned)).collect(Collectors.toList());
        }
        if (minExperience != 0) {
            players = players.stream().filter(player -> player.getExperience() > minExperience).collect(Collectors.toList());
        }
        if (maxExperience != 0) {
            players = players.stream().filter(player -> player.getExperience() < maxExperience).collect(Collectors.toList());
        }
        if (minLevel != 0) {
            players = players.stream().filter(player -> player.getLevel() > minLevel).collect(Collectors.toList());
        }
        if (maxLevel != 0) {
            players = players.stream().filter(player -> player.getLevel() < maxLevel).collect(Collectors.toList());
        }
        return players;
    }


    public Integer count() {
        return (int) playerRepository.count();
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

        if (player.getName().isEmpty() || player.getExperience() < 0 || player.getExperience() > 10000000
                || player.getBirthday().getTime() < 0) {
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

    public Player update(String sID, Player newPlayer) {
        Long id = catchException(sID);
        return playerRepository.findById(id).map(player -> {
            player.setName(newPlayer.getName());
            player.setTitle(newPlayer.getTitle());
            player.setRace(newPlayer.getRace());
            player.setProfession(newPlayer.getProfession());
            player.setBirthday(newPlayer.getBirthday());
            if (newPlayer.getBanned() != null) {
                player.setBanned(newPlayer.getBanned());
            } else {
                player.setBanned(false);
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
