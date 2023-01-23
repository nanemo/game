package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Validated
public class PLayerService {
    @Autowired
    private PlayerRepository playerRepository;
    private static final String BAD_REQUEST_MESSAGE = "Given ID is invalid: ";
    private static final String NOT_FOUND_REQUEST_MESSAGE = "ID is not found: ";


    /**
     * method can not search by name, title, after, before....
     *
     * */
    public List<Player> findAll(Map<String, String> playerParams) {
        List<Player> players = new ArrayList<>();
        int pageNumber = Integer.parseInt(playerParams.get("pageNumber"));

        int pageSize = Integer.parseInt(playerParams.get("pageSize"));
        String playerOrder = PlayerOrder.valueOf(playerParams.get("order")).getFieldName();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(playerOrder));
        List<Player> allPlayersFromDB = playerRepository.findAll(pageable).toList();

        if (playerParams.size() == 3) {
            return allPlayersFromDB;
        }

        if (playerParams.get("name") != null) {
            players = searchPlayersByName(playerParams, allPlayersFromDB);
        }

        if (playerParams.get("title") != null) {
            players = searchPlayersByTitle(playerParams, allPlayersFromDB);
        }

        if (playerParams.get("after") != null) {

        }

        if (playerParams.get("before") != null) {

        }

//        if (Long.parseLong(playerParams.get("after")) == null)

//        String name = ;
//        String title = ;
//        Race race = Race.valueOf(playerParams.get("race"));
//        Profession profession = Profession.valueOf(playerParams.get("profession"));
//
//        Long after = ;
//        Long before = Long.parseLong(playerParams.get("before"));
//        Boolean banned = Boolean.parseBoolean(playerParams.get("banned"));
//        Integer minExperience = Integer.parseInt(playerParams.get("minExperience"));
//        Integer maxExperience = Integer.parseInt(playerParams.get("maxExperience"));
//        Integer minLevel = Integer.parseInt(playerParams.get("minLevel"));
//        Integer maxLevel = Integer.parseInt(playerParams.get("maxLevel"));

        return players;
    }

    private List<Player> searchPlayersByTitle(Map<String, String> playerParams, List<Player> allPlayersFromDB) {
        List<Player> pickedUpPlayers = new ArrayList<>();
        for (Player playerFromDB : allPlayersFromDB) {
            if (playerParams.get("title") != null || playerFromDB.getTitle().contains(playerParams.get("title").trim())) {
                System.out.println(playerParams.get("title") + " title");
                pickedUpPlayers.add(playerFromDB);
            }
        }
        return pickedUpPlayers;

    }

    private List<Player> searchPlayersByName(Map<String, String> playerParams, List<Player> allPlayersFromDB) {
        List<Player> pickedUpPlayers = new ArrayList<>();
        for (Player playerFromDB : allPlayersFromDB) {
            if (playerParams.get("name") != null || playerFromDB.getName().contains(playerParams.get("name").trim())) {
                System.out.println(playerParams.get("name") + " name");
                pickedUpPlayers.add(playerFromDB);
            }
        }
        return pickedUpPlayers;

    }

    private void searchPlayersByBirthday(List<Player> players) {

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
        Date date = new Date();
        DateFormat simple = new SimpleDateFormat("yyyy");
        Date birthday = player.getBirthday();

        if (player.getName().isEmpty() || player.getExperience() < 0 || player.getExperience() > 10000000
                || player.getBirthday().getTime() < 0 || Integer.parseInt(simple.format(date)) < 2000
                || Integer.parseInt(simple.format(date)) > 3000) {
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
            return playerRepository.saveAndFlush(player);
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
