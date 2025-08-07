package com.app.focus.interfaces;

import com.app.focus.dto.focusSession.FocusSessionRequestDTO;
import com.app.focus.dto.focusSession.FocusSessionResponseDTO;
import java.util.List;

public interface IFocusSessionService {
    FocusSessionResponseDTO createSession(FocusSessionRequestDTO dto);
    List<FocusSessionResponseDTO> getMySessions();
    List<FocusSessionResponseDTO> getTodaySessions();
}