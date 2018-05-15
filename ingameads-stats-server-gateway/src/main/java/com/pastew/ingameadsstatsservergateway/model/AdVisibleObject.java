package com.pastew.ingameadsstatsservergateway.model;

import lombok.Data;

@Data
public class AdVisibleObject {
    public long advertId;
    public String gameObjectName;
    public int visibleStartTimestamp;
    public int visibleEndTimestamp;
    public String advertURL;
}