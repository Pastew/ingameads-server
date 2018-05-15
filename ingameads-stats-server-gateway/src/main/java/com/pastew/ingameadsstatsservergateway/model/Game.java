package com.pastew.ingameadsstatsservergateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Game {

    @Id
    private String id;

    private List<AdVisibleObject> adVisibleObjectList;

    public Game(String id, List<AdVisibleObject> adVisibleObjectList) {
        this.id = id;
        this.adVisibleObjectList = adVisibleObjectList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AdVisibleObject> getAdVisibleObjectList() {
        return adVisibleObjectList;
    }

    public void setAdVisibleObjectList(List<AdVisibleObject> adVisibleObjectList) {
        this.adVisibleObjectList = adVisibleObjectList;
    }
}
