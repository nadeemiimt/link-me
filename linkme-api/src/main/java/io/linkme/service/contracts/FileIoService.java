package io.linkme.service.contracts;

import java.io.File;

public interface FileIoService {
    String uploadFile(File file);

    byte[] downloadFile(String fileKey);
}
