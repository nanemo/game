package com.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.lang.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player", schema = "rpg")
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @Column(name = "name", length = 12)
    @NonNull
    private String name;

    @Column(name = "title", length = 30)
    @NonNull
    private String title;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    @NonNull
    private Race race;

    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    @NonNull
    private Profession profession;

    @Column(name = "experience", length = 10000000)
    @NonNull
    private Integer experience;

    @Column(name = "level")
    @NonNull
    private Integer level;

    @Column(name = "untilNextLevel")
    @NonNull
    private Integer untilNextLevel;

    @Column(name = "birthday")
    @NonNull
    private Date birthday;

    @Column(name = "banned", columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean banned;


}
