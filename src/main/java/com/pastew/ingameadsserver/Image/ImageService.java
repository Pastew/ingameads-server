package com.pastew.ingameadsserver.Image;

import com.pastew.ingameadsserver.Game.Game;
import com.pastew.ingameadsserver.Game.GameRepository;
import com.pastew.ingameadsserver.User.User;
import com.pastew.ingameadsserver.User.UserRepository;
import com.pastew.ingameadsserver.dev.Dev;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class ImageService {

    private static String UPLOAD_ROOT = "images";
    private final ImageRepository repository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader, UserRepository userRepository) {
        this.repository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
    }

    public Page<Image> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String filename) {
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    public void createImage(MultipartFile file) throws IOException {

        if ("".equals(file.getOriginalFilename()))
            throw new IOException("Filename is empty");

        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image(file.getOriginalFilename(),
                    userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        }
    }

    @PreAuthorize("@ImageRepository.findByName(#filename)?.owner?.username == authentication?.name or hasRole('ADMIN')")
    public void deleteImage(@Param("filename") String filename) throws IOException {
        final Image byName = repository.findByName(filename);
        repository.delete(byName);

        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(ImageRepository imagerepository,
                            UserRepository userRepository,
                            GameRepository gameRepository) throws IOException {

        return args -> {
            //FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            //Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User greg = userRepository.save(new User(Dev.GREG, Dev.userPassword, "ROLE_ADMIN", "ROLE_USER"));
            User bob = userRepository.save(new User(Dev.BOB, Dev.userPassword, "ROLE_USER", "ROLE_GAME_DEVELOPER"));

            Image[] images = {
                    new Image("test1.PNG", greg),
                    new Image("test2.PNG", greg),
                    new Image("test3.PNG", greg),
                    new Image("test4.PNG", bob),
                    new Image("test5.PNG", bob)
            };

            for (int i = 0 ; i < 5; ++i) {
                imagerepository.save(images[i]);
            }

            gameRepository.save(new Game("com.pastew.game1", greg, "Game 1 title", "Description of game 1", Arrays.asList(images[0], images[1])));
            gameRepository.save(new Game("com.pastew.game2", greg, "Game 2 title", "Description of game 2", Arrays.asList(images[2])));
            gameRepository.save(new Game("com.pastew.game3", bob, "Game 3 title", "Description of game 3", Arrays.asList(images[3])));
            gameRepository.save(new Game("com.pastew.game4", bob, "Game 4 title", "Description of game 4", null));
            gameRepository.save(new Game("com.pastew.game5", bob, "Game 5 title", "Description of game 5", Arrays.asList(images[4])));
        };
    }
}
