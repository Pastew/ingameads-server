package com.pastew.ingameadsserver.Game;

import com.pastew.ingameadsserver.Image.ImageService;
import com.pastew.ingameadsserver.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @RequestMapping(method = RequestMethod.GET, value = "/games/{id}")
    public String getGame(Model model, @PathVariable String id) {
        Game game = gameService.getGame(id);
        model.addAttribute("game", game);
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
}
