package com.example.MetadataService.Repositories.Implementation;

import com.example.MetadataService.Entities.SensingEvent;
import com.example.MetadataService.Repositories.SensingEventRepositoryCustom;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.List;

public class SensingEventRepositoryImpl implements SensingEventRepositoryCustom {

    private final MongoTemplate operations;

    public SensingEventRepositoryImpl(MongoTemplate operations) {
        Assert.notNull(operations,"MongoOperations must not be null");
        this.operations = operations;
    }

    /**
     * Get all of the sensing event data that is contained in the given circle.
     * @param size radius in kilometers
     * @param lon longitude of the center of the circle
     * @param lat latitude of the center of the circle
     * @return
     */
    @Override
    public List<SensingEvent> findSensingEventInCircle(double size, double lon, double lat) {
        // Calculation to radius according to https://stackoverflow.com/questions/41059429/query-location-with-kilometer-radius-in-mongodb
        Circle radius = new Circle(lon, lat, size / 6378.1);

        List<SensingEvent> events = operations.find(new Query(Criteria.where("gpsLocation").withinSphere(radius)), SensingEvent.class);
        return events;
    }
}
