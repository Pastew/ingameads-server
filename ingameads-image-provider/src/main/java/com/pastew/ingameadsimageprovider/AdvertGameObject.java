package com.pastew.ingameadsimageprovider;

public class AdvertGameObject {

    public String imageUrl;
    public long id;

    public AdvertGameObject(Advert advert) {
        id = advert.getId();
        imageUrl = advert.getImageURL();
    }

    public AdvertGameObject(String imageUrl, long id) {
        this.imageUrl = imageUrl;
        this.id = id;
    }
}
