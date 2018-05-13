package com.pastew.ingameadsui.Advert;

import com.pastew.ingameadsui.User.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AdvertOffer {

    @Id
    @GeneratedValue
    long id;

    @OneToOne(cascade = {CascadeType.PERSIST})
    Advert advert;

    @ManyToOne
    User buyer;

    @ManyToOne
    User gameOwner;

    @Enumerated(EnumType.STRING)
    private AdvertOfferStates state;

}
