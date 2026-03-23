package ru.personal.baserestcontroller.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.personal.baserestcontroller.dto.TaskDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class Task1Controller {

    public static List<TaskDto> tasks;

    public Task1Controller() {
        tasks = new ArrayList<>();
        tasks.add(new TaskDto("id1", "title1", false));
        tasks.add(new TaskDto("id2", "title2", true));
        tasks.add(new TaskDto("id3", "title3", false));
        tasks.add(new TaskDto("id4", "title4", true));
        tasks.add(new TaskDto("id5", "title5", false));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks() {
        return ResponseEntity.ofNullable(tasks);
    }

    @PostMapping
    public ResponseEntity<List<TaskDto>> createTask(@RequestBody TaskDto taskDto) {
        tasks.add(taskDto);
        return ResponseEntity.ofNullable(tasks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<TaskDto>> deleteTask(@PathVariable String id) {
        tasks.removeIf(task -> task.id().equals(id));
        return ResponseEntity.ofNullable(tasks);
    }

    @PutMapping
    public ResponseEntity<List<TaskDto>> updateTask(@RequestBody TaskDto taskDto) {
        var updatedTask = tasks.stream()
                .filter(task -> task.id().equals(taskDto.id()))
                .findFirst();
        if (updatedTask.isPresent()) {
            tasks.remove(updatedTask.get());
            tasks.add(taskDto);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } else {
            tasks.add(taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(tasks);
        }
    }

}
