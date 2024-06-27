package com.teoresi.staff.dtos.CustomsDTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectGanttDTO implements Comparable<ProjectGanttDTO>{

    private String unitTrigram;
    private int year;
    private String name;
    private String status;
    private Map<Integer, Map<Integer,Integer>> estimatedCostPct;
    private Map<Integer, Map<Integer,Integer>> estimatedCost;
    private Map<Integer, Map<Integer,Integer>> actualCostPct;
    private Map<Integer, Map<Integer,Integer>> actualCost;


    @Override
    public int compareTo(@NotNull ProjectGanttDTO o) {
        return this.name.compareTo(o.name);
    }
}
