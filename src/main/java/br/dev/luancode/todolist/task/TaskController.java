package br.dev.luancode.todolist.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.luancode.todolist.response.ResponseHandler;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<Object> create(@RequestBody TaskModel taskModel) {
    var task = this.taskRepository.save(taskModel);

    return ResponseHandler.generateResponse(null, HttpStatus.CREATED, task);
  }

}