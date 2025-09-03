package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Supplier;

public record BinaryContentCreateRequest(
    @NotBlank @Size(max = 255)
    String fileName,

    @NotBlank
    String contentType,

    @NotBlank
    byte[] bytes
) {

    public long size() {
        return bytes.length;
    }

    public Supplier<InputStream> toInputStreamSupplier() {
        return () -> new ByteArrayInputStream(bytes);
    }

}
