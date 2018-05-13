package com.pastew.ingameadsui.Image;

import com.pastew.ingameadsui.Advert.Advert;
import com.pastew.ingameadsui.Advert.AdvertOffer;
import com.pastew.ingameadsui.Advert.AdvertOfferRepository;
import com.pastew.ingameadsui.Advert.AdvertOfferStates;
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
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "images";
    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final UserService userService;

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

        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            return imageRepository.save(new Image(file.getOriginalFilename(),
                    userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        }

        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@Param("filename") String filename) throws IOException {
        final Image image = imageRepository.findByName(filename);
        Game game = gameRepository.findByImages(image);
        game.getImages().remove(image);
        gameRepository.save(game);

        imageRepository.delete(image);

        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageRepository imagerepository,
                            UserRepository userRepository,
                            GameRepository gameRepository,
                            AdvertOfferRepository advertOfferRepository) throws IOException {

        return args -> {
            File folder = new File(UPLOAD_ROOT);
            try {
                for (File file : folder.listFiles()) {
                    if (!file.getName().startsWith("test")) {
                        file.delete();
                    }
                }
            } catch( NullPointerException e){
                log.error("Can't find any image in UPLOAD_ROOT: " + folder.getAbsolutePath());
                System.exit(1);
            }

            //Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User greg = userRepository.save(new User(Dev.GREG, Dev.userPassword, "ROLE_USER"));
            User bob = userRepository.save(new User(Dev.BOB, Dev.userPassword, "ROLE_USER"));

            Image[] images = {
                    new Image("test1.PNG", greg),
                    new Image("test2.PNG", greg),
                    new Image("test3.PNG", greg),
                    new Image("test4.PNG", bob),
                    new Image("test5.PNG", bob)
            };

            for (int i = 0; i < 5; ++i) {
                imagerepository.save(images[i]);
            }
            Lorem l = LoremIpsum.getInstance();
            gameRepository.save(new Game("com.pastew.example_game_1", greg, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[0], images[1])));
            gameRepository.save(new Game("com.pastew.example_game_2", greg, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[2])));
            gameRepository.save(new Game("com.pastew.example_game_3", bob, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[3])));
            gameRepository.save(new Game("com.pastew.example_game_4", bob, l.getTitle(1, 4), l.getWords(70, 120), null));
            gameRepository.save(new Game("com.pastew.example_game_5", bob, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[4])));

            Advert advert = new Advert();
            advert.setStartDate(1529056800); // 15 June 2018
            advert.setEndDate(1529316000); // 18 June 2018
            advert.setImageURL("http://www.colouringinteam.co.uk/portfolio/storage/cache/images/000/182/FRijj,large.1433757631.jpg");
            advert.setGame(gameRepository.findById("com.pastew.example_game_1").get());

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
            advert2.setGame(gameRepository.findById("com.pastew.example_game_2").get());

            AdvertOffer advertOffer2 = new AdvertOffer();
            advertOffer2.setBuyer(bob);
            advertOffer2.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
            advertOffer2.setAdvert(advert2);
            advertOffer2.setGameOwner(advert2.getGame().getOwner());
            advertOfferRepository.save(advertOffer2);
        };
    }

    public List<Image> getCurrentUserImages() {
        User owner = userService.getLoggedUser();
        return imageRepository.findByOwner(owner);
    }
}
