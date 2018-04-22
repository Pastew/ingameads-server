package com.pastew.ingameadsserver.Game;

import com.pastew.ingameadsserver.Image.Image;
import com.pastew.ingameadsserver.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public void addGame(Game game) {
        game.setOwner(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        gameRepository.save(game);
    }

    public Page<Game> findPage(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }
}
