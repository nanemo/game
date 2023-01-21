package com.game.controller;


import com.game.entity.Player;
import com.game.exception.PlayerNotFoundException;
import com.game.service.PLayerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
class PlayerController {

    private final PLayerService service;

    PlayerController(PLayerService repository) {
        this.service = repository;
    }


    @GetMapping("/players")
    List<Player> all() {
        return service.findAll();
    }

    @GetMapping("/players/count")
    int count() {
        return service.count();
    }

    @PostMapping("/players")
    Player newPlayer(@RequestBody Player player) {
        return service.create(player);
    }

    // Single item

    @GetMapping("/players/{id}")
    Player getPlayer(@PathVariable String id) throws PlayerNotFoundException {
        return service.getPlayerById(id);
    }

    @PutMapping("/employees/{id}")
    Player replaceEmployee(@RequestBody Player newPlayer, @PathVariable String id) {
        return service.update(newPlayer, id);
    }

    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable String id) {
        service.delete(id);
    }
}