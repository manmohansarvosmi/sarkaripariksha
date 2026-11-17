package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pack_feature")
@Data
public class PackFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String featureCode;

    private String featureName;

    private String description;

    private Boolean active = true;
}
