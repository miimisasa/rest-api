package todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Todo {

        private UUID id;

        private LocalDateTime dateCreated;

        private boolean done;

        private String task;

        public Todo(boolean done, String task) {
                this(UUID.randomUUID(), LocalDateTime.now(), done, task);
        }
}

