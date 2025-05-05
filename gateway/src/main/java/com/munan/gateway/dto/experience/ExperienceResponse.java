package com.munan.gateway.dto.experience;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceResponse implements Serializable {

    private List<String> skills;
    private List<ExperienceEntry> experience;

    // Inner class for experience entries
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceEntry implements Serializable {

        private String date; // From "date" in Flask
        private List<String> companies; // From "companies" in Flask
        private String context; // From "context" in Flask
    }
}
