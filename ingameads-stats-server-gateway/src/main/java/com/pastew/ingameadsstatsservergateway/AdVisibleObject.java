package com.pastew.ingameadsstatsservergateway;

public class AdVisibleObject {

    public long advertId;
    public String gameObjectName;
    public int visibleStartTimestamp;
    public int visibleEndTimestamp;
    public String advertURL;

    public AdVisibleObject() {
    }

    public long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(long advertId) {
        this.advertId = advertId;
    }

    public String getGameObjectName() {
        return gameObjectName;
    }

    public void setGameObjectName(String gameObjectName) {
        this.gameObjectName = gameObjectName;
    }

    public int getVisibleStartTimestamp() {
        return visibleStartTimestamp;
    }

    public void setVisibleStartTimestamp(int visibleStartTimestamp) {
        this.visibleStartTimestamp = visibleStartTimestamp;
    }

    public int getVisibleEndTimestamp() {
        return visibleEndTimestamp;
    }

    public void setVisibleEndTimestamp(int visibleEndTimestamp) {
        this.visibleEndTimestamp = visibleEndTimestamp;
    }

    public String getAdvertURL() {
        return advertURL;
    }

    public void setAdvertURL(String advertURL) {
        this.advertURL = advertURL;
    }

    @Override
    public String toString() {
        return "AdVisibleObject{" +
                "gameObjectName='" + gameObjectName + '\'' +
                ", visibleStartTimestamp=" + visibleStartTimestamp +
                ", visibleEndTimestamp=" + visibleEndTimestamp +
                '}';
    }
}

