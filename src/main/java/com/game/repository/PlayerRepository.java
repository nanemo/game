package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PlayerRepository extends PagingAndSortingRepository<Player, Long> , JpaSpecificationExecutor<Player> {
    List<Player> findAllByNameLike(String name, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLike(String name, String title, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLikeAndBirthdayBetween(String name, String title, Date birthday, Date birthday2, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLikeAndBirthdayBetweenAndRace(String name, String title, Date birthday, Date birthday2, Race race, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLikeAndBirthdayBetweenAndRaceAndProfession(String name, String title, Date birthday, Date birthday2, Race race, Profession profession, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLikeAndBirthdayBetweenAndRaceAndProfessionAndExperienceBetween(String name, String title, Date birthday, Date birthday2, Race race, Profession profession, Integer experience, Integer experience2, Pageable pageable);

    List<Player> findAllByNameLikeAndTitleLikeAndBirthdayBetweenAndRaceAndProfessionAndExperienceBetweenAndLevelBetween(String name, String title, Date birthday, Date birthday2, Race race, Profession profession, Integer experience, Integer experience2, Integer level, Integer level2, Pageable pageable);
}
