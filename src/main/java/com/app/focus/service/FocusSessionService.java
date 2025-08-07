package com.app.focus.service;

import com.app.focus.dto.focusSession.FocusSessionRequestDTO;
import com.app.focus.dto.focusSession.FocusSessionResponseDTO;
import com.app.focus.entity.FocusSession;
import com.app.focus.entity.User;
import com.app.focus.interfaces.IFocusSessionService;
import com.app.focus.repository.FocusSessionRepository;
import com.app.focus.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FocusSessionService implements IFocusSessionService {

    private final FocusSessionRepository sessionRepository;
    private final AuthenticatedUserProvider authUserProvider;
    private final ModelMapper modelMapper;

    @Override
    public FocusSessionResponseDTO createSession(FocusSessionRequestDTO dto) {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Creating new focus session for user ID: {}", user.getId());

        FocusSession session = modelMapper.map(dto, FocusSession.class);
        session.setUser(user);

        FocusSession saved = sessionRepository.save(session);
        log.info("Focus session successfully created with ID: {}", saved.getId());

        return modelMapper.map(saved, FocusSessionResponseDTO.class);
    }

    @Override
    public List<FocusSessionResponseDTO> getMySessions() {
        User user = authUserProvider.getAuthenticatedUser();
        log.info("Fetching focus sessions for user ID: {}", user.getId());

        List<FocusSession> sessions = sessionRepository.findByUserIdOrderBySessionDateDesc(user.getId());
        log.debug("Retrieved {} focus sessions for user ID: {}", sessions.size(), user.getId());

        return sessions.stream()
                .map(s -> modelMapper.map(s, FocusSessionResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FocusSessionResponseDTO> getTodaySessions() {
        User user = authUserProvider.getAuthenticatedUser();
        LocalDate today = LocalDate.now();
        log.info("Fetching today's focus sessions for user ID: {}", user.getId());

        List<FocusSession> sessions = sessionRepository.findByUserAndSessionDate(user, today);
        log.debug("Retrieved {} sessions for today ({}) for user ID: {}", sessions.size(), today, user.getId());

        return sessions.stream()
                .map(session -> modelMapper.map(session, FocusSessionResponseDTO.class))
                .collect(Collectors.toList());
    }
}
