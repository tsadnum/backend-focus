package com.app.focus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
    private String description;

    private String icon;


    @ElementCollection
    @CollectionTable(name = "habit_days", joinColumns = @JoinColumn(name = "habit_id"))
    @Column(name = "day")
    private List<String> activeDays;

    @ElementCollection
    private List<LocalTime> reminderTimes;

    private boolean isActive;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitLog> logs;
}
