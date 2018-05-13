package com.pastew.ingameadsimageprovider;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String gameId;

    @Column( length = 500 )
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