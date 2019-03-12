package com.pass0word1.demo.springtest;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.pass0word1.demo.springtest.domain.Pet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
@Rollback()
@Transactional
public class SpringtestPetControllerTests {
  final static Logger logger= LoggerFactory.getLogger(SpringtestPetControllerTests.class);

  @Rule
  public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private Gson gson;

  private MockMvc mockMvc;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .apply(documentationConfiguration(this.restDocumentation)).build();
  }

  @BeforeTransaction
  void beforeTransaction() {
    // logic to be executed before a transaction is started
    logger.info("beforeTransaction");
  }

  @AfterTransaction
  void afterTransaction() {
    // logic to be executed after a transaction has ended
    logger.info("afterTransaction");
  }

  @Test
  public void testCreatePet() throws Exception {
    int beforeRowCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "PET");
    Assert.assertEquals(0, beforeRowCount);
    Pet pet = new Pet();
    pet.setName("cat");
    pet.setStatus("available");
    mockMvc.perform(
        post("/pet").contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(pet)))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
        .andDo(document("addPet", requestFields(fieldWithPath("name").description("宠物名字"),
            fieldWithPath("status").description("宠物状态"))));
    int afterRowCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "PET");
    Assert.assertEquals(1, afterRowCount);
  }

  @Test
  public void testGetPetById() throws Exception {
    Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate,"pet"));
    Pet pet = new Pet();
    pet.setName("cat");
    pet.setStatus("available");
    mockMvc.perform(post("/pet").contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(pet))).andExpect(status().isCreated());
    Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"pet"));
    mockMvc.perform(
        get("/pet/{id}", 1)
            .accept(MediaType.APPLICATION_JSON_UTF8)
    ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").isString())
        .andExpect(jsonPath("$.status").value("available"))
        .andDo(print());
  }

  @Test
  public void testPetNotFound() throws Exception {
    mockMvc.perform(
        get("/pet/{id}", 2)
            .accept(MediaType.APPLICATION_JSON_UTF8)
    ).andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void testGetPetWithInvalidId() throws Exception {
    mockMvc.perform(
        get("/pet/{id}", "a")
            .accept(MediaType.APPLICATION_JSON_UTF8)
    )
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testDeletePetById() throws Exception {
    Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate,"PET"));
    Pet pet = new Pet();
    pet.setName("cat");
    pet.setStatus("available");
    String body=mockMvc.perform(post("/pet").contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(pet))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    Integer id=JsonPath.parse(body).read("$.id");
    Assert.assertNotNull(id);
    Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"pet"));
    mockMvc.perform(delete("/pet/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isNoContent());
    Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate,"PET"));
  }
}
