package com.example.demo.service.Services;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.Ulti.AppUtils;
import com.example.demo.models.Todo;
import com.example.demo.payload.request.TodoRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ITodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class TodoService implements ITodoService {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> completeTodo(Long id, UserDetailsImpl currentUser) {
        var isExist = todoRepository.findById(id);
        if (isExist.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Todo id is not found"),
                    HttpStatus.NOT_FOUND);
        Todo todo = isExist.get();
        if (todo.getUser().getId().equals(currentUser.getId())) {
            todo.setCompleted(Boolean.TRUE);
            var res = todoRepository.save(todo);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> unCompleteTodo(Long id, UserDetailsImpl currentUser) {
        var isExist = todoRepository.findById(id);
        if (isExist.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Todo id is not found"),
                    HttpStatus.NOT_FOUND);
        Todo todo = isExist.get();
        if (todo.getUser().getId().equals(currentUser.getId())) {
            todo.setCompleted(Boolean.FALSE);
            var res = todoRepository.save(todo);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new MessageResponse("Error: You have no permission on this resource!"),
                HttpStatus.FORBIDDEN);
    }

    @Override
    public PagedResponse<Todo> getAllTodos(UserDetailsImpl currentUser, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.CREATED_AT);

        Page<Todo> todos = todoRepository.findAllByUserId(currentUser.getId(), pageable);

        List<Todo> content = todos.getNumberOfElements() == 0 ? Collections.emptyList() : todos.getContent();

        return new PagedResponse<>(content, todos.getNumber(), todos.getSize(), todos.getTotalElements(),
                todos.getTotalPages(), todos.isLast());
    }

    @Override
    public ResponseEntity<?> addTodo(TodoRequest todo, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        Todo t = new Todo();
        t.setCompleted(todo.getCompleted());
        t.setCreatedBy(user.getId());
        t.setTitle(todo.getTitle());
        t.setUser(user);
        var res = todoRepository.save(t);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getTodo(Long id, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        var isExist = todoRepository.findById(id);
        if (isExist.isEmpty())
            return new ResponseEntity<>(
                    new MessageResponse("Error: Todo id is not found"),
                    HttpStatus.NOT_FOUND);
        Todo todo = isExist.get();
        if (todo.getUser().getId().equals(user.getId())
            || AppUtils.isAdmin(currentUser)
        ) {
            return new ResponseEntity<>(todo, HttpStatus.OK);
        }
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"),
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> updateTodo(Long id, TodoRequest newTodo, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        var isExist = todoRepository.findById(id);
        if (isExist.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: Todo id is not found"),
                HttpStatus.NOT_FOUND);
        Todo todo = isExist.get();
        if (todo.getUser().getId().equals(user.getId())) {
            todo.setTitle(newTodo.getTitle());
            todo.setCompleted(newTodo.getCompleted());
            todo.setUpdatedBy(user.getId());
            todo.setUpdatedAt(Instant.now());
            var res = todoRepository.save(todo);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"),
            HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<?> deleteTodo(Long id, UserDetailsImpl currentUser) {
        var user = userRepository.findById(currentUser.getId()).get();
        var isExist = todoRepository.findById(id);
        if (isExist.isEmpty())
            return new ResponseEntity<>(
                new MessageResponse("Error: Todo id is not found"),
                HttpStatus.NOT_FOUND);
        Todo todo = isExist.get();

        if (todo.getUser().getId().equals(user.getId())
            || AppUtils.isAdmin(currentUser)
        ) {
            todoRepository.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Delete todo successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(
            new MessageResponse("Error: You have no permission on this resource!"),
            HttpStatus.FORBIDDEN);
    }

}
