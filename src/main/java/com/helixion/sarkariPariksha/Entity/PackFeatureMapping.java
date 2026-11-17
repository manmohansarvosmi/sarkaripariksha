package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pack_feature_mapping")
@Data
public class PackFeatureMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", nullable = false)
    private PackMaster pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private PackFeature feature;

    private Boolean enabled = true;

    private Integer featureLimit;
}
