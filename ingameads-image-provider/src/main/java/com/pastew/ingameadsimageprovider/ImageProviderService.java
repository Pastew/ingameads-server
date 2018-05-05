package com.pastew.ingameadsimageprovider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@Slf4j
public class ImageProviderService {
    private final ImageProviderRepository imageProviderRepository;

    @Autowired
    public ImageProviderService(ImageProviderRepository imageProviderRepository) {
        this.imageProviderRepository = imageProviderRepository;
    }

    public Advert findAdvertByGameId(String gameId) {
        return imageProviderRepository.findByGameId(gameId);
    }

    public String getCurrentAdvertImageURL(String gameId) {
        //Advert currentAdvert = findAdvertByGameId(gameId);
        //TODO: Zweryfikuj czy ta reklama jest aktualna. Może nawet zrób to od razu w SQL.
        //return currentAdvert.getImageURL();
        return "https://i2.wp.com/beebom.com/wp-content/uploads/2016/01/Reverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg";
    }
}
