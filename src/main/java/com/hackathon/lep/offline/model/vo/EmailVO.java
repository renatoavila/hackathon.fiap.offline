package com.hackathon.lep.offline.model.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString()
public class EmailVO {

    private String subject;
    private String from;
    private String content;

}