package com.client.staff.entities;

import com.client.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "company")
public class Company extends BasicEntity implements Cloneable{

    private Long id;
    private String name;
    private String owner;
    private String regione;
    private String provincia;
    private String citta;
    private String address;
    private String code;
    private String firstEmail;
    private String secondEmail;
    private String thirdEmail;
    private String phone;
    private String directory;
    private String note;

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

    public Set<String> getEmails() {
        Set<String> validEmails = new HashSet<>();
        Collections.addAll(validEmails, firstEmail, secondEmail, thirdEmail);
        validEmails.remove(null);
        return validEmails;
    }

}
