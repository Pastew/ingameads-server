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

    @OneToOne(cascade = {CascadeType.ALL})
    Advert advert;

    @ManyToOne
    User buyer;
}
