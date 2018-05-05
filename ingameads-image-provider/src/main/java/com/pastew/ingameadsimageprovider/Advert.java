package com.pastew.ingameadsimageprovider;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Advert {

    @Id
    @GeneratedValue
    private Long id;

    private String gameId;

    private String imageURL;
}