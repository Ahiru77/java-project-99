package hexlet.code.controller;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.User;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelsControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelMapper labelMapper;

    private JwtRequestPostProcessor token;

    private Label testLabel;

    private User testUser;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = labelMapper.map(testLabel);

        var request = post("/api/labels").with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request).andExpect(status().isCreated());

        var label = labelRepository.findByName(testLabel.getName()).orElse(null);
        assertNotNull(label);
        assertThat(label.getName()).isEqualTo(testLabel.getName());
    }

    @Test
    public void testDestroy() throws Exception {
        labelRepository.save(testLabel);
        var request = delete("/api/labels/" + testLabel.getId()).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());

        assertThat(labelRepository.existsById(testLabel.getId())).isEqualTo(false);
    }

    @Test
    public void testIndex() throws Exception {
        labelRepository.save(testLabel);

        var response = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk()).andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<LabelDTO> labelsDTOS = om.readValue(body, new TypeReference<>() {
        });
        var actual = labelsDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        labelRepository.save(testLabel);

        var request = get("/api/labels/" + testLabel.getId()).with(jwt());
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(v -> v.node("name").isEqualTo(testLabel.getName()));
    }

    @Test
    public void testUpdate() throws Exception {
        labelRepository.save(testLabel);
        var labelId = (labelRepository.findByName(testLabel.getName()).orElseThrow()).getId();

        var data = new LabelUpdateDTO();
        data.setName(JsonNullable.of("New label"));

        var request = put("/api/labels/" + labelId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isOk());
        var updateLabel = labelRepository.findById(labelId).orElseThrow();
        assertThat(updateLabel.getName()).isEqualTo("New label");
    }

    @Test
    public void testUpdateNotAuthorized() throws Exception {
        labelRepository.save(testLabel);

        var labelId = (labelRepository.findByName(testLabel.getName()).orElseThrow()).getId();

        var data = new LabelUpdateDTO();
        data.setName(JsonNullable.of("New label"));

        var request = put("/api/labels/" + labelId).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isUnauthorized());

        var updateLabel = labelRepository.findById(labelId).orElseThrow();
        assertThat(updateLabel.getName()).isNotEqualTo("New label");
    }

    @Test
    public void testUpdateFailed() throws Exception {
        labelRepository.save(testLabel);
        var labelId = (labelRepository.findByName(testLabel.getName()).orElseThrow()).getId();

        var data = new LabelUpdateDTO();
        data.setName(JsonNullable.of("Up"));

        var request = put("/api/labels/" + labelId)
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isBadRequest());
        var updateLabel = labelRepository.findById(labelId).orElseThrow();
        assertThat(updateLabel.getName()).isNotEqualTo("Up");
    }
}
