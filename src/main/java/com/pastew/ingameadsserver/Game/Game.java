package com.pastew.ingameadsserver.Game;

import com.pastew.ingameadsserver.Image.Image;
import com.pastew.ingameadsserver.User.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Game {

    @Id
    private String id;
    private String title;
    private String description;

    @OneToOne
    private User owner;

    @OneToMany
    private List<Image> images;

    public Game() {
    }

    public Game(String id, User owner, String title, String description, List<Image> images) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.images = images;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
