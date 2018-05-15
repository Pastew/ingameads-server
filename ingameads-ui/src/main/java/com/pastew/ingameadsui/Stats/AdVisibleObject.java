package com.pastew.ingameadsui.Stats;

import lombok.Data;

@Data
public class AdVisibleObject {
    public long advertId;
    public String gameObjectName;
    public long visibleStartTimestamp;
    public long visibleEndTimestamp;
    public String advertURL;

    // in seconds
    public long getViewTime() {
        return (visibleEndTimestamp - visibleStartTimestamp) ;
    }
}
