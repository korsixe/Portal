package com.yourcompany.yourproject.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Announcement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  private Double price;
  private String category;
  private Date creationDate;
  private Date updateDate;
  private String status;

  @ManyToOne
  private User user;

  // Write getters and setters
}
