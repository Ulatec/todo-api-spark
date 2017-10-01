package com.teamtreehouse.techdegrees.dao;

import com.teamtreehouse.techdegrees.exception.TodoException;
import com.teamtreehouse.techdegrees.model.Todo;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oTodoDao implements TodoDao {

    private final Sql2o sql2o;
    public Sql2oTodoDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Todo todo) throws TodoException{
        String sql = "INSERT INTO todos(name) VALUES (:name)";
        try(Connection con = sql2o.open()){
            int id = (int) con.createQuery(sql)
                    .bind(todo)
                    .executeUpdate()
                    .getKey();
            todo.setId(id);
        }catch (Sql2oException ex){
            throw new TodoException(ex, ex.getMessage());
        }
    }

    @Override
    public List<Todo> findAll() {
        try (Connection con = sql2o.open()){
            return con.createQuery("Select * FROM todos")
                    .executeAndFetch(Todo.class);
        }
    }

    @Override
    public Todo findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("Select * FROM todos where id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Todo.class);
        }
    }

    @Override
    public void update(Todo todo) {
        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE todos SET name = :name, completed = :completed WHERE id = :id")
                    .addParameter("id", todo.getId())
                    .addParameter("completed", todo.isCompleted())
                    .addParameter("name", todo.getName())
                    .executeUpdate();
        }
    }

    @Override
    public void delete(Todo todo){
        try (Connection con = sql2o.open()) {
            con.createQuery("Delete FROM todos where id = :id")
                    .addParameter("id", todo.getId())
                    .executeUpdate();
        }
    }
}
