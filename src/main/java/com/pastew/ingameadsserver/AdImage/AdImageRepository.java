package com.pastew.ingameadsserver.AdImage;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AdImageRepository extends PagingAndSortingRepository<AdImage, Long> {

    public AdImage findByName(String name);
}
