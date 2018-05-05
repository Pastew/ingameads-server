package com.pastew.ingameadsui.Game;

import com.pastew.ingameadsui.Image.Image;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameRepository extends PagingAndSortingRepository<Game, String> {

    public List<Game> findByOwnerId(Long ownerId);

    Game findByImages(Image image);
}
