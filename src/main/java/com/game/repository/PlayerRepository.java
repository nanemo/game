package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
        Page<Player> findAllByNameLike(String name , Pageable pageable);
        Page<Player> findAllByNameLikeAndTitleLike(String name, String title, Pageable pageable);
//        Page<Player> findAllByNameLikeAndTitleLikeAndBir
}
