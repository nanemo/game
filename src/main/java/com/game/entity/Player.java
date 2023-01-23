package com.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.springframework.lang.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PLAYER", schema = "RPG")
@Getter
@Setter
@DynamicUpdate
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 12)
    private String name;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    private Profession profession;

    @Column(name = "experience", length = 10000000)
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "banned", columnDefinition = "false")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean banned;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race=" + race +
                ", profession=" + profession +
                ", experience=" + experience +
                ", level=" + level +
                ", untilNextLevel=" + untilNextLevel +
                ", birthday=" + birthday +
                ", banned=" + banned +
                '}';
    }
}
