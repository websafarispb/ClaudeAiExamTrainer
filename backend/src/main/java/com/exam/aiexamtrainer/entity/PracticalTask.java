package com.exam.aiexamtrainer.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class PracticalTask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500)
  private String title;

  @Column(length = 5000)
  private String scenario;

  @Column(length = 5000)
  private String task;

  @ElementCollection
  @CollectionTable(
      name = "practical_task_what_to_cover",
      joinColumns = @JoinColumn(name = "practical_task_id")
  )
  @Column(name = "what_to_cover", length = 2000)
  private List<String> whatToCover;

  @Column(name = "sample_approach", length = 5000)
  private String sampleApproach;
}