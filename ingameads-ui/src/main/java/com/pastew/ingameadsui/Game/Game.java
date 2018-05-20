package com.pastew.ingameadsui.Game;

import com.pastew.ingameadsui.Image.Image;
import com.pastew.ingameadsui.User.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private String id;

    private String title;
    private int pricePerDay; // $

    @Column( length = 100000 )
    private String description;

    @ManyToOne
    private User owner;

    @OneToMany
    private List<Image> images;
}
