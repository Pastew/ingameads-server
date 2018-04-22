package com.pastew.ingameadsserver.Game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
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

    @RequestMapping(method = RequestMethod.GET, value = "games/{id}")
    public String getGame(Model model, @PathVariable String id){
        Game game = gameService.getGame(id);

        model.addAttribute("game", game);
        return "game";
    }
}
