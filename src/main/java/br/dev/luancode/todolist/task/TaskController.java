package br.dev.luancode.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.luancode.todolist.response.ResponseHandler;
import br.dev.luancode.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");
    taskModel.setUserId(userId);

    var currentDate = LocalDateTime.now();

    if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
      return ResponseHandler.generateResponse("The start/end at date must be greater than the current date",
          HttpStatus.BAD_REQUEST, null);
    }

    if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseHandler.generateResponse("The start at date must be less than the end at date",
          HttpStatus.BAD_REQUEST, null);
    }

    var task = this.taskRepository.save(taskModel);

    return ResponseHandler.generateResponse(null, HttpStatus.CREATED, task);
  }

  @GetMapping("/")
  public ResponseEntity<Object> list(HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");
    var tasks = this.taskRepository.findByUserId(userId);

    return ResponseHandler.generateResponse(null, HttpStatus.OK, tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
      HttpServletRequest request) {
    var currentTask = this.taskRepository.findById(id).orElse(null);
    var userId = (UUID) request.getAttribute("userId");

    if (currentTask == null) {
      return ResponseHandler.generateResponse("Task not found", HttpStatus.NOT_FOUND,
          null);
    }

    if (!currentTask.getUserId().equals(userId)) {
      return ResponseHandler.generateResponse("User without permission to edit this task", HttpStatus.BAD_REQUEST,
          null);
    }

    Utils.copyNonNullProperties(taskModel, currentTask);

    var task = this.taskRepository.save(currentTask);

    return ResponseHandler.generateResponse(null, HttpStatus.CREATED, task);
  }

}
