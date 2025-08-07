package com.app.focus.interfaces;

import com.app.focus.dto.event.EventRequestDTO;
import com.app.focus.dto.event.EventResponseDTO;
import java.util.List;

public interface IEventService {
    EventResponseDTO createEvent(EventRequestDTO dto);
    List<EventResponseDTO> getUserEvents();
    EventResponseDTO updateEvent(Long id, EventRequestDTO dto);
    void deleteEvent(Long id);
    List<EventResponseDTO> getEventsByUserId(Long userId);
}
