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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "ingameads",
            "api_key", "726636437293547",
            "api_secret", "cGwYcd8ef5b3xN08im8JmM_I75o",
            "proxy", "http://10.158.100.2:8080"));

    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, UserRepository userRepository,
                        GameRepository gameRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Image> findPage(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public Image createImage(MultipartFile file) throws IOException {

        if ("".equals(file.getOriginalFilename()))
            throw new IOException("Filename is empty");

        if (file.isEmpty())
            throw new IOException("File is empty");

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

    public List<Image> getCurrentUserImages() {
        User owner = userService.getLoggedUser();
        return imageRepository.findByOwner(owner);
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageRepository imagerepository,
                            UserRepository userRepository,
                            GameRepository gameRepository,
                            AdvertOfferRepository advertOfferRepository,
                            AdvertOfferService advertOfferService) throws IOException {

        return args -> {
            User greg = userRepository.save(new User(Dev.GREG,  passwordEncoder.encode(Dev.userPassword), "greg@qwe.com", "ROLE_USER"));
            User bob = userRepository.save(new User(Dev.BOB,  passwordEncoder.encode(Dev.userPassword), "bob@qwe.com", "ROLE_USER"));


            Image[] images = {
                    new Image(bob, "http://www.res.cloudinary.com/ingameads/image/upload/v1526309281/watch_dogs_2.png"),
                    new Image(bob, "http://www.res.cloudinary.com/ingameads/image/upload/v1526309280/watch_dogs.png"),
                    new Image(bob, "http://www.res.cloudinary.com/ingameads/image/upload/v1526309281/sonic.jpg"),
                    new Image(greg, "http://www.res.cloudinary.com/ingameads/image/upload/v1526309278/spiderman.jpg"),
                    new Image(greg, "http://res.cloudinary.com/ingameads/image/upload/v1526325835/gta5_0.jpg"),
                    new Image(greg, "http://www.res.cloudinary.com/ingameads/image/upload/v1526309280/gta.jpg"),
                    new Image(greg, "http://res.cloudinary.com/ingameads/image/upload/v1526325836/gta5_2.png"),
                    new Image(greg, "http://res.cloudinary.com/ingameads/image/upload/v1526329519/ingameads.png"),
            };

            for (Image image : images) imagerepository.save(image);

            Game gta5 = gameRepository.save(new Game("com.rockstar.gta5", "Grand Theft Auto 5", 50, "Piąta, pełnoprawna odsłona niezwykle popularnej serii gier akcji, nad której rozwojem pieczę sprawuje studio Rockstar North we współpracy z koncernem Take Two Interactive. Miejscem akcji Grand Theft Auto V jest fikcyjne miasto Los Santos (wzorowane na Los Angeles), a fabuła koncentruje się na perypetiach trójki bohaterów: Michaela De Santy, Trevora Philipsa i Franklina Clintona, którym nieobce są zatargi z prawem. Twórcy gry pozostali wierni sandboksowemu modelowi rozgrywki, pozwalając graczom na dużą swobodę w wykonywaniu zadań i poruszaniu się po wirtualnym mieście. Koszty produkcji i promocji tytułu oszacowane zostały na ponad 360 milionów dolarów, co pobiło wszystkie wcześniejsze rekordy w branży gier wideo.", bob, Arrays.asList(images[4], images[5], images[6])));
            Game watchDogs = gameRepository.save(new Game("com.ubisoft.watch_dogs", "Watch Dogs", 10, "Przygodowa gra akcji z widokiem z perspektywy trzeciej osoby (TPP), za której powstanie odpowiadają studia deweloperskie koncernu Ubisoft na czele z Ubisoft Montreal. Fabuła Watch Dogs przenosi graczy w niedaleką przyszłość do Chicago, jednego z wielu amerykańskich miast, których infrastrukturą zarządza Centralny System Operacyjny (CtOS). Główny bohater to Aiden Pearce, utalentowany haker potrafiący włamać się niemal do każdego urządzenia elektronicznego i wykorzystać jego możliwości, by sprokurować rozwój wypadków lub wpłynąć na zachowanie innych ludzi. Rozgrywka toczy się w otwartym świecie, a gracz ma dużą swobodę w wykonywaniu zadań", bob, Arrays.asList(images[0], images[1])));
            Game sonic = gameRepository.save(new Game("com.sega.sonic", "Sonic", 15, "Sonic Generations to zręcznościowa platformówka powstała z myślą o uczczeniu 20. urodzin tytułowego niebieskiego jeża. W grze autorstwa ekipy Sonic Team występują dwa wcielenia Sonika - klasyczny i nowoczesny, które starają się zlikwidować dziury w czasoprzestrzeni. W grze pojawia się wiele lokacji stanowiących nawiązanie do poziomów, znanych fanom wcześniejszych odsłon cyklu", greg, Arrays.asList(images[2])));
            Game spiderman = gameRepository.save(new Game("com.activision.spiderman", "Spiderman", 10, "Przygodowa gra akcji ze słynnym superbohaterem w roli głównej. Rozgrywka polega oczywiście na walczeniu z przestępcami. Za produkcję odpowiada słynne studio Insomniac Games, znane m.in. z serii Ratchet & Clank, Resistance czy Sunset Overdrive.", greg, Arrays.asList(images[3])));
            Game ingameadsClient = gameRepository.save(new Game("com.pastew.ingameads_client", "InGameAds - przykładowa gra", 2, "Przykładowa gra wizualizująca działanie sytemu. https://github.com/Pastew/ingameads-client", bob, Arrays.asList(images[7])));

            Advert advert = new Advert();
            advert.setStartDate(1529056800); // 15 June 2018
            advert.setEndDate(1529316000); // 18 June 2018
            advert.setImageURL("http://www.colouringinteam.co.uk/portfolio/storage/cache/images/000/182/FRijj,large.1433757631.jpg");
            advert.setGame(gta5);

            AdvertOffer advertOffer = new AdvertOffer();
            advertOffer.setBuyer(greg);
            advertOffer.setState(AdvertOfferStates.WAITING_FOR_GAME_OWNER_ACCEPTANCE);
            advertOffer.setAdvert(advert);
            advertOffer.setGameOwner(advert.getGame().getOwner());
            advertOfferRepository.save(advertOffer);

            Advert advert2 = new Advert();
            advert2.setStartDate(1529056800); // 15 June 2018
            advert2.setEndDate(1529316000); // 18 June 2018
            advert2.setImageURL("https://orig00.deviantart.net/8854/f/2009/205/c/0/starbucks_advert_by_vinayizblank.jpg");
            advert2.setGame(gta5);

            AdvertOffer advertOffer2 = new AdvertOffer();
            advertOffer2.setBuyer(greg);
            advertOffer2.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
            advertOffer2.setAdvert(advert2);
            advertOffer2.setGameOwner(advert2.getGame().getOwner());
            advertOfferRepository.save(advertOffer2);

            Advert advert3 = new Advert();
            advert3.setStartDate(1526242800); // 12 may 2018
            advert3.setEndDate(1529316000); // 18 June 2018
            advert3.setImageURL("https://orig00.deviantart.net/8854/f/2009/205/c/0/starbucks_advert_by_vinayizblank.jpg");
            advert3.setGame(ingameadsClient);

            AdvertOffer advertOffer3 = new AdvertOffer();
            advertOffer3.setBuyer(greg);
            advertOffer3.setState(AdvertOfferStates.ACCEPTED_AND_WAITING_FOR_PAYMENT);
            advertOffer3.setAdvert(advert3);
            advertOffer3.setGameOwner(advert3.getGame().getOwner());
            advertOfferRepository.save(advertOffer3);

            //advertOfferService.notifyImageProviderAboutNewAdvert(advertOffer3.getId());
        };
    }
}
