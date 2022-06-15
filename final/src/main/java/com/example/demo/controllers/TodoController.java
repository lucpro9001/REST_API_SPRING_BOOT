package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.validation.Valid;

import com.example.demo.Ulti.AppConstants;
import com.example.demo.models.Todo;
import com.example.demo.payload.request.TodoRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.security.service.UserDetailsImpl;
import com.example.demo.service.IServices.ITodoService;

@RestController
@RequestMapping("/api/todos")
@SecurityRequirement(name = "peniiz")
@PreAuthorize("isAuthenticated()")
public class TodoController {

	@Autowired
	private ITodoService todoService;

	@GetMapping()
	public ResponseEntity<PagedResponse<Todo>> getAllTodos(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		PagedResponse<Todo> response = todoService.getAllTodos(currentUser, page, size);

		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<?> addTodo(@Valid @RequestBody TodoRequest todo) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.addTodo(todo, currentUser);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTodo(@PathVariable(value = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.getTodo(id, currentUser);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateTodo(@PathVariable(value = "id") Long id, @Valid @RequestBody TodoRequest newTodo) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.updateTodo(id, newTodo, currentUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTodo(@PathVariable(value = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.deleteTodo(id, currentUser);
	}

	@PutMapping("/{id}/complete")
	public ResponseEntity<?> completeTodo(@PathVariable(value = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.completeTodo(id, currentUser);
	}

	@PutMapping("/{id}/unComplete")
	public ResponseEntity<?> unCompleteTodo(@PathVariable(value = "id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (UserDetailsImpl) auth.getPrincipal();
		return todoService.unCompleteTodo(id, currentUser);
	}
}
