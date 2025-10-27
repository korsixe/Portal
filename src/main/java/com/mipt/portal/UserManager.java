package com.mipt.portal;

import com.mipt.portal.service.*;
import java.util.List;
import java.util.Optional;

public interface UserManager {
    boolean addUser(User user);
    Optional<User> getUserById(long id);
    Optional<User> getUserByEmail(String email);
    boolean updateUser(User user);
    boolean deleteUser(long id);
    List<User> getAllUsers();
    boolean isEmailExists(String email);
}