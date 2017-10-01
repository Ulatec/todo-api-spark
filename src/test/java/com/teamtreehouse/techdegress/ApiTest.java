package com.teamtreehouse.techdegress;

import com.google.gson.Gson;
import com.teamtreehouse.techdegrees.App;
import com.teamtreehouse.techdegrees.dao.Sql2oTodoDao;
import com.teamtreehouse.techdegrees.model.Todo;
import com.teamtreehouse.techdegress.testing.ApiClient;
import com.teamtreehouse.techdegress.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiTest {

    public static final String PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection conn;
    private ApiClient client;
    private Gson gson;
    private Sql2oTodoDao todoDao;


    @BeforeClass
    public static void startServer(){
        String[] args = {PORT, TEST_DATASOURCE};
        App.main(args);
    }
    @AfterClass
    public static void stopServer(){
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        todoDao = new Sql2oTodoDao(sql2o);
        conn = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
    }


    @Test
    public void coursesCanBeAccessedById() throws Exception {
        Todo todo = newTodo();
        todoDao.add(todo);

        ApiResponse res = client.request("GET",
                "/api/v1/todos/" + todo.getId());
        Todo retrieved = gson.fromJson(res.getBody(), Todo.class);
        assertEquals(todo, retrieved);
    }
    @Test
    public void addingTodosReturnsCreatedStatus() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        ApiResponse res = client.request("POST", "/api/v1/todos", gson.toJson(values));
        assertEquals(201, res.getStatus());
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() throws Exception {
        ApiResponse res = client.request("GET", "/api/v1/todos/1337");

        assertEquals(404, res.getStatus());
    }

    @Test
    public void updatingTodoProperlyUpdatesValues() throws Exception {
        Todo newTodo = newTodo();
        todoDao.add(newTodo);
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Modified Test");
        values.put("completed", false);
        ApiResponse res = client.request("PUT", String.format("/api/v1/todos/%d", newTodo.getId()), gson.toJson(values) );
        Todo retrieved = gson.fromJson(res.getBody(), Todo.class);
        assertEquals("Modified Test", retrieved.getName());
    }

    @Test
    public void updatingUnknownTodoThrowsError() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "new test name");
        values.put("completed", true);
        ApiResponse res = client.request("PUT", "/api/v1/todos/1337", gson.toJson(values));
        assertEquals(500, res.getStatus());
    }
    @Test
    public void deletingTodoReturnsProperStatus() throws Exception {
        Todo newTodo = newTodo();
        todoDao.add(newTodo);

        ApiResponse res = client.request("DELETE", String.format("/api/v1/todos/%d",newTodo.getId()));
        assertEquals(204, res.getStatus());
    }

    @Test
    public void allTodosAreReturned() throws Exception{
        Todo Todo1 = new Todo("Test1");
        Todo Todo2 = new Todo("Test2");
        todoDao.add(Todo1);
        todoDao.add(Todo2);
        ApiResponse res = client.request("GET", "/api/v1/todos");
        Todo[] retrieved = gson.fromJson(res.getBody(), Todo[].class);
        assertEquals(2, retrieved.length);
    }


    @After
    public void tearDown() throws Exception {
        conn.close();
    }
    private Todo newTodo() {
        return new Todo("Test");
    }
}