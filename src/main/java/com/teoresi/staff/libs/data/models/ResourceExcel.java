package com.teoresi.staff.libs.data.models;

import com.teoresi.staff.shared.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResourceExcel {


    private String unitTrigram;
    private Integer employeeId;
    private String name;
    private String surname;
    private Date birthDate;
    private Date hiringDate;
    private Date leaveDate;
    private String site;
    private String location;
    private Set<Role> roles;

    private Float hourlyCost;
    private Date hourlyCostStartDate;

    public void setRoles(String[] stringRoles){
        Set<Role> roles = new HashSet<>();
        for (String stringRole : stringRoles) {
            switch (stringRole.trim()) {
                case "DUM":
                    roles.add(Role.DUM);
                    break;
                case "GDM":
                    roles.add(Role.GDM);
                    break;
                case "PM":
                    roles.add(Role.PM);
                    break;
                case "DTL":
                    roles.add(Role.DTL);
                    break;
                case "PSL":
                    roles.add(Role.PSL);
                    break;
                case "PSE":
                    roles.add(Role.PSE);
                    break;
                case "PSM":
                    roles.add(Role.PSM);
                    break;
                default:
                    roles.add(Role.CONSULTANT);
                    break;
            }
        }
        this.roles = roles;
    }

    public void setName(String name){
        this.name = name!= null ? name.replaceAll("[^a-zA-Z0-9 ]", "") : null;
    }

    public void setSurname(String name){
        this.surname = name!= null ? name.replaceAll("[^a-zA-Z0-9 ]", "") : null;
    }

    private Date convertToDate(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public Set<Role> getRoles() {
        return Objects.requireNonNullElseGet(this.roles, () -> Collections.singleton(Role.CONSULTANT));
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

}
