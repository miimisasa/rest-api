package todo.rest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.web.bind.annotation.*;
import todo.Todo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("api/todo")
@RequiredArgsConstructor
public final class TodoRestController {

    @NonNull
    private final JdbcOperations jdbcOperations;

    @GetMapping
    public ResponseEntity<List<Todo>> getTodoList() {
        return ResponseEntity.ok(this.jdbcOperations.query("select * from todo", (resultSet, i) ->
                new Todo(UUID.fromString(resultSet.getString("id")),
                        resultSet.getTimestamp("date_created").toLocalDateTime(),
                        resultSet.getBoolean("done"),
                        resultSet.getString("task"))));
    }

    @GetMapping("{todoId}")
    public ResponseEntity<Todo> getTodo(@PathVariable UUID todoId) {
        try {
            return ResponseEntity.ok(Objects.requireNonNull(this.jdbcOperations.queryForObject("select * from todo where id = ? limit 1",
                    (resultSet, i) ->
                            new Todo(UUID.fromString(resultSet.getString("id")),
                                    resultSet.getTimestamp("date_created").toLocalDateTime(),
                                    resultSet.getBoolean("done"),
                                    resultSet.getString("task")), todoId.toString())));
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, "application/vnd.example.todo_payload+json"})
    public ResponseEntity<Todo> createTodo(@RequestBody TodoPayload payload) {
        var todo = new Todo(payload.isDone(), payload.getTask());
        jdbcOperations.update("insert into todo (id, date_created, done, task) values (?, ?, ?, ?)",
                todo.getId().toString(), Timestamp.valueOf(todo.getDateCreated()), todo.isDone(), todo.getTask());

        return ResponseEntity.ok(todo);
    }

    @PutMapping(path = "{todoId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, "application/vnd.example.todo_payload+json"})
    public ResponseEntity<Void> modifyTodo(@PathVariable UUID todoId, @RequestBody TodoPayload payload) {
        if (this.jdbcOperations.update("update todo set done = ?, task = ? where id = ?", payload.isDone(),
                payload.getTask()) == 1) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID todoId) {
        if (this.jdbcOperations.update("delete from todo where id = ?", todoId.toString()) == 1) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
