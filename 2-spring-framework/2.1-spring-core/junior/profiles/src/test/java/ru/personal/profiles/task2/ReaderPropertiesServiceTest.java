package ru.personal.profiles.task2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = ReaderPropertiesService.class)
class ReaderPropertiesServiceTest {

    @Autowired
    private ReaderPropertiesService readerPropertiesService;

    @Test
    void readProperties_shouldTestValueWhenUseTestProfile() {
        assertEquals("/tmp/test-uploads", readerPropertiesService.getUploadPatch());
    }

}