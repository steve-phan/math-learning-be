package com.mathlearning.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserProgress {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "total_xp")
    @Builder.Default
    private Integer totalXp = 0;
    
    @Column(name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0;
    
    @Column(name = "longest_streak")
    @Builder.Default
    private Integer longestStreak = 0;
    
    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
