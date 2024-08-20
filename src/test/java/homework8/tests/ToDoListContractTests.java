package homework8.tests;

import homework8.service.TaskClass;
import homework8.service.TestHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


import java.io.IOException;


public class ToDoListContractTests {
    public static final String URL = "https://todo-app-sky.herokuapp.com";
    private HttpClient client;
    private TestHelper helper;

    @BeforeEach
    public void setUp() {
        client = HttpClientBuilder.create().build();
        helper = new TestHelper();
    }

    @Test
    @DisplayName("Получение списка дел")
    public void getListOfTasks() throws IOException {
        HttpGet request = new HttpGet(URL);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(responseData.startsWith("[")).isTrue();
        assertThat(responseData.endsWith("]")).isTrue();
    }

    @Test
    @DisplayName("Создание нового дела")
    public void createNewTask() throws IOException {
        HttpPost request = new HttpPost(URL);
        String body = "{\"title\": \"My new task\"}";
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(responseData.startsWith("{")).isTrue();
        assertThat(responseData.endsWith("}")).isTrue();
    }

    @Test
    @DisplayName("Редактирование названия задачи")
    public void renameTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();
        String taskTitle = taskData.getTitle();

        HttpPatch request = new HttpPatch(URL + "/" + taskId);
        String body = "{\"id\": \"%s\", \"title\": \"%s\", \"completed\": \"null\"}".formatted(taskId, taskTitle + "1");
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(responseData.startsWith("{")).isTrue();
        assertThat(responseData.endsWith("}")).isTrue();
    }

    @Test
    @DisplayName("Проставление здаче статуса Выполнена")
    public void setComplitedTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();

        HttpPatch request = new HttpPatch(URL + "/" + taskId);
        StringEntity entity = new StringEntity("{\"completed\":true}", ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(responseData.startsWith("{")).isTrue();
        assertThat(responseData.endsWith("}")).isTrue();
    }

    @Test
    @DisplayName("Удаление задачи из списка")
    public void deleteTask() throws IOException {
        TaskClass taskData = helper.createTask();
        int taskId = taskData.getId();

        HttpDelete request = new HttpDelete(URL + "/" + taskId);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getHeaders("Content-Type").length).isEqualTo(1);
        assertThat(response.getHeaders("Content-Type")[0].getValue()).isEqualTo("application/json; charset=utf-8");
        assertThat(responseData).isEqualTo("\"todo was deleted\"");
    }
}
