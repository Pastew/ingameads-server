package com.pastew.ingameadsui.Game;

import com.pastew.ingameadsui.Image.Image;
import com.pastew.ingameadsui.Image.ImageService;
import com.pastew.ingameadsui.User.User;
import com.pastew.ingameadsui.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    public void addGame(Game game) {
        game.setOwner(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        gameRepository.save(game);
    }

    public Page<Game> findPage(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    public Game getGame(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> getCurrentGameDeveloperGames() {
        String ownerName = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(ownerName);
        return gameRepository.findByOwnerId(owner.getId());
    }

    public void addImageToGame(MultipartFile file, String gameId) throws IOException {
        Image image = imageService.createImage(file);
        Game game = getGame(gameId);
        game.getImages().add(image);
        gameRepository.save(game);
    }

}
