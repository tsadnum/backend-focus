package com.app.focus.service;

import com.app.focus.dto.event.EventRequestDTO;
import com.app.focus.dto.event.EventResponseDTO;
import com.app.focus.entity.Event;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IEventService;
import com.app.focus.repository.EventRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final AuthenticatedUserProvider authUserProvider;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating event for user: {}", user.getEmail());

        if (dto.getEndDateTime().isBefore(dto.getStartDateTime()) || dto.getEndDateTime().isEqual(dto.getStartDateTime())) {
            log.warn("Invalid event time: endDateTime {} is not after startDateTime {}", dto.getEndDateTime(), dto.getStartDateTime());
            throw new IllegalArgumentException("La fecha/hora de finalización debe ser posterior a la de inicio.");
        }

        Event event = modelMapper.map(dto, Event.class);
        event.setUser(user);
        Event saved = eventRepository.save(event);

        log.info("Event successfully created with ID: {}", saved.getId());
        return modelMapper.map(saved, EventResponseDTO.class);
    }


    @Override
    public List<EventResponseDTO> getUserEvents() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Retrieving events for user: {}", user.getEmail());

        List<Event> events = eventRepository.findByUser(user);
        log.debug("Retrieved {} events for user: {}", events.size(), user.getEmail());

        return events.stream()
                .map(event -> modelMapper.map(event, EventResponseDTO.class))
                .toList();
    }

    @Override
    public EventResponseDTO updateEvent(Long id, EventRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Updating event with ID: {} for user: {}", id, user.getEmail());

        if (dto.getEndDateTime().isBefore(dto.getStartDateTime()) || dto.getEndDateTime().isEqual(dto.getStartDateTime())) {
            log.warn("Invalid event time on update: endDateTime {} is not after startDateTime {}", dto.getEndDateTime(), dto.getStartDateTime());
            throw new IllegalArgumentException("La fecha/hora de finalización debe ser posterior a la de inicio.");
        }

        Event existing = getUserEventOrThrow(id, user.getId());
        modelMapper.map(dto, existing);

        Event updated = eventRepository.save(existing);
        log.info("Event with ID: {} updated successfully", updated.getId());

        return modelMapper.map(updated, EventResponseDTO.class);
    }


    @Override
    public void deleteEvent(Long id) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Deleting event with ID: {} for user: {}", id, user.getEmail());

        Event event = getUserEventOrThrow(id, user.getId());

        eventRepository.delete(event);
        log.info("Event with ID: {} deleted successfully", id);
    }

    @Override
    public List<EventResponseDTO> getEventsByUserId(Long userId) {
        log.info("Retrieving events by userId: {}", userId);
        List<Event> events = eventRepository.findByUserId(userId);
        log.debug("Found {} events for userId: {}", events.size(), userId);

        return events.stream()
                .map(event -> modelMapper.map(event, EventResponseDTO.class))
                .toList();
    }


    private Event getUserEventOrThrow(Long id, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found with ID: {}", id);
                    return new EntityNotFoundException("Event not found");
                });

        if (!event.getUser().getId().equals(userId)) {
            log.warn("Access denied: userId {} tried to access event ID: {}", userId, id);
            throw new AccessDeniedException("Not authorized to access this event");
        }

        return event;
    }
}
