package com.pastew.ingameadsimageprovider;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ImageProviderRepository extends CrudRepository<Advert, Long> {
    Advert findByGameId(String gameId);

    @Query(value = "SELECT a FROM Advert a WHERE a.gameId = :gameId AND :currentTime BETWEEN a.startDate AND a.endDate")
    Advert findFirstCurrentAdvertByGameId(@Param("gameId")String gameId, @Param("currentTime") long currentTime);

}
