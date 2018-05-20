package com.pastew.ingameadsui.Game;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pastew.ingameadsui.Advert.Advert;
import com.pastew.ingameadsui.Advert.AdvertOffer;
import com.pastew.ingameadsui.Advert.AdvertOfferService;
import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import com.pastew.ingameadsui.Image.Image;
import com.pastew.ingameadsui.Image.ImageService;
import com.pastew.ingameadsui.Stats.AdVisibleObject;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertOfferService advertOfferService;

    @Autowired
    private RestTemplate restTemplate;

    public void addGame(Game game) {
        game.setOwner(userService.getLoggedUser());
        gameRepository.save(game);
    }

    public Page<Game> findPage(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    public Game getGame(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> getCurrentGameDeveloperGames() {
        User owner = userService.getLoggedUser();
        return gameRepository.findByOwnerId(owner.getId());
    }

    public void addImageToGame(MultipartFile file, String gameId) throws IOException {
        Image image = imageService.createImage(file);
        Game game = getGame(gameId);
        game.getImages().add(image);
        gameRepository.save(game);
    }

    public void submitAdvertOffer(AdvertOffer advertOffer) throws AdvertBuyException {
        User loggedUser = userService.getLoggedUser();
        verifyIfUserIsLogged(loggedUser);
        advertOffer.setBuyer(loggedUser);
        advertOffer.setGameOwner(advertOffer.getAdvert().getGame().getOwner());
        verifyIfTimeSlotIsAvailable(advertOffer);

        advertOfferService.addAdvertOffer(advertOffer);
    }

    private void verifyIfUserIsLogged(User loggedUser) throws AdvertBuyException {
        if (null == loggedUser)
            throw new AdvertBuyException("You have to login first!");
    }

    private void verifyIfTimeSlotIsAvailable(AdvertOffer advertOffer) throws AdvertBuyException {
        Advert ad = advertOffer.getAdvert();

        Advert[] adverts = getGameAdverts(ad.getGame().getId());

        for (Advert a : adverts) {
            if (twoDatesOverlap(a.getStartDate(), a.getEndDate(),
                    ad.getStartDate(),
                    ad.getEndDate()))
                throw new AdvertBuyException("Ten termin jest zajęty, wybierz inną datę.");
        }
    }

    public Advert[] getGameAdverts(String gameId) {
        return restTemplate.getForObject("http://ingameads-image-provider/allAdverts/" + gameId, Advert[].class);
    }

    private boolean twoDatesOverlap(long start1, long end1, long start2, long end2) {
        return start1 <= end2 && start2 <= end1;
    }

    public AdVisibleObject[] getAdVisibleObjects(String gameId) {
        return restTemplate.getForObject("http://ingameads-stats-server-gateway/adVisibleObjects/" + gameId, AdVisibleObject[].class);
    }
}
