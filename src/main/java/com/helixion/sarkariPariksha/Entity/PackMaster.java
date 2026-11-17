package com.helixion.sarkariPariksha.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pack_master")
@Data
public class PackMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String packName;

    @Column(unique = true, nullable = false)
    private String packCode;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer validityDays;

    private Integer maxMockTests;

    private Boolean active = true;

    private Date createdAt;

    @OneToMany(mappedBy = "pack", fetch = FetchType.LAZY)
    private List<PackFeatureMapping> features = new ArrayList<>();

}
