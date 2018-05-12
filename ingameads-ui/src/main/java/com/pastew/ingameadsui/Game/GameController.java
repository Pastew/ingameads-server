package com.pastew.ingameadsui.Game;

import com.pastew.ingameadsui.Advert.Advert;
import com.pastew.ingameadsui.Advert.AdvertOffer;
import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import com.pastew.ingameadsui.Image.ImageService;
import com.pastew.ingameadsui.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class GameController {

    private final GameService gameService;
    private ImageService imageService;
    private UserRepository userRepository;

    @Autowired
    public GameController(GameService gameService, ImageService imageService, UserRepository userRepository) {
        this.gameService = gameService;
        this.imageService = imageService;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/games/new")
    public void addGame(@RequestBody Game game) {
        gameService.addGame(game);
    }

    @RequestMapping(value = "/games")
    public String index(Model model, Pageable pageable) {
        final Page<Game> page = gameService.findPage(pageable);
        model.addAttribute("page", page);
        if (page.hasPrevious())
            model.addAttribute("prev", page.previousPageable());

        if (page.hasNext())
            model.addAttribute("next", page.nextPageable());

        return "games";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/games/{gameId}")
    public String getGame(Model model, @PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        Advert[] adverts = gameService.getGameAdverts(gameId);
        model.addAttribute("game", game);
        model.addAttribute("adverts", adverts);
        return "game";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mygames/{id}")
    public String getMyGame(Model model, @PathVariable String id) {
        Game game = gameService.getGame(id);
        model.addAttribute("game", game);
        return "mygame";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mygames")
    public String getMyGames(Model model) {
        List<Game> currentGameDeveloperGames = gameService.getCurrentGameDeveloperGames();
        model.addAttribute("mygames", currentGameDeveloperGames);

        return "mygames";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/images")
    public String uploadImageForGame(@RequestParam("file") MultipartFile file,
                                     @PathVariable String gameId,
                                     RedirectAttributes redirectAttributes) {

        try {
            gameService.addImageToGame(file, gameId);
            redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
        }

        return "redirect:/mygames";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/buyAdvert")
    public String buyAdvert(@RequestParam("file") MultipartFile file,
                            @PathVariable String gameId,
                            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                            RedirectAttributes redirectAttributes) {

        try {
            String imageUrl = gameService.uploadImage(file);
            Advert advert = new Advert();
            advert.setGame(gameService.getGame(gameId));
            advert.setImageURL(imageUrl);
            advert.setStartDate(startDate.getTime()/1000);
            advert.setEndDate(endDate.getTime()/1000 );

            AdvertOffer advertOffer = new AdvertOffer();
            advertOffer.setAdvert(advert);
            gameService.buyAdvert(advertOffer);
            redirectAttributes.addFlashAttribute("flash.message", "Successfully bought advert!");
        } catch (AdvertBuyException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Can't buy advert: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message", "Failed to upload image " + e.getMessage());
        }

        return "redirect:/games/" + gameId;
    }
}
