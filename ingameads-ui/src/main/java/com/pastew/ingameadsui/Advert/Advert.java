package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.Game.Game;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Advert {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Game game;

    private String imageURL;

    private long startDate;

    private long endDate;

}