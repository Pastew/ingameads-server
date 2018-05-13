package com.pastew.ingameadsui.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ImageController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @RequestMapping(value = "/images")
    public String getMyImages (Model model){
        List<Image> currentUserImages = imageService.getCurrentUserImages();
        model.addAttribute("currentUserImages", currentUserImages);
        return "images";
    }

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
    @ResponseBody
    public ResponseEntity<?> oneRawImage(@PathVariable String filename) {
        Resource file = imageService.findOneImage(filename);
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
    public String createFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            imageService.createImage(file);
            redirectAttributes.addFlashAttribute("flash.message", "Zdjęcie wysłano " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Nie udało się wysłać zdjęcia " + file.getOriginalFilename() + " => " + e.getMessage());
        }

        return "redirect:/images";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    public String deleteFile(@PathVariable String filename, RedirectAttributes redirectAttributes){

        try {
            imageService.deleteImage(filename);
            redirectAttributes.addFlashAttribute("flash.message", "Usunięto zdjęcie " + filename);
        } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Nie udało się usunąć zdjęcia " + filename + " => " + e.getMessage());
        }
        return "redirect:/images";
    }
}
