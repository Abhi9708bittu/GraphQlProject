package com.example.GraphQlProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@SpringBootTest
@AutoConfigureMockMvc
public class UserGraphQLControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearDb() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:graphql.db")) {
            conn.createStatement().executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void createUser_and_queryUser() throws Exception {
        // Create user
        String mutation = "mutation { createUser(id: \"1\", name: \"Alice\") { id name } }";
        var mvcResult1 = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"" + mutation.replace("\"", "\\\"") + "\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createUser.id").value("1"))
                .andExpect(jsonPath("$.data.createUser.name").value("Alice"));

        // Query users
        String query = "{ users { id name } }";
        var mvcResult2 = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"" + query.replace("\"", "\\\"") + "\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users[0].id").value("1"))
                .andExpect(jsonPath("$.data.users[0].name").value("Alice"));
    }

    @Test
    void updateUser_and_deleteUser() throws Exception {
        // Create user
        String mutation = "mutation { createUser(id: \"2\", name: \"Bob\") { id name } }";
        var mvcResult3 = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"" + mutation.replace("\"", "\\\"") + "\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createUser.id").value("2"))
                .andExpect(jsonPath("$.data.createUser.name").value("Bob"));

        // Update user
        String update = "mutation { updateUser(id: \"2\", name: \"Bobby\") { id name } }";
        var mvcResult4 = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"" + update.replace("\"", "\\\"") + "\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult4))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateUser.id").value("2"))
                .andExpect(jsonPath("$.data.updateUser.name").value("Bobby"));

        // Delete user
        String del = "mutation { deleteUser(id: \"2\") { id name } }";
        var mvcResult5 = mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\": \"" + del.replace("\"", "\\\"") + "\"}"))
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteUser.id").value("2"))
                .andExpect(jsonPath("$.data.deleteUser.name").value("Bobby"));
    }
} 