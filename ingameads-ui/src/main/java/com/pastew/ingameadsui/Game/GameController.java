package com.pastew.ingameadsui.Game;

import com.pastew.ingameadsui.Advert.Advert;
import com.pastew.ingameadsui.Advert.AdvertOffer;
import com.pastew.ingameadsui.Advert.AdvertOfferStates;
import com.pastew.ingameadsui.Exceptions.AdvertBuyException;
import com.pastew.ingameadsui.Image.Image;
import com.pastew.ingameadsui.Image.ImageService;
import com.pastew.ingameadsui.Stats.AdVisibleObject;
import com.pastew.ingameadsui.User.UserRepository;
import org.cloudinary.json.JSONObject;
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
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

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

        AdVisibleObject[] adVisibleObjects = gameService.getAdVisibleObjects(gameId);
        if (adVisibleObjects.length > 0) {
            //model.addAttribute("adVisibleObjects", adVisibleObjects);
            model.addAttribute("views", getTotalViews(adVisibleObjects));
            model.addAttribute("averageAdViewTime", getAverageAdViewTime(adVisibleObjects));
            model.addAttribute("medianAdViewTime", getMedianAdViewTime(adVisibleObjects));
            model.addAttribute("viewsPerHour", getViewsPerHour(adVisibleObjects));
        }
        return "game";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/mygames/{gameId}")
    public String getMyGame(Model model, @PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        model.addAttribute("game", game);
        return "mygame";
    }

    private String getViewsPerHour(AdVisibleObject[] adVisibleObjects) {
        int[] hoursMap = new int[24];

        for (AdVisibleObject adVisibleObject : adVisibleObjects) {
            int hour = Instant.ofEpochSecond(adVisibleObject.getVisibleStartTimestamp())
                    .atOffset(ZoneOffset.UTC).toLocalTime().getHour();

            ++hoursMap[hour];
        }

        StringBuilder hoursMapJson = new StringBuilder("[");
        for (int i = 0; i < hoursMap.length; ++i) {
            //hoursMapJson.append(String.format("{\"x\":\"%s\", \"y\":\"%s\"},", i, hoursMap[i]));
            hoursMapJson.append(String.format("%s,", hoursMap[i]));
        }
        hoursMapJson.setLength(hoursMapJson.length() - 1); // To remove last ,
        hoursMapJson.append("]");

        return hoursMapJson.toString();
    }

    private double getMedianAdViewTime(AdVisibleObject[] adVisibleObjects) {
        long[] timeViews = new long[adVisibleObjects.length];

        for (int i = 0; i < adVisibleObjects.length; ++i)
            timeViews[i] = adVisibleObjects[i].getViewTime();

        Arrays.sort(timeViews);
        if (timeViews.length % 2 == 0)
            return ((double) timeViews[timeViews.length / 2] + (double) timeViews[timeViews.length / 2 - 1]) / 2;
        else
            return (double) timeViews[timeViews.length / 2];
    }

    private double getAverageAdViewTime(AdVisibleObject[] adVisibleObjects) {
        double sum = 0;
        for (int i = 0; i < adVisibleObjects.length; ++i)
            sum += adVisibleObjects[i].getViewTime();

        return sum / adVisibleObjects.length;
    }

    private int getTotalViews(AdVisibleObject[] adVisibleObjects) {
        return adVisibleObjects.length;
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
            redirectAttributes.addFlashAttribute("flash.message", "Udało się wysłać zdjęcie " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Nie udało się wysłać zdjęcia " + file.getOriginalFilename() + " => " + e.getMessage());
        }

        return "redirect:/mygames";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/games/add")
    public String addNewGame(@RequestParam("file") MultipartFile file,
                             @RequestParam("id") String id,
                             @RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("pricePerDay") int pricePerDay,
                             RedirectAttributes redirectAttributes) {


        Image gameImage;
        try {
            gameImage = imageService.createImage(file);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Nie udało się wysłać zdjęcia, spróbuj ponownie później." + file.getOriginalFilename());
            return "redirect:/mygames";
        }

        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setDescription(description);
        game.setPricePerDay(pricePerDay);
        game.setImages(Arrays.asList(gameImage));
        gameService.addGame(game);
        redirectAttributes.addFlashAttribute("flash.message", "Dodano nową grę." + file.getOriginalFilename());

        return "redirect:/mygames";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/submitAdvertOfferForGame")
    public String submitAdvertOfferForGame(@RequestParam("file") MultipartFile file,
                                           @PathVariable String gameId,
                                           @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                           @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                           RedirectAttributes redirectAttributes) {

        try {
            String imageUrl = imageService.createImage(file).getUrl();
            Advert advert = new Advert();
            advert.setGame(gameService.getGame(gameId));
            advert.setImageURL(imageUrl);
            advert.setStartDate(startDate.getTime() / 1000);
            advert.setEndDate(endDate.getTime() / 1000);

            AdvertOffer advertOffer = new AdvertOffer();
            advertOffer.setState(AdvertOfferStates.WAITING_FOR_GAME_OWNER_ACCEPTANCE);
            advertOffer.setAdvert(advert);
            gameService.submitAdvertOffer(advertOffer);
            redirectAttributes.addFlashAttribute("flash.message", "Pomyślnie złożono ofertę!");
        } catch (AdvertBuyException e) {
            redirectAttributes.addFlashAttribute("flash.message", "Nie udało się złożyć oferty: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("flash.message", "Coś poszło nie tak: " + e.getMessage());
        }

        return "redirect:/games/" + gameId;
    }
}
