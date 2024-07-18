package com.client.staff.entities;

import com.client.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "licence")
public class Licence extends BasicEntity implements Cloneable{

    private Long id;
    private String name;
    private String note;

    private String directory;

    public static String computeDirectory(Licence licence) {
        String name = licence.getName();
        name = name.replaceAll("[^a-zA-Z0-9]", "").trim();
        return name + "-" + getTimestamp() + "/";
    }

    @Override
    public Licence clone() {
        try {
            return (Licence) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for Unit");
        }
    }

    private static String getTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return timestamp.format(formatter);
    }
}