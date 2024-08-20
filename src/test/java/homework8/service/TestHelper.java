package homework8.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

import java.util.List;

public class TestHelper {
    private static final String URL = "https://todo-app-sky.herokuapp.com";
    private CloseableHttpClient client;

    public TestHelper() {
        this.client = HttpClients.createDefault();
    }


    public TaskClass createTask() throws IOException {
        HttpPost request = new HttpPost(URL);
        String body = "{\"title\": \"My new task\"}";
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());
        request.releaseConnection();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseData, TaskClass.class);
    }

    public List<TaskClass> getTasks() throws IOException {
        HttpGet request = new HttpGet(URL);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());
        request.releaseConnection();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseData, new TypeReference<>() {
        });
    }

    public void doCompleteTask(int id) throws IOException {
        HttpPatch request = new HttpPatch(URL + "/" + id);
        StringEntity entity = new StringEntity("{\"completed\":true}", ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        client.execute(request);
        request.releaseConnection();
    }

    public void rollbackCompletedTask(int id) throws IOException {
        HttpPatch request = new HttpPatch(URL + "/" + id);
        StringEntity entity = new StringEntity("{\"completed\":false}", ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        client.execute(request);
        request.releaseConnection();
    }

    public HttpResponse deleteTaskFromList(int id) throws IOException {
        HttpDelete request = new HttpDelete(URL + "/" + id);
        HttpResponse response = client.execute(request);
        request.releaseConnection();
        return response;
    }


    public TaskClass renameTaskInList(int id, String title) throws IOException {

        HttpPatch request = new HttpPatch(URL + "/" + id);
        String body = "{\"id\": \"%s\", \"title\": \"%s\", \"completed\": \"null\"}".formatted(id, title + "1");
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        String responseData = EntityUtils.toString(response.getEntity());
        request.releaseConnection();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseData, TaskClass.class);
    }
}
