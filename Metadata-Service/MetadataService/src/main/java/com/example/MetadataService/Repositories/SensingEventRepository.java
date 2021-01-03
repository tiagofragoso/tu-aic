package com.example.MetadataService.Repositories;

import com.example.MetadataService.Entities.SensingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SensingEventRepository extends MongoRepository<SensingEvent, String>, SensingEventRepositoryCustom {

}
