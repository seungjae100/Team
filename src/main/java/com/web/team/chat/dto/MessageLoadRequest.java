package com.web.team.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageLoadRequest {

    private Long roomId;
    private int page;
    private int size;
}
