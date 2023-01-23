package com.game.entity;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PlayerSpecification {
    public Specification<Player> getPlayers(Map<String, String> request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.get("name") != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + request.get("name") + "%"));
            }
            if (request.get("title") != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + request.get("title") + "%"));
            }

            if (request.get("after") != null || request.get("before") != null) {
                Date after = new Date(request.get("after") != null ? Long.parseLong(request.get("after")) : 0);
                Date before = new Date(request.get("before") != null ? Long.parseLong(request.get("before")) : 1719530400000L);
                System.out.println("Before: " + before + " After: " + after);
                Predicate data = criteriaBuilder.between(root.get("birthday"),
                        after, before
                );
                predicates.add(data);
            }
            if (request.get("race") != null) {
                predicates.add(criteriaBuilder.equal(root.get("race"), Race.valueOf(request.get("race").toUpperCase())));
            }
            if (request.get("profession") != null) {
                predicates.add(criteriaBuilder.equal(root.get("profession"), Profession.valueOf(request.get("profession").toUpperCase())));
            }
            if (request.get("banned") != null) {
                predicates.add(criteriaBuilder.equal(root.get("banned"), Boolean.valueOf(request.get("banned").toUpperCase())));
            }
            if (request.get("minExperience") != null || request.get("maxExperience") != null) {
                int minExperience = request.get("minExperience") != null ? Integer.parseInt(request.get("minExperience")) : Integer.parseInt("0");
                int maxExperience = request.get("maxExperience") != null ? Integer.parseInt(request.get("maxExperience")) : Integer.MAX_VALUE;
                System.out.println("Girdi bura minExp: " + minExperience + "maxExp : " + maxExperience);
                predicates.add(criteriaBuilder.between(root.get("experience"),
                        minExperience, maxExperience
                ));
            }
            if (request.get("minLevel") != null || request.get("maxLevel") != null) {
                int minLevel = request.get("minLevel") != null ? Integer.parseInt(request.get("minLevel")) : Integer.parseInt("0");
                int maxLevel = (request.get("maxLevel") != null) ? Integer.parseInt(request.get("maxLevel")) : Integer.MAX_VALUE;
                System.out.println("girdi bura minLevel: " + minLevel + " , maxLevel: " + maxLevel);
                predicates.add(criteriaBuilder.between(root.get("level"),
                        minLevel, maxLevel
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
