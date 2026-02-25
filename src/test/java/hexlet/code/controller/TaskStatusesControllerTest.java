package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.assertj.core.api.Assertions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    private User testUser;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = taskStatusMapper.map(testTaskStatus);

        var request = post("/api/task_statuses").with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request).andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findByName(testTaskStatus.getName()).orElse(null);
        assertNotNull(taskStatus);
        assertThat(taskStatus.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    public void testDestroy() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId()).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isEqualTo(false);
    }

    @Test
    public void testIndex() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var response = mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk()).andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<TaskStatusDTO> taskStatusesDTOS = om.readValue(body, new TypeReference<>() {
        });
        var actual = taskStatusesDTOS.stream().map(taskStatusMapper::map).toList();
        var expected = taskStatusRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var request = get("/api/task_statuses/" + testTaskStatus.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("name").isEqualTo(testTaskStatus.getName()));
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var taskStatusId = (taskStatusRepository.findByName(testTaskStatus.getName()).orElseThrow()).getId();

        Map<String, String> data = new HashMap<>(Map.of(
                "name", "To Be Updated",
                "slug", "to_be_updated"
        ));

        var request = put("/api/task_statuses/" + taskStatusId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isOk());
        var updateTaskStatus = taskStatusRepository.findById(taskStatusId).orElseThrow();
        assertThat(updateTaskStatus.getName()).isEqualTo("To Be Updated");
        assertThat(updateTaskStatus.getSlug()).isEqualTo("to_be_updated");
    }

    @Test
    public void testUpdateNotAuthorized() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var taskStatusId = (taskStatusRepository.findByName(testTaskStatus.getName()).orElseThrow()).getId();

        Map<String, String> data = new HashMap<>(Map.of(
                "name", "To Be Updated",
                "slug", "to_be_updated"
        ));

        var request = put("/api/task_statuses/" + taskStatusId).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnauthorized());

        var updateTaskStatus = taskStatusRepository.findById(taskStatusId).orElseThrow();
        assertThat(updateTaskStatus.getName()).isNotEqualTo("To Be Updated");
        assertThat(updateTaskStatus.getSlug()).isNotEqualTo("to_be_updated");
    }

    @Test
    public void testUpdateFailed() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var taskStatusId = (taskStatusRepository.findByName(testTaskStatus.getName()).orElseThrow()).getId();

        Map<String, String> data = new HashMap<>(Map.of(
                "name", "",
                "slug", ""
        ));

        var request = put("/api/task_statuses/" + taskStatusId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());
        var updateTaskStatus = taskStatusRepository.findById(taskStatusId).orElseThrow();
        assertThat(updateTaskStatus.getName()).isNotEqualTo("");
        assertThat(updateTaskStatus.getSlug()).isNotEqualTo("");
    }
}
