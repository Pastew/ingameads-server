package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.Game.Game;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Advert {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Game game;

    @Column( length = 500 )
    private String imageURL;

    private long startDate;

    private long endDate;

    public double calculateCost() {
        int days = 4; //TODY: Calculate end - start days
        return days * game.getPricePerDay();
    }
}