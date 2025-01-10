package org.pshhs.model;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Currency {
    private Long id;
    private String code;
    private String name;
    private String sign;

}



