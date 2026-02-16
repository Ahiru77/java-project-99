package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    private JwtRequestPostProcessor token;

    private Task testTask;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        taskRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = taskMapper.map(testTask);

        var request = post("/api/tasks").with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request).andExpect(status().isCreated());

        var task = taskRepository.findByName(testTask.getName()).orElse(null);
        assertNotNull(task);
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
    }

    @Test
    public void testDestroy() throws Exception {
        taskRepository.save(testTask);
        var request = delete("/api/tasks/" + testTask.getId()).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }

    @Test
    public void testIndex() throws Exception {
        taskRepository.save(testTask);

        var response = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk()).andReturn()
                .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).and(v -> {
            v.node("[0].id").isEqualTo(testTask.getId());
            v.node("[0].title").isEqualTo(testTask.getName());
            v.node("[0].index").isEqualTo(testTask.getIndex());
            v.node("[0].content").isEqualTo(testTask.getDescription());
            v.node("[0].status").isEqualTo(testTask.getTaskStatus().getSlug());
            v.node("[0].assignee_id").isEqualTo(testTask.getAssignee().getId());
            v.node("[0].createdAt").isEqualTo(testTask.getCreatedAt().toString());
            v.node("[0].taskLabelIds")
                    .isEqualTo(testTask.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        });
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);

        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(v -> {
            v.node("id").isEqualTo(testTask.getId());
            v.node("title").isEqualTo(testTask.getName());
            v.node("index").isEqualTo(testTask.getIndex());
            v.node("content").isEqualTo(testTask.getDescription());
            v.node("status").isEqualTo(testTask.getTaskStatus().getSlug());
            v.node("assignee_id").isEqualTo(testTask.getAssignee().getId());
            v.node("createdAt").isEqualTo(testTask.getCreatedAt().toString());
        });
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);
        var taskId = (taskRepository.findByName(testTask.getName()).orElseThrow()).getId();

        var anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);
        var anotherTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(anotherTaskStatus);
        var anotherLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(anotherLabel);

        var data = Map.of(
                "index", 25,
                "assignee_id", anotherUser.getId(),
                "title", "Awesome task",
                "content", "It's about the awesome task",
                "status", anotherTaskStatus.getSlug(),
                "taskLabelIds", Set.of(anotherLabel.getId())
        );

        var request = put("/api/tasks/" + taskId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isOk());

        var updateTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updateTask.getIndex()).isEqualTo(data.get("index"));
        assertThat(updateTask.getAssignee().getId()).isEqualTo(data.get("assignee_id"));
        assertThat(updateTask.getName()).isEqualTo(data.get("title"));
        assertThat(updateTask.getDescription()).isEqualTo(data.get("content"));
        assertThat(updateTask.getTaskStatus().getSlug()).isEqualTo(data.get("status"));
        assertThat(updateTask.getLabels().stream().map(Label::getId).collect(Collectors.toSet()))
                .isEqualTo(data.get("taskLabelIds"));
    }

    @Test
    public void testUpdateFailed() throws Exception {
        taskRepository.save(testTask);
        var taskId = (taskRepository.findByName(testTask.getName()).orElseThrow()).getId();

        var anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);
        var anotherTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(anotherTaskStatus);
        var anotherLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(anotherLabel);

        var data = Map.of(
                "index", 25,
                "assignee_id", anotherUser.getId(),
                "title", "",
                "content", "It's about the awesome task",
                "status", anotherTaskStatus.getSlug(),
                "taskLabelIds", Set.of(anotherLabel.getId())
        );

        var request = put("/api/tasks/" + taskId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());

        var updateTask = taskRepository.findById(taskId).orElseThrow();
        assertThat(updateTask.getName()).isNotEqualTo(data.get("title"));
        assertThat(updateTask.getTaskStatus().getSlug()).isNotEqualTo(data.get("status"));
    }
}
