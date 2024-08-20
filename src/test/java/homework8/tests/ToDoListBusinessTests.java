package homework8.tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import homework8.service.TaskClass;
import homework8.service.TestHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;

public class ToDoListBusinessTests {
    public static final String URL = "https://todo-app-sky.herokuapp.com";
    private TestHelper helper;
    private HttpClient client;

    @BeforeEach
    public void setUp() {
        helper = new TestHelper();
        client = HttpClientBuilder.create().build();
    }

    @Test
    @DisplayName("Получение списка дел")
    public void getTasks() throws IOException {
        helper.createTask();
        List<TaskClass> allTasks = helper.getTasks();
        assertThat(allTasks.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Проверка создания задачи")
    public void createTask() throws IOException {
        TaskClass newTask = helper.createTask();
        int id = newTask.getId();
        String title = newTask.getTitle();

        List<TaskClass> allTasks = helper.getTasks();

        TaskClass taskForAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == id) {
                taskForAssert = task;
            }
        }
        assertThat(taskForAssert.getTitle()).isEqualTo(title);
        assertThat(taskForAssert.getId()).isEqualTo(id);
        assertThat(taskForAssert.isCompleted()).isNull();
    }

    @Test
    @DisplayName("Редактирование задачи")
    public void renameTask() throws IOException {
        TaskClass newTask = helper.createTask();
        int taskId = newTask.getId();
        String taskTitle = newTask.getTitle();

        TaskClass taskWithNewTitle = helper.renameTaskInList(taskId, taskTitle);
        List<TaskClass> allTasks = helper.getTasks();
        TaskClass taskForAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == taskId) {
                taskForAssert = task;
            }
        }
        assertThat(taskForAssert.getTitle()).isEqualTo(taskWithNewTitle.getTitle());
        assertThat(taskForAssert.getId()).isEqualTo(taskWithNewTitle.getId());
    }

    @Test
    @DisplayName("Проставление здаче статуса Выполнена")
    public void setComplitedTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();

        helper.doCompleteTask(taskId);
        List<TaskClass> allTasks = helper.getTasks();

        TaskClass taskToAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == taskId) {
                taskToAssert = task;
            }
        }
        assertThat(taskToAssert.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Удаление задачи из списка")
    public void deleteTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();

        helper.deleteTaskFromList(taskId);
        List<TaskClass> allTasks = helper.getTasks();

        TaskClass taskToAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == taskId) {
                taskToAssert = task;
            }
        }
        assertThat(taskToAssert).isNull();
    }

    @Test
    @DisplayName("Добавление задачи с пустым полем title")
    public void createTaskWithoutTitle() throws IOException {
        HttpPost request = new HttpPost(URL);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper();
        TaskClass taskData = mapper.readValue(responseData, TaskClass.class);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(taskData.getTitle()).isNull();
        assertThat(taskData.isCompleted()).isNull();
    }

    @Test
    @DisplayName("Удаление несуществующей задачи")
    public void deleteNonExistentTask() throws IOException {
        int idForDeleteTask = -100;
        HttpResponse response = helper.deleteTaskFromList(idForDeleteTask);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
    }

    @Test
    @DisplayName("Проставление статуса Выполнена уже выполненной задаче")
    public void renameNonExistentTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();

        helper.doCompleteTask(taskId);
        helper.doCompleteTask(taskId);
        List<TaskClass> allTasks = helper.getTasks();


        TaskClass taskToAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == taskId) {
                taskToAssert = task;
            }
        }
        assertThat(taskToAssert.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Изменение статуса Выполнена обратно на Невыполнена")
    public void rollbackStatus() throws IOException {
        TaskClass newTask = helper.createTask();
        int id = newTask.getId();
        helper.doCompleteTask(id);
        helper.rollbackCompletedTask(id);

        List<TaskClass> allTasks = helper.getTasks();

        TaskClass taskToAssert = null;
        for (TaskClass task : allTasks) {
            if (task.getId() == id) {
                taskToAssert = task;
            }
        }
        assertThat(taskToAssert.isCompleted()).isFalse();
    }
}