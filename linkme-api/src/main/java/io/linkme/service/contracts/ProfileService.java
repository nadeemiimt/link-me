package io.linkme.service.contracts;

import io.linkme.model.ProfileDTO;

import java.nio.file.Path;

public interface ProfileService {
    ProfileDTO getByEmail(String email);

    ProfileDTO get(Integer profileId);

    Integer create(ProfileDTO profileDTO);

    void update(Integer profileId, ProfileDTO profileDTO);

    void upsert(ProfileDTO profileDTO);

    void delete(Integer profileId);

    String uploadFileAndGetUrl(Path path);
}
