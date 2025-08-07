package com.app.focus.entity;
import com.app.focus.entity.enums.TimerMode;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "focus_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate sessionDate;
    private java.time.LocalTime startTime;
    private java.time.LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private TimerMode timerMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}

