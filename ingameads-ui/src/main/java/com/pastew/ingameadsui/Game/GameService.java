package com.pastew.ingameadsui.Game;

import com.cloudinary.utils.ObjectUtils;
import com.pastew.ingameadsui.Advert.Advert;
import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import com.pastew.ingameadsui.Image.Image;
import com.pastew.ingameadsui.Image.ImageService;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RestTemplate restTemplate;

    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "ingameads",
            "api_key", "726636437293547",
            "api_secret", "cGwYcd8ef5b3xN08im8JmM_I75o"));

    public void addGame(Game game) {
        game.setOwner(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        gameRepository.save(game);
    }

    public Page<Game> findPage(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    public Game getGame(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> getCurrentGameDeveloperGames() {
        String ownerName = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(ownerName);
        return gameRepository.findByOwnerId(owner.getId());
    }

    public void addImageToGame(MultipartFile file, String gameId) throws IOException {
        Image image = imageService.createImage(file);
        Game game = getGame(gameId);
        game.getImages().add(image);
        gameRepository.save(game);
    }

    public void buyAdvert(Advert ad) throws AdvertBuyException {
        Advert[] adverts = getGameAdverts(ad.getGameId());

        for (Advert a : adverts) {
            if (twoDatesOverlap(a.getStartDate(), a.getEndDate(),
                    ad.getStartDate(),
                    ad.getEndDate()))
                throw new AdvertBuyException("Choose another dates!");
        }

        HttpEntity<Advert> request = new HttpEntity<>(ad);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://ingameads-image-provider/" + ad.getGameId() + "/saveAdvert/",
                request, String.class);

        if(!response.getStatusCode().is2xxSuccessful())
            throw new AdvertBuyException("Failed to buy advert");
    }

    public Advert[] getGameAdverts(String gameId) {
        return restTemplate.getForObject("http://ingameads-image-provider/allAdverts/" + gameId, Advert[].class);
    }

    private boolean twoDatesOverlap(long start1, long end1, long start2, long end2) {
        return start1 <= end2 && start2 <= end1;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }
}
