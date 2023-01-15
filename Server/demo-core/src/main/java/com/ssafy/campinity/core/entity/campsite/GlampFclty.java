package com.ssafy.campinity.core.entity.campsite;


import com.ssafy.campinity.core.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class GlampFclty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fcltyName;

    @OneToMany
    @JoinColumn(name = "glamp_fclty_id")
    @ToString.Exclude
    private List<CampsiteAndGlampFclty> campsiteAndGlampFclties = new ArrayList<>();

    @Builder
    public GlampFclty(String fcltyName, List<CampsiteAndGlampFclty> campsiteAndGlampFclties) {
        this.fcltyName = fcltyName;
        this.campsiteAndGlampFclties = campsiteAndGlampFclties;
    }
}
