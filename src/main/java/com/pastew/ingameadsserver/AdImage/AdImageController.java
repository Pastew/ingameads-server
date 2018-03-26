package com.pastew.ingameadsserver.AdImage;

import com.pastew.ingameadsserver.AdImage.AdImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class AdImageController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final AdImageService adImageService;

    @Autowired
    public AdImageController(AdImageService adImageService) {
        this.adImageService = adImageService;
    }


    @RequestMapping(value = "/")
    public String index (Model model, Pageable pageable){
        final Page<AdImage> page = adImageService.findPage(pageable);
        model.addAttribute("page", page);
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String filename) {
        Resource file = adImageService.findOneImage(filename);
        try {
            return ResponseEntity
                    .ok()
                    .contentLength(file.contentLength())
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Couldn't find " + filename + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
    @ResponseBody
    public ResponseEntity<?> createFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            adImageService.createImage(file);
            final URI locationUri = new URI(request.getRequestURL().toString() + "/").resolve(file.getOriginalFilename() + "/raw");

            return ResponseEntity
                    .created(locationUri)
                    .body("Successfully upload " + file.getOriginalFilename());
        } catch (IOException | URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable String filename){

        try {
            adImageService.deleteImage(filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Succesfully delete " + filename);
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Coundn't delete " + filename + " => " + e.getMessage());
        }
    }
}