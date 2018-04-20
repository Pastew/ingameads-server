package com.pastew.ingameadsserver.AdImage;

import com.pastew.ingameadsserver.User.User;
import com.pastew.ingameadsserver.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@Service
public class AdImageService {

    private static String UPLOAD_ROOT = "adimages";
    private final AdImageRepository repository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;

    @Autowired
    public AdImageService(AdImageRepository adImageRepository, ResourceLoader resourceLoader, UserRepository userRepository) {
        this.repository = adImageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
    }

    public Page<AdImage> findPage(Pageable pageable) {
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
            repository.save(new AdImage(file.getOriginalFilename(),
                    userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
        }
    }

    public void deleteImage(String filename) throws IOException {
        final AdImage byName = repository.findByName(filename);
        repository.delete(byName);

        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
        //@Profile("dev")
    CommandLineRunner setUp(AdImageRepository adImagerepository,
                            UserRepository userRepository) throws IOException {

        return args -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User greg = userRepository.save(new User("greg", "qwe", "ROLE_ADMIN", "ROLE_USER"));
            User rob = userRepository.save(new User("rob", "qwe", "ROLE_USER"));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test1"));
            adImagerepository.save(new AdImage("test1", greg));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test2"));
            adImagerepository.save(new AdImage("test2", greg));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test3"));
            adImagerepository.save(new AdImage("test3", rob));

        };

    }
}
