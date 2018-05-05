package com.pastew.ingameadsui.Image;

import com.pastew.ingameadsui.Game.Game;
import com.pastew.ingameadsui.Game.GameRepository;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserRepository;
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

import static com.pastew.ingameadsui.Roles.GAME_DEV;

@Service
@Slf4j
public class ImageService {

    private static String UPLOAD_ROOT = "images";
    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, UserRepository userRepository, GameRepository gameRepository) {
        this.repository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public Page<Image> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    public Image createImage(MultipartFile file) throws IOException {

        if ("".equals(file.getOriginalFilename()))
            throw new IOException("Filename is empty");

        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            return repository.save(new Image(file.getOriginalFilename(),
                    userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        }

        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(@Param("filename") String filename) throws IOException {
        final Image image = repository.findByName(filename);
        Game game = gameRepository.findByImages(image);
        game.getImages().remove(image);
        gameRepository.save(game);

        repository.delete(image);

        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageRepository imagerepository,
                            UserRepository userRepository,
                            GameRepository gameRepository) throws IOException {

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

            User greg = userRepository.save(new User(Dev.GREG, Dev.userPassword, "ROLE_ADMIN", "ROLE_USER"));
            User bob = userRepository.save(new User(Dev.BOB, Dev.userPassword, "ROLE_USER", "ROLE_"+GAME_DEV));

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
            gameRepository.save(new Game("com.pastew.game1", greg, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[0], images[1])));
            gameRepository.save(new Game("com.pastew.game2", greg, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[2])));
            gameRepository.save(new Game("com.pastew.game3", bob, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[3])));
            gameRepository.save(new Game("com.pastew.game4", bob, l.getTitle(1, 4), l.getWords(70, 120), null));
            gameRepository.save(new Game("com.pastew.game5", bob, l.getTitle(1, 4), l.getWords(70, 120), Arrays.asList(images[4])));
        };
    }
}
