package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exception.TodoException;
import com.teamtreehouse.techdegrees.model.Todo;

import java.util.List;

public interface TodoDao {
    void add(Todo todo) throws TodoException;
    List<Todo> findAll();
    Todo findById(int id);
    void update(Todo todo);
    void delete(Todo todo);
}
