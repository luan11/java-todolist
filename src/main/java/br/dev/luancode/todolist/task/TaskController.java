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
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    taskModel.setUserId((UUID) request.getAttribute("userId"));

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
    var tasks = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));

    return ResponseHandler.generateResponse(null, HttpStatus.OK, tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
      HttpServletRequest request) {
    taskModel.setUserId((UUID) request.getAttribute("userId"));
    taskModel.setId(id);

    var task = this.taskRepository.save(taskModel);

    return ResponseHandler.generateResponse(null, HttpStatus.CREATED, task);
  }

}
