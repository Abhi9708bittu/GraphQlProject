package com.example.GraphQlProject;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserGraphQLController {

    private static final String DB_URL = "jdbc:sqlite:graphql.db";

    @QueryMapping
    public List<User> users() throws Exception {
        System.out.println("users() called");
        List<User> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs.next()) {
                result.add(new User(rs.getString("id"), rs.getString("name")));
            }
        }
        return result;
    }

    @MutationMapping
    public User createUser(@Argument String id, @Argument String name) throws Exception {
        System.out.println("createUser called with id=" + id + ", name=" + name);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)");
            ps.setString(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        }
        return getUserById(id);
    }

    @MutationMapping
    public User updateUser(@Argument String id, @Argument String name) throws Exception {
        System.out.println("updateUser called with id=" + id + ", name=" + name);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET name = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setString(2, id);
            ps.executeUpdate();
        }
        return getUserById(id);
    }

    @MutationMapping
    public User deleteUser(@Argument String id) throws Exception {
        System.out.println("deleteUser called with id=" + id);
        User user = getUserById(id);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setString(1, id);
            ps.executeUpdate();
        }
        return user;
    }

    private User getUserById(String id) throws Exception {
        System.out.println("getUserById called with id=" + id);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("id"), rs.getString("name"));
            }
        }
        return null;
    }
} 