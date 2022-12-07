package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Setter
@Getter
@ToString
@SuperBuilder
public class Feed {
    Long timestamp;
    Long userId;
    String eventType;
    String operation;
    Long eventId;
    Long entityId;

}
