package com.pastew.ingameadsui.Image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.pastew.ingameadsui.Advert.*;
import com.pastew.ingameadsui.Game.Game;
import com.pastew.ingameadsui.Game.GameRepository;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserRepository;
import com.pastew.ingameadsui.User.UserService;
import com.pastew.ingameadsui.dev.Dev;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "images";
    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final UserService userService;

    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "ingameads",
            "api_key", "726636437293547",
            "api_secret", "cGwYcd8ef5b3xN08im8JmM_I75o"));

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, UserRepository userRepository, GameRepository gameRepository, UserService userService) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
    }

    public Page<Image> findPage(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    public Image createImage(MultipartFile file) throws IOException {

        if ("".equals(file.getOriginalFilename()))
            throw new IOException("Filename is empty");

        if (file.isEmpty())
            throw new IOException("File is empty");

        Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String url = (String) uploadResult.get("url");

        User currentUser = userService.getLoggedUser();
        Image image = new Image(currentUser, url);
        return imageRepository.save(image);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@Param("filename") String url) throws IOException {
        final Image image = imageRepository.findByUrl(url);
        Game game = gameRepository.findByImages(image);
        game.getImages().remove(image);
        gameRepository.save(game);
        imageRepository.delete(image);
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageRepository imagerepository,
                            UserRepository userRepository,
                            GameRepository gameRepository,
                            AdvertOfferRepository advertOfferRepository,
                            AdvertOfferService advertOfferService) throws IOException {

        return args -> {
            User greg = userRepository.save(new User(Dev.GREG, Dev.userPassword, "ROLE_USER"));
            User bob = userRepository.save(new User(Dev.BOB, Dev.userPassword, "ROLE_USER"));


            Image[] images = {
                    new Image(bob,"http://www.res.cloudinary.com/ingameads/image/upload/v1526309281/watch_dogs_2.png"),
                    new Image(bob,"http://www.res.cloudinary.com/ingameads/image/upload/v1526309280/watch_dogs.png"),
                    new Image(bob,"http://www.res.cloudinary.com/ingameads/image/upload/v1526309281/sonic.jpg"),
                    new Image(greg,"http://www.res.cloudinary.com/ingameads/image/upload/v1526309278/spiderman.jpg"),
                    new Image(greg,"http://res.cloudinary.com/ingameads/image/upload/v1526325835/gta5_0.jpg"),
                    new Image(greg,"http://www.res.cloudinary.com/ingameads/image/upload/v1526309280/gta.jpg"),
                    new Image(greg,"http://res.cloudinary.com/ingameads/image/upload/v1526325836/gta5_2.png"),
                    new Image(greg,"http://res.cloudinary.com/ingameads/image/upload/v1526329519/ingameads.png"),
            };

            for (Image image : images) imagerepository.save(image);

            Lorem l = LoremIpsum.getInstance();
            Game gta5 = gameRepository.save(new Game("com.rockstar.gta5", l.getTitle(1, 4), 1, l.getWords(70, 120), greg, Arrays.asList(images[4], images[5], images[6])));
            Game watchDogs = gameRepository.save(new Game("com.ubisoft.watch_dogs", l.getTitle(1, 4), 1, l.getWords(70, 120), bob, Arrays.asList(images[0], images[1])));
            Game sonic = gameRepository.save(new Game("com.sega.sonic", l.getTitle(1, 4), 1, l.getWords(70, 120), bob, Arrays.asList(images[2])));
            Game spiderman = gameRepository.save(new Game("com.activision.spiderman", l.getTitle(1, 4), 1, l.getWords(70, 120), greg, Arrays.asList(images[3])));
            Game ingameadsClient = gameRepository.save(new Game("com.pastew.ingameads_client", l.getTitle(1, 4), 1, l.getWords(70, 120), greg, Arrays.asList(images[7])));

            Advert advert = new Advert();
            advert.setStartDate(1529056800); // 15 June 2018
            advert.setEndDate(1529316000); // 18 June 2018
            advert.setImageURL("http://www.colouringinteam.co.uk/portfolio/storage/cache/images/000/182/FRijj,large.1433757631.jpg");
            advert.setGame(gta5);

            AdvertOffer advertOffer = new AdvertOffer();
            advertOffer.setBuyer(bob);
            advertOffer.setState(AdvertOfferStates.WAITING_FOR_GAME_OWNER_ACCEPTANCE);
            advertOffer.setAdvert(advert);
            advertOffer.setGameOwner(advert.getGame().getOwner());
            advertOfferRepository.save(advertOffer);

            Advert advert2 = new Advert();
            advert2.setStartDate(1529056800); // 15 June 2018
            advert2.setEndDate(1529316000); // 18 June 2018
            advert2.setImageURL("https://cdn-a.william-reed.com/var/wrbm_gb_food_pharma/storage/images/publications/food-beverage-nutrition/foodnavigator.com/article/2017/01/11/ferrero-defends-palm-oil-in-nutella-with-advert-against-unfair-smear-campaign/1179591-6-eng-GB/Ferrero-defends-palm-oil-in-Nutella-with-advert-against-unfair-smear-campaign_wrbm_large.jpg");
            advert2.setGame(gta5);

            AdvertOffer advertOffer2 = new AdvertOffer();
            advertOffer2.setBuyer(bob);
            advertOffer2.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
            advertOffer2.setAdvert(advert2);
            advertOffer2.setGameOwner(advert2.getGame().getOwner());
            advertOfferRepository.save(advertOffer2);

            Advert advert3 = new Advert();
            advert3.setStartDate(1526242800); // 12 may 2018
            advert3.setEndDate(1529316000); // 18 June 2018
            advert3.setImageURL("https://cdn-a.william-reed.com/var/wrbm_gb_food_pharma/storage/images/publications/food-beverage-nutrition/foodnavigator.com/article/2017/01/11/ferrero-defends-palm-oil-in-nutella-with-advert-against-unfair-smear-campaign/1179591-6-eng-GB/Ferrero-defends-palm-oil-in-Nutella-with-advert-against-unfair-smear-campaign_wrbm_large.jpg");
            advert3.setGame(ingameadsClient);

            AdvertOffer advertOffer3 = new AdvertOffer();
            advertOffer3.setBuyer(bob);
            advertOffer3.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
            advertOffer3.setAdvert(advert3);
            advertOffer3.setGameOwner(advert3.getGame().getOwner());
            advertOfferRepository.save(advertOffer3);

            advertOfferService.payForAdvertOffer(advertOffer3.getId());
        };
    }

    public List<Image> getCurrentUserImages() {
        User owner = userService.getLoggedUser();
        return imageRepository.findByOwner(owner);
    }
}
