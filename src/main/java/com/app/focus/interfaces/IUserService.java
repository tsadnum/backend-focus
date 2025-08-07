package com.app.focus.interfaces;

import com.app.focus.dto.user.UserRequestDTO;
import com.app.focus.dto.user.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserService {
    List<UserResponseDTO> getUsersFiltered(LocalDateTime startDate, LocalDateTime endDate, String status);
    UserResponseDTO updateUser(Long userId, UserRequestDTO dto);
    UserResponseDTO getCurrentUserProfile();
}
