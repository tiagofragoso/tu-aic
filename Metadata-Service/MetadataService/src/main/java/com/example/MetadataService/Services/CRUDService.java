package com.example.MetadataService.Services;

import com.example.MetadataService.DTOs.*;
import com.example.MetadataService.Entities.SensingEvent;
import com.example.MetadataService.Entities.Tag;
import com.example.MetadataService.Repositories.SensingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class CRUDService {

    @Autowired
    private SensingEventRepository eventRepository;

    /**
     * Delete an event
     * @param id the id of the event that should be deleted.
     * @throws Exception Throws an exception if the item could not be found.
     */
    public void deleteEvent(String id) throws Exception {

        Optional<SensingEvent> event = eventRepository.findById(id);

        if(event.isPresent()) {
            eventRepository.delete(event.get());
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find the given Sensing Event");
        }
    }

    /**
     * Create an event and store it in the persistence.
     * @param event the event that should be stored.
     */
    public void createEvent(EventDTO event) {
        try {
            event.setUpdated(event.getTimestamp());
            eventRepository.insert(sensingDtoToReal(event));
        }
        catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The sensing event was already added");
        }
    }

    /**
     * Update an event in the persistence.
     * @param event the event that should be stored.
     */
    public void updateEvent(EventDTO event) {
        try {

            Optional<SensingEvent> storedOpt = eventRepository.findById(event.getSensingEventId());

            if(!storedOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The specified event could not be found!");
            }

            SensingEvent stored = storedOpt.get();

            ArrayList<TagDTO> tagDtos = new ArrayList<>();
            for(Tag tagdt : stored.getTags()) {
                tagDtos.add(tagRealToDto(tagdt));
            }
            event.setTags(tagDtos);
            event.setUpdated(Instant.now().getEpochSecond());

            eventRepository.save(sensingDtoToReal(event));
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
    public PageMetaPlusItemDTO getAllEvents(Pageable pageRequest, String search) throws Exception {

        List<SimpleEventDTO> ret = new ArrayList<>();

        List<Sort.Order> orders = new ArrayList<>();
        for (Iterator<Sort.Order> it = pageRequest.getSort().stream().iterator(); it.hasNext(); ) {
            Sort.Order order = it.next();

            try {
                orders.add(Sort.Order.by(AttributeMapper.mapJsonAttributeToInternalAttributes(order.getProperty())).with(order.getDirection()));
            }
            catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given attribute for sorting is not valid.");
            }
        }

        Pageable modifiedPageRequest = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), Sort.by(orders));

        Page<SensingEvent> page;
        if(search != null) {
            page = findEventFullSearch(search, modifiedPageRequest);
        }
        else {
            page = eventRepository.findAll(modifiedPageRequest);
        }

        for(SensingEvent event : page.getContent()) {
            ret.add(sensingRealToSimpleDto(event));
        }

        return new PageMetaPlusItemDTO(ret, page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    /**
     * Get all events in radius
     * @param size the size of the radius in km
     * @param lon the longitude
     * @param lat the latitude
     * @return returns all event dtos found in the radius
     * @throws Exception
     */
    public List<SimpleEventDTO> getAllEventsInRadius(double size, double lon, double lat) throws Exception {
        List<SimpleEventDTO> ret = new ArrayList<>();

        for(SensingEvent event : eventRepository.findSensingEventInCircle(size, lon, lat)) {
            ret.add(sensingRealToSimpleDto(event));
        }

        return ret;
    }

    /**
     * Delete a tag.
     * @param event_id the id of the sensing event.
     * @param tag_name the id of the tag that should be removed
     */
    public synchronized void deleteTag(String event_id, String tag_name) {
        SensingEvent event = getEventIfExists(event_id);

        // Check if the tag already exists in the event

        Tag tag = event.getTags().stream().filter(x -> x.getTagName().equals(tag_name)).findAny().orElse(null);

        if(tag == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given tag name was not found.");
        }

        event.getTags().remove(tag);
        event.updateTagsConcatString();
        eventRepository.save(event);
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

        event.getTags().add(tagDtoToReal(tag, Instant.now().getEpochSecond()));
        event.updateTagsConcatString();
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
            addTag(event_id, tag);
        }
        else {
            Tag realTag = foundTag.get();
            realTag.setImageHash(tag.getImageHash());

            eventRepository.save(event);
        }
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
            tags.add(tagDtoToReal(tag, dto.getTimestamp()));
        }

        SensingEvent realEvent = new SensingEvent(dto.getSensingEventId(), dto.getName(), dto.getDeviceIdentifier(), dto.getTimestamp(), tags, dto.getLongitude(), dto.getLatitude(), dto.getFrameNum(), dto.getPlaceIdent(), dto.getEventFrames(), dto.getUpdated());
        return realEvent;
    }

    /**
     * Converts a tag dto to a normal tag
     * @param dto the dto that should be converted.mongorepos
     * @return returns a tag
     */
    private Tag tagDtoToReal(TagDTO dto, long created) {
        return new Tag(dto.getTagName(), dto.getImageHash(), created);
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

        return new EventDTO(event.getId(), event.getName(), event.getDeviceIdentifier(), event.getTimestamp(), event.getLongitude(), event.getLatitude(), tags, event.getFrameNum(), event.getPlaceIdent(), event.getEventFrames(), event.getUpdated());
    }

    /**
     * Converts a tag to a tag dto
     * @param tag the tag that should be converted.
     * @return returns a tag dto.
     */
    private TagDTO tagRealToDto(Tag tag) {
        return new TagDTO(tag.getTagName(), tag.getImageHash(), tag.getCreated());
    }

    /**
     * Converts a tag to a simple tag dto
     * @param tag the tag that should be converted.
     * @return returns a tag dto.
     */
    private SimpleTagDTO tagRealToSimpleDto(Tag tag) {
        return new SimpleTagDTO(tag.getTagName(), tag.getImageHash());
    }

    /**
     * Convert a sensing event to a simple dto
     * @param event the event that should be converted.
     * @return returns the dto
     * @throws Exception throws an exception if the location was wrongly stored.
     */
    private SimpleEventDTO sensingRealToSimpleDto(SensingEvent event) throws Exception {
        List<SimpleTagDTO> tags = new ArrayList<>();

        for(Tag tag : event.getTags()) {
            tags.add(tagRealToSimpleDto(tag));
        }

        return new SimpleEventDTO(event.getId(), event.getName(), event.getPlaceIdent(), event.getTimestamp(), event.getLongitude(), event.getLatitude(), tags, event.getUpdated());
    }

    private Page<SensingEvent> findEventFullSearch(String searchText, Pageable pageable) {
        return eventRepository.findByNameContainingIgnoreCaseOrPlaceIdentContainingIgnoreCaseOrCreatedHumanReadableContainingIgnoreCaseOrUpdatedHumanReadableContainingIgnoreCaseOrTagConcatContainingIgnoreCase(searchText, searchText, searchText, searchText, searchText, pageable);

    }
}
