package com.example.MetadataService.Repositories;

import com.example.MetadataService.Entities.SensingEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SensingEventRepository extends MongoRepository<SensingEvent, String>, SensingEventRepositoryCustom {
        Page<SensingEvent> findByNameContainingIgnoreCaseOrPlaceIdentContainingIgnoreCaseOrCreatedHumanReadableContainingIgnoreCaseOrUpdatedHumanReadableContainingIgnoreCaseOrTagConcatContainingIgnoreCase(String name, String placeIdent, String createdHumanReadable, String updated, String tagConcat, Pageable pageable);
}
