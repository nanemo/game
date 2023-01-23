package com.game.controller;


import com.game.entity.Player;
import com.game.exception.PlayerNotFoundException;
import com.game.service.PLayerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest")
class PlayerController {

    private final PLayerService service;

    PlayerController(PLayerService repository) {
        this.service = repository;
    }


    @GetMapping("/players")
    List<Player> all(@RequestParam Map<String, String> playerParams) {
        return service.findAll(playerParams);
    }

    @GetMapping("/players/count")
    int count() {
        return service.count();
    }

    @PostMapping("/players")
    public Player newPlayer(@RequestBody Player player) {
        return service.create(player);
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable String id) throws PlayerNotFoundException {
        return service.getPlayerById(id);
    }

    @PostMapping("/players/{id}")
    public Player replacePlayer(@PathVariable String id, @RequestBody Player newPlayer) {
        return service.update(id, newPlayer);
    }

    @DeleteMapping("/players/{id}")
    void deleteEmployee(@PathVariable String id) {
        service.delete(id);
    }
}