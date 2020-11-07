package com.example.MetadataService.Services;

import com.example.MetadataService.DTOs.EventDTO;
import com.example.MetadataService.DTOs.TagDTO;
import com.example.MetadataService.Entities.SensingEvent;
import com.example.MetadataService.Entities.Tag;
import com.example.MetadataService.Repositories.SensingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CRUDService {

    @Autowired
    private SensingEventRepository eventRepository;


    public void createEvent(EventDTO event) {
        eventRepository.insert(sensingDtoToReal(event));
    }

    public EventDTO getEventDetails(String id) throws Exception {

        Optional<SensingEvent> event = eventRepository.findById(id);

        if(event.isPresent()) {
            return sensingRealToDto(event.get());
        }
        else {
            // TODO need to define custom exceptions with error code
            throw new Exception("Could not find the given Sensing Event");
        }
    }

    public List<EventDTO> getAllEvents() throws Exception {

        List<EventDTO> ret = new ArrayList<>();

        for(SensingEvent event : eventRepository.findAll()) {
            ret.add(sensingRealToDto(event));
        }

        return ret;
    }

    public List<EventDTO> getAllEventsInRadius(double size, double lon, double lat) throws Exception {
        List<EventDTO> ret = new ArrayList<>();

        for(SensingEvent event : eventRepository.findSensingEventInCircle(size, lon, lat)) {
            ret.add(sensingRealToDto(event));
        }

        return ret;
    }

    // TODO should be atomic
    public void addTag(TagDTO tag) {
//        eventRepository.
    }

    // TODO should be atomic
    public void updateTag(TagDTO tag) {

    }

    private SensingEvent sensingDtoToReal(EventDTO dto) {

        List<Tag> tags = new ArrayList<>();
        for(TagDTO tag : dto.getTags()) {
            tags.add(tagDtoToReal(tag));
        }

        SensingEvent realEvent = new SensingEvent(dto.getSensingEventId(), dto.getName(), dto.getDeviceIdentifier(), dto.getTimestamp(), tags, dto.getLongitude(), dto.getLatitude());
        return realEvent;
    }

    private Tag tagDtoToReal(TagDTO dto) {
        return new Tag(dto.getTagName(), dto.getImageHash());
    }


    private EventDTO sensingRealToDto(SensingEvent event) throws Exception {
        List<TagDTO> tags = new ArrayList<>();

        for(Tag tag : event.getTags()) {
            tags.add(tagRealToDto(tag));
        }

        return new EventDTO(event.getId(), event.getName(), event.getDeviceIdentifier(), event.getTimestamp(), tags, event.getLongitude(), event.getLatitude());
    }

    private TagDTO tagRealToDto(Tag tag) {
        return new TagDTO(tag.getTagName(), tag.getImageHash());
    }
}
