package com.pastew.ingameadsserver.Game;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameRepository extends PagingAndSortingRepository<Game, String> {

    public List<Game> findByOwnerId(Long ownerId);
}
