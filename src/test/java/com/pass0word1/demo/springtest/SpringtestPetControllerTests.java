package com.pass0word1.demo.springtest;

import com.google.gson.Gson;
import com.pass0word1.demo.springtest.domain.Pet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class SpringtestPetControllerTests {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private Gson gson;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void testCreatePet() throws Exception {
    Pet pet = new Pet();
    pet.setName("cat");
    pet.setStatus("available");
    mockMvc.perform(
        post("/pet").contentType(MediaType.APPLICATION_JSON_UTF8).content(gson.toJson(pet)))
        .andDo(print())
        .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber());
  }

  @Test
  public void testGetPetById() throws Exception {
    testCreatePet();
    mockMvc.perform(
        get("/pet/{id}", 1)
            .accept(MediaType.APPLICATION_JSON_UTF8)
    ).andDo(print()).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").isString())
        .andExpect(jsonPath("$.status").value("available"))
    ;
  }

  @Test
  public void testPetNotFound() throws Exception {
    mockMvc.perform(
        get("/pet/{id}", 2)
            .accept(MediaType.APPLICATION_JSON_UTF8)
    )
        .andDo(print())
        .andExpect(status().isNotFound());
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
    mockMvc.perform(delete("/pet/{id}", 1).accept(MediaType.APPLICATION_JSON_UTF8))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
