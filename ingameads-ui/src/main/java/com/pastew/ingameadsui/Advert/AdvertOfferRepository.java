package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.User.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdvertOfferRepository extends CrudRepository<AdvertOffer, Long> {
    List<AdvertOffer> findByAdvertGameOwner(User gameOwner);
}
