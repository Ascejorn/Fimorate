package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@NoArgsConstructor
@Setter
@Getter
@ToString
@SuperBuilder
public class Feed {
    Instant timestamp;
    Long userId;
    String eventType;
    String operation;
    Long eventId;
    Long entityId;

}
