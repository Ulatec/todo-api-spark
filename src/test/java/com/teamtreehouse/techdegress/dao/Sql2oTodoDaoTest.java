package com.teamtreehouse.techdegress.dao;

import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oTodoDaoTest {

    private Gson gson;
    private Sql2o sql2o;
    private Sql2oTodoDao todoDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception{
        String datasource = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        gson = new Gson();
        sql2o = new Sql2o(datasource, "", "");
        todoDao = new Sql2oTodoDao(sql2o);
        conn = sql2o.open();
    }

    private Todo newTodo() {
        return new Todo("Test");
    }

    @Test
    public void addingTodoSetsId() throws Exception {
        Todo newTodo = newTodo();
        int originalId = newTodo.getId();
        todoDao.add(newTodo);
        assertNotEquals(originalId, newTodo.getId());
    }
    @Test
    public void existingTodoCanBeFoundById() throws Exception {
        Todo todo = newTodo();
        todoDao.add(todo);
        Todo foundTodo = todoDao.findById(todo.getId());
        assertEquals(todo, foundTodo);
    }

    @Test
    public void allTodosAreReturnedFromFindAll() throws Exception {
        Todo todo1 = new Todo("Test 1");
        Todo todo2 = new Todo("Test 2");
        todoDao.add(todo1);
        todoDao.add(todo2);

        assertEquals(2, todoDao.findAll().size());
    }
    @Test
    public void todosAreProperlyDeleted() throws Exception {
        Todo todo = newTodo();
        todoDao.add(todo);
        todoDao.delete(todo);
        List<Todo> todos = todoDao.findAll();
        assertEquals(0, todos.size());
    }
    @Test
    public void todoValuesAreProperlyUpdated() throws Exception {
        Todo todo = newTodo();
        todoDao.add(todo);
        Todo retrievedTodo = todoDao.findById(todo.getId());
        todo.setName("Test1");
        todo.setCompleted(true);
        todoDao.update(todo);
        assertNotEquals(todo, retrievedTodo);
    }
    @Test
    public void noTodosReturnsEmpty() throws Exception {
        assertEquals(0, todoDao.findAll().size());
    }

    @After
    public void tearDown()throws Exception{
        conn.close();
    }



}
