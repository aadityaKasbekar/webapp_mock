package com.cloudcomputing.movieRetrievalWebApp.dao;

import com.cloudcomputing.movieRetrievalWebApp.MovieRetrievalWebAppApplication;
import com.cloudcomputing.movieRetrievalWebApp.dao.implementation.UserDAOImpl;
import com.cloudcomputing.movieRetrievalWebApp.model.User;
import com.cloudcomputing.movieRetrievalWebApp.repository.UserRepo;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MovieRetrievalWebAppApplication.class)
@Transactional
public class UserDaoImplIntegrationTest {

  @Autowired
  private UserDAOImpl userDAOImpl;

  @MockBean
  private UserRepo userRepo;

  private User sampleUser;

  @BeforeEach
  public void setUp() {
    // Setup a sample user object for testing
    sampleUser = new User();
    sampleUser.setEmailAddress("john.doe@example.com");
    sampleUser.setPassword("password123");
    sampleUser.setFirstName("John");
    sampleUser.setLastName("Doe");
  }

  @Test
  public void testGetAllUsers() {
    // Arrange
    when(userRepo.findAll()).thenReturn(Collections.singletonList(sampleUser));

    // Act
    var users = userDAOImpl.getAllUsers();

    // Assert
    assertNotNull(users);
    assertEquals(1, users.size());
    assertEquals("john.doe@example.com", users.get(0).getEmailAddress());

    // Verify that the method was called once
    verify(userRepo, times(1)).findAll();
  }

  @Test
  public void testGetUserById_userExists() {
    // Arrange
    when(userRepo.findById(anyLong())).thenReturn(Optional.of(sampleUser));

    // Act
    Optional<User> foundUser = userDAOImpl.getUserById(1L);

    // Assert
    assertTrue(foundUser.isPresent());
    assertEquals("john.doe@example.com", foundUser.get().getEmailAddress());
  }

  @Test
  public void testGetUserById_userDoesNotExist() {
    // Arrange
    when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

    // Act
    Optional<User> foundUser = userDAOImpl.getUserById(1L);

    // Assert
    assertFalse(foundUser.isPresent());
  }

  @Test
  public void testCreateUser_userDoesNotExist() {
    // Arrange
    when(userRepo.findAll()).thenReturn(Collections.emptyList());
    when(userRepo.save(any(User.class))).thenReturn(sampleUser);

    // Act
    User createdUser = userDAOImpl.createUser(sampleUser);

    // Assert
    assertNotNull(createdUser);
    assertEquals("john.doe@example.com", createdUser.getEmailAddress());
    verify(userRepo, times(1)).save(any(User.class));
  }

  @Test
  public void testUpdateUser_userExists() {
    // Arrange
    User updatedUser = new User("john.doe@example.com", "newpassword", "John", "Smith");
    when(userRepo.findAll()).thenReturn(Collections.singletonList(sampleUser));
    when(userRepo.save(any(User.class))).thenReturn(updatedUser);

    // Act
    User result = userDAOImpl.updateUser("john.doe@example.com", updatedUser);

    // Assert
    assertEquals("newpassword", result.getPassword());
    assertEquals("Smith", result.getLastName());
  }

  @Test
  public void testDeleteUser_userExists() {
    // Arrange
    when(userRepo.findAll()).thenReturn(Collections.singletonList(sampleUser));

    // Act
    userDAOImpl.deleteUser("john.doe@example.com");

    // Assert
    verify(userRepo, times(1)).delete(sampleUser);
  }
}
