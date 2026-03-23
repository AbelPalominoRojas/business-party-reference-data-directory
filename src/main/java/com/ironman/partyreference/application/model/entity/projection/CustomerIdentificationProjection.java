package com.ironman.partyreference.application.model.entity.projection;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class CustomerIdentificationProjection implements Serializable {
  private Long id;
  private String documentType;
  private String documentNumber;
}
