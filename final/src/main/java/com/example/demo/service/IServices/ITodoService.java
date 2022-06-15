package com.example.demo.service.IServices;

import org.springframework.http.ResponseEntity;

import com.example.demo.models.Todo;
import com.example.demo.payload.request.TodoRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;

public interface ITodoService {

	ResponseEntity<?> completeTodo(Long id, UserDetailsImpl currentUser);

	ResponseEntity<?> unCompleteTodo(Long id, UserDetailsImpl currentUser);

	PagedResponse<Todo> getAllTodos(UserDetailsImpl currentUser, int page, int size);

	ResponseEntity<?> addTodo(TodoRequest todo, UserDetailsImpl currentUser);

	ResponseEntity<?> getTodo(Long id, UserDetailsImpl currentUser);

	ResponseEntity<?> updateTodo(Long id, TodoRequest newTodo, UserDetailsImpl currentUser);

	ResponseEntity<?> deleteTodo(Long id, UserDetailsImpl currentUser);

}
