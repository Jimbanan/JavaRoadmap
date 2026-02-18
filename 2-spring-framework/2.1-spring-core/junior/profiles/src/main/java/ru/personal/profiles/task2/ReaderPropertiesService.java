package ru.personal.profiles.task2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReaderPropertiesService {

    @Value("${app.upload.path}")
    private String uploadPatch = "/default/patch";

    public String getUploadPatch() {
        return uploadPatch;
    }
}
