package com.teoresi.staff.config;

import com.teoresi.staff.dtos.ImportResourceDTO;
import com.teoresi.staff.dtos.ImportTimesheetDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {
    @Bean
    @Qualifier("timesheets")
    public Sinks.Many<ImportTimesheetDTO> sinkTimesheets() {
        return Sinks.many().replay().limit(1);
    }

    @Bean
    @Qualifier("resources")
    public Sinks.Many<ImportResourceDTO> sinkResources() {
        return Sinks.many().replay().limit(1);
    }

    @Bean
    public Flux<ImportTimesheetDTO> updateTimesheetBroadcast(@Qualifier("timesheets") Sinks.Many<ImportTimesheetDTO> sink) {
        return sink.asFlux().cache(0);
    }

    @Bean
    public Flux<ImportResourceDTO> updateResourceBroadcast(@Qualifier("resources") Sinks.Many<ImportResourceDTO> sink) {
        return sink.asFlux().cache(0);
    }
}
