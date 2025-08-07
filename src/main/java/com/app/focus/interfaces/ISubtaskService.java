package com.app.focus.interfaces;

import com.app.focus.dto.task.SubtaskRequestDTO;
import com.app.focus.dto.task.SubtaskResponseDTO;
import java.util.List;

public interface ISubtaskService {
    SubtaskResponseDTO createSubtask(Long parentTaskId, SubtaskRequestDTO dto);
    List<SubtaskResponseDTO> getSubtasksByParentId(Long parentTaskId);
    SubtaskResponseDTO updateSubtask(Long subtaskId, SubtaskRequestDTO dto);
    void deleteSubtask(Long subtaskId);
}
