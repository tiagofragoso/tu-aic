package com.example.MetadataService.Repositories;

import com.example.MetadataService.Entities.SensingEvent;

import java.util.List;

public interface SensingEventRepositoryCustom {
    List<SensingEvent> findSensingEventInCircle(double size, double lon, double lat);
}
