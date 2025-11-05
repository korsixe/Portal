package com.mipt.portal;

import java.sql.SQLException;

public interface IDatabaseManager {
  void createTables() throws SQLException;
  void insertData() throws SQLException;
}