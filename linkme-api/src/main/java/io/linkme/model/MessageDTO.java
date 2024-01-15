package io.linkme.model;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageDTO {

    private Integer messageId;

    private String messageText;

    private OffsetDateTime timestamp;

    @Size(max = 50)
    private String status;

    private Integer sender;

    private Integer receiver;

}
