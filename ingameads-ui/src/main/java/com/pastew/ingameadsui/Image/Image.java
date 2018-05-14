package com.pastew.ingameadsui.Image;

import com.pastew.ingameadsui.User.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@Entity
public class Image {

    @Id @GeneratedValue
    private Long id;

    @OneToOne
    private User owner;
    private String url;

    public Image(User owner, String url) {
        this.owner = owner;
        this.url = url;
    }
}
