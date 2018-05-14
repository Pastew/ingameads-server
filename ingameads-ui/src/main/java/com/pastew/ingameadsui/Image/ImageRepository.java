package com.pastew.ingameadsui.Image;

import com.pastew.ingameadsui.User.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    List<Image> findByOwner(User owner);

    Image findByUrl(String url);
}
