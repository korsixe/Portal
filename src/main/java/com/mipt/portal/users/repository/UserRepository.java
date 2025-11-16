package com.mipt.portal.users.repository;

import com.mipt.portal.users.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<User> save(User user);

  Optional<User> findByEmail(String email);

  Optional<User> findById(long id);

  boolean update(User user);

  boolean delete(long id);

  boolean existsByEmail(String email);

  List<User> findAll();
}
