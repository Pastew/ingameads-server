package com.pastew.ingameadsstatsservergateway;

import com.pastew.ingameadsstatsservergateway.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class StatServerGatewayService {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private GameRepository gameRepository;


    public void pushMethod(String gameId, AdVisibleObject[] adVisibleObjects) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("id").is(gameId)),
                new Update().push("adVisibleObjectList", adVisibleObjects[0]), AdVisibleObject.class);
    }

    public void uploadStats(String gameId, AdVisibleObject[] adVisibleObjects) {
        List<AdVisibleObject> adVisibleObjectsList = Arrays.asList(adVisibleObjects);

        Optional<Game> game = gameRepository.findById(gameId);
        if (!game.isPresent()) {
            Game newGame = new Game(gameId, adVisibleObjectsList);
            gameRepository.save(newGame);
            return;
        }
        Game updatedGame = game.get();
        updatedGame.getAdVisibleObjectList().addAll(adVisibleObjectsList);
        gameRepository.save(updatedGame);
    }

    public List<AdVisibleObject> getAdvertsByGame(String gameId) {
        Game game = gameRepository.findById(gameId).get();
        return game.getAdVisibleObjectList();
    }
}