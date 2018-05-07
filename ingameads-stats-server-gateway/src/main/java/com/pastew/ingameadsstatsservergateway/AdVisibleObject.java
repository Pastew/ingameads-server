package com.pastew.ingameadsstatsservergateway;

public class AdVisibleObject
{
    public String gameObjectName;
    public int visibleStartTimestamp;
    public int visibleEndTimestamp;

    public AdVisibleObject() {
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

    @Override
    public String toString() {
        return "AdVisibleObject{" +
                "gameObjectName='" + gameObjectName + '\'' +
                ", visibleStartTimestamp=" + visibleStartTimestamp +
                ", visibleEndTimestamp=" + visibleEndTimestamp +
                '}';
    }
}

