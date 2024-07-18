package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "company")
public class Company extends BasicEntity implements Cloneable{

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String directory;

    public static String computeDirectory(Company company) {
        String name = company.getName();
        name = name.replaceAll("[^a-zA-Z0-9]", "").trim();

        return name + "-" + getTimestamp() + "/";
    }

    @Override
    public Company clone() {
        try {
            return (Company) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private static String getTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return timestamp.format(formatter);
    }
}
