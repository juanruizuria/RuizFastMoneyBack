package com.ruiz.prestamos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class LocalDateConfig {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Módulo de soporte para las clases de java.time (JSR-310)
        JavaTimeModule module = new JavaTimeModule();

        // Serializador -> transforma LocalDate en "dd/MM/yyyy"
        module.addSerializer(LocalDate.class, new LocalDateSerializer(FORMATTER));

        // Deserializador -> transforma "dd/MM/yyyy" en LocalDate
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(FORMATTER));

        // Registramos el módulo en el ObjectMapper
        mapper.registerModule(module);

        return mapper;
    }
}

