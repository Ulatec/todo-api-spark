package com.teamtreehouse.techdegrees;


import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.exception.ApiError;
import com.teamtreehouse.techdegrees.model.Todo;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {

    private static Gson gson;
    private static Sql2o sql2o;
    private static Sql2oTodoDao todoDao;


    public static void main(String[] args) {
        String datasource = "jdbc:h2:~/todos.db";
        if(args.length > 0){
            if(args.length != 2){
                System.out.println("Java API <port> <datasource>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            datasource = args[1];
        }
        staticFileLocation("/public");
        gson = new Gson();
        sql2o = new Sql2o(String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", datasource), "", "");
        todoDao = new Sql2oTodoDao(sql2o);

        /* GET ALL TODOS */
        get("/api/v1/todos", (req, res) -> {
            res.status(200);
            return todoDao.findAll();
        }, gson::toJson);

        /* GET EXISTING TODO */
        get("/api/v1/todos/:id", (req,res) -> {
            int id = Integer.parseInt(req.params("id"));
            Todo todo = todoDao.findById(id);
            if(todo == null){
                throw new ApiError(404, "Todo not found");
            }
            res.status(200);
            return todo;
        }, gson::toJson);

        /* DELETE EXISTING TODO */
        delete("/api/v1/todos/:id", (req, res) ->{
            int id = Integer.parseInt(req.params("id"));
            Todo todo = todoDao.findById(id);
            if(todo == null){
                throw new ApiError(404, "Todo not found");
            }
            todoDao.delete(todo);
            res.status(204);
            return todo;
        }, gson::toJson);

        /* Update existing TODO */
        put("/api/v1/todos/:id", (req, res) ->{
            int id = Integer.parseInt(req.params("id"));
            Todo existingTodo = todoDao.findById(id);
            existingTodo.setCompleted(gson.fromJson(req.body(), Todo.class).isCompleted());
            existingTodo.setName(gson.fromJson(req.body(), Todo.class).getName());
            todoDao.update(existingTodo);
            res.status(200);
            return existingTodo;
        }, gson::toJson);

        /* POST NEW TODOS */
        post("/api/v1/todos", (req, res) -> {
            Todo todo =  gson.fromJson(req.body(), Todo.class);
            todoDao.add(todo);
            res.status(201);
            return todo;
        }, gson::toJson);

        /* API ERROR HANDLER */
        exception(ApiError.class, (exc, req, res) ->{
            ApiError err = (ApiError) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json");
            res.status(err.getStatus());
            res.body(gson.toJson(jsonMap));
        });
        
        after((req, res) -> {
            res.type("application/type");
        } );


    }



}
