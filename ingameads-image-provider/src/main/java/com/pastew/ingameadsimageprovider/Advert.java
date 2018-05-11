package com.pastew.ingameadsimageprovider;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String gameId;

    private String imageURL;

    private long startDate;

    private long endDate;

    public Advert(String gameId, String imageURL, long startDate, long endDate) {
        this.gameId = gameId;
        this.imageURL = imageURL;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Advert() {
    }
}