package io.linkme.service.contracts;

import io.linkme.model.UserJobApplicationDTO;

import java.util.List;

public interface UserJobApplicationService {
    List<UserJobApplicationDTO> findAll();

    UserJobApplicationDTO get(Integer applicationId);

    Integer create(UserJobApplicationDTO userJobApplicationDTO);

    void update(Integer applicationId,
                UserJobApplicationDTO userJobApplicationDTO);

    void delete(Integer applicationId);
}
