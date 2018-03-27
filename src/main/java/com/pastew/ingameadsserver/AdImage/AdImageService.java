package com.pastew.ingameadsserver.AdImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public AdImageService(AdImageRepository repository, ResourceLoader resourceLoader){
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    public Page<AdImage> findPage(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String filename){
        return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
    }

    public void createImage(MultipartFile file) throws IOException {

        if ("".equals(file.getOriginalFilename()))
            throw new IOException("Filename is empty");

        if (!file.isEmpty()){
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new AdImage(file.getOriginalFilename()));
        }
    }

    public void deleteImage(String filename) throws IOException {
        final AdImage byName = repository.findByName(filename);
        repository.delete(byName);

        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
    }

    @Bean
    //@Profile("dev")
    CommandLineRunner setUp(AdImageRepository repository) throws IOException {

        return args -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test1"));
            repository.save(new AdImage("test1"));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new AdImage("test2"));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test3"));
            repository.save(new AdImage("test3"));
        };

    }
}
