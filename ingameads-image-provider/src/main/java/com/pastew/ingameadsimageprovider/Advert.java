package com.pastew.ingameadsimageprovider;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Advert {

    @Id
    private Long id;

    private String gameId;

    @Column( length = 500 )
    private String imageURL;

    private long startDate;

    private long endDate;

    public Advert(Long id, String gameId, String imageURL, long startDate, long endDate) {
        this.id = id;
        this.gameId = gameId;
        this.imageURL = imageURL;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Advert() {
    }
}