package com.teoresi.staff.dtos.CustomsDTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceLoadDTO implements Comparable<ResourceLoadDTO>{

    private String unitTrigram;
    private String fullName;
    private int year;
    private Map<Integer, Map<Integer,Integer>> weeklyRealCommitmentPct;
    private Map<Integer, Map<Integer,Integer>> weeklySaleCommitmentPct;


    @Override
    public int compareTo(ResourceLoadDTO other) {
        return this.fullName.compareTo(other.fullName);
    }
}
