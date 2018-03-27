package com.pastew.ingameadsserver.AdImage;

import com.pastew.ingameadsserver.AdImage.AdImageService;
import com.sun.net.httpserver.Authenticator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if(page.hasPrevious())
            model.addAttribute("prev", page.previousPageable());

        if(page.hasNext())
            model.addAttribute("next", page.nextPageable());

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
    public String createFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            adImageService.createImage(file);
            redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }

        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
    public String deleteFile(@PathVariable String filename, RedirectAttributes redirectAttributes){

        try {
            adImageService.deleteImage(filename);
            redirectAttributes.addFlashAttribute("flash.message", "Sucessfully deleted image " + filename);
        } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Failed to delete image " + filename + " => " + e.getMessage());
        }
        return "redirect:/";
    }
}
