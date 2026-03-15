package com.ironman.partyreference.application.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class CustomerEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "document_type", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  private String documentType;

  @Column(name = "document_number", nullable = false, length = 15)
  private String documentNumber;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "paternal_surname", length = 100)
  private String paternalSurname;

  @Column(name = "maternal_surname", length = 100)
  private String maternalSurname;

  @Column(name = "trade_name", length = 150)
  private String tradeName;

  @Column(name = "customer_type", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  private String customerType;

  @Column(name = "residency_status", nullable = false, length = 1, columnDefinition = "CHAR(1)")
  private String residencyStatus;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
