package com.example.moviequizz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    protected Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 20, message = "Username cannot exceed 20 characters")
    @Column(nullable = false)
    private String username;

    @Min(value = 0, message = "Score must be zero or positive")
    @Column(name = "score", nullable = false)
    private int score;

    // May refactor the below to a base entity if needed elsewhere
    @Column(name = "create_time", updatable = false, nullable = false)
    @ToString.Exclude
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        setCreateTime(LocalDateTime.now());
    }
}
