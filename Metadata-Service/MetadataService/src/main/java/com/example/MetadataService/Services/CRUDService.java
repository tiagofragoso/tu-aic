package com.example.MetadataService.Services;

import com.example.MetadataService.DTOs.EventDTO;
import com.example.MetadataService.DTOs.TagDTO;
import com.example.MetadataService.Entities.SensingEvent;
import com.example.MetadataService.Entities.Tag;
import com.example.MetadataService.Repositories.SensingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CRUDService {

    @Autowired
    private SensingEventRepository eventRepository;

    /**
     * Create an event and store it in the persistence.
     * @param event the event that should be stored.
     */
    public void createEvent(EventDTO event) {
        try {
            eventRepository.insert(sensingDtoToReal(event));
        }
        catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The sensing event was already added");
        }
    }

    /**
     * Get the details of an event
     * @param id the id of the event that should be returned.
     * @return returns the details of an event.
     * @throws Exception Throws an exception if the item could not be found.
     */
    public EventDTO getEventDetails(String id) throws Exception {

        Optional<SensingEvent> event = eventRepository.findById(id);

        if(event.isPresent()) {
            return sensingRealToDto(event.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the given Sensing Event");
        }
    }

    /**
     * Get all events stored in the persistence.
     * @return returns all events.
     * @throws Exception Throws an exception if location data was wrongly stored.
     */
    public List<EventDTO> getAllEvents() throws Exception {

        List<EventDTO> ret = new ArrayList<>();

        for(SensingEvent event : eventRepository.findAll()) {
            ret.add(sensingRealToDto(event));
        }

        return ret;
    }

    /**
     * Get all events in radius
     * @param size the size of the radius in km
     * @param lon the longitude
     * @param lat the latitude
     * @return returns all event dtos found in the radius
     * @throws Exception
     */
    public List<EventDTO> getAllEventsInRadius(double size, double lon, double lat) throws Exception {
        List<EventDTO> ret = new ArrayList<>();

        for(SensingEvent event : eventRepository.findSensingEventInCircle(size, lon, lat)) {
            ret.add(sensingRealToDto(event));
        }

        return ret;
    }


    /**
     * Add a tag to a sensing event.
     * @param event_id the id of the sensing event.
     * @param tag the tag dto.
     */
    public synchronized void addTag(String event_id, TagDTO tag) {
        SensingEvent event = getEventIfExists(event_id);

        // Check if the tag already exists in the event
        if(event.getTags().stream().anyMatch(x -> x.getTagName().equals(tag.getTagName()))){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The given tag was already added to this event.");
        }

        event.getTags().add(tagDtoToReal(tag));
        eventRepository.save(event);
    }


    /**
     * Update a tag.
     * @param event_id the id of the sensing event
     * @param tag the tag dto.
     */
    public synchronized void updateTag(String event_id, TagDTO tag) {

        SensingEvent event = getEventIfExists(event_id);

        Optional<Tag> foundTag = event.getTags().stream().filter(x -> x.getTagName().equals(tag.getTagName())).findFirst();

        if(!foundTag.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the given tag.");
        }

        Tag realTag = foundTag.get();
        realTag.setImageHash(tag.getImageHash());

        eventRepository.save(event);
    }

    /**
     * Get an event if it exists, otherwise throw an not found exception.
     * @param id the id of the event
     * @return returns an event.
     */
    private SensingEvent getEventIfExists(String id) {
        Optional<SensingEvent> optEvent = eventRepository.findById(id);

        if(!optEvent.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the given Sensing Event");
        }

        SensingEvent event = optEvent.get();
        return event;
    }

    /**
     * Converts a dto to a real sensing event object
     * @param dto the dto that should be converted.
     * @return returns a real sensing event object
     */
    private SensingEvent sensingDtoToReal(EventDTO dto) {

        List<Tag> tags = new ArrayList<>();
        for(TagDTO tag : dto.getTags()) {
            tags.add(tagDtoToReal(tag));
        }

        SensingEvent realEvent = new SensingEvent(dto.getSensingEventId(), dto.getName(), dto.getDeviceIdentifier(), dto.getTimestamp(), tags, dto.getLongitude(), dto.getLatitude());
        return realEvent;
    }

    /**
     * Converts a tag dto to a normal tag
     * @param dto the dto that should be converted.
     * @return returns a tag
     */
    private Tag tagDtoToReal(TagDTO dto) {
        return new Tag(dto.getTagName(), dto.getImageHash());
    }

    /**
     * Convert a sensing event to a dto
     * @param event the event that should be converted.
     * @return returns the dto
     * @throws Exception throws an exception if the location was wrongly stored.
     */
    private EventDTO sensingRealToDto(SensingEvent event) throws Exception {
        List<TagDTO> tags = new ArrayList<>();

        for(Tag tag : event.getTags()) {
            tags.add(tagRealToDto(tag));
        }

        return new EventDTO(event.getId(), event.getName(), event.getDeviceIdentifier(), event.getTimestamp(), event.getLongitude(), event.getLatitude(), tags);
    }

    /**
     * Converts a tag to a tag dto
     * @param tag the tag that should be converted.
     * @return returns a tag dto.
     */
    private TagDTO tagRealToDto(Tag tag) {
        return new TagDTO(tag.getTagName(), tag.getImageHash());
    }
}
