package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Player> findAllByParams(Map<String, String> playerParams) {
        String order = isNull(playerParams.get("order")) ? "id" : playerParams.get("order").toLowerCase();
        int pageNumber = isNull(playerParams.get("pageNumber")) ? 0 : Integer.parseInt(playerParams.get("pageNumber"));
        int pageSize = isNull(playerParams.get("pageSize")) ? 3 : Integer.parseInt(playerParams.get("pageSize"));

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> cq = cb.createQuery(Player.class);
        Root<Player> player = cq.from(Player.class);

        List<Predicate> predicates = getPredicates(player, cb, playerParams);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(player.get(order)));

        TypedQuery<Player> query = entityManager.createQuery(cq);

        return query
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public Long countByParams(Map<String, String> playerParams) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Player> player = cq.from(Player.class);
        cq.select(cb.count(player));

        List<Predicate> predicates = getPredicates(player, cb, playerParams);

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = entityManager.createQuery(cq);

        return query.getSingleResult();
    }

    private List<Predicate> getPredicates(Root<Player> player, CriteriaBuilder cb, Map<String, String> playerParams) {
        List<Predicate> predicates = new ArrayList<>();

        if (playerParams.get("name") != null) predicates.add(cb.like(cb.upper(player.get("name")),
                "%" + playerParams.get("name").toUpperCase() + "%"));
        if (playerParams.get("title") != null) predicates.add(cb.like(cb.upper(player.get("title")),
                "%" + playerParams.get("title").toUpperCase() + "%"));
        if (playerParams.get("race") != null) predicates.add(cb.equal(player.get("race"),
                Race.valueOf(playerParams.get("race"))));
        if (playerParams.get("profession") != null) predicates.add(cb.equal(player.get("profession"),
                Profession.valueOf(playerParams.get("profession").toUpperCase())));
        if (playerParams.get("after") != null) predicates.add(cb.greaterThan(player.get("birthday"),
                new Date(Long.parseLong(playerParams.get("after")))));
        if (playerParams.get("before") != null) predicates.add(cb.lessThan(player.get("birthday"),
                new Date(Long.parseLong(playerParams.get("before")))));
        if (playerParams.get("minExperience") != null) predicates.add(cb.greaterThanOrEqualTo(player.get("experience"),
                Integer.parseInt(playerParams.get("minExperience"))));
        if (playerParams.get("maxExperience") != null) predicates.add(cb.lessThanOrEqualTo(player.get("experience"),
                Integer.parseInt(playerParams.get("maxExperience"))));
        if (playerParams.get("minLevel") != null) predicates.add(cb.greaterThanOrEqualTo(player.get("level"),
                Integer.parseInt(playerParams.get("minLevel"))));
        if (playerParams.get("maxLevel") != null) predicates.add(cb.lessThanOrEqualTo(player.get("level"),
                Integer.parseInt(playerParams.get("maxLevel"))));
        if (playerParams.get("banned") != null) predicates.add(Boolean.parseBoolean(playerParams.get("banned")) ?
                cb.isTrue(player.get("banned")) : cb.isFalse(player.get("banned")));

        return predicates;
    }
}
