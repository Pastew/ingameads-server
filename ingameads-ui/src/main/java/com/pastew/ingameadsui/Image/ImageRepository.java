package com.pastew.ingameadsui.Image;

import com.pastew.ingameadsui.User.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    public Image findByName(String name);

    List<Image> findByOwner(User owner);
}
