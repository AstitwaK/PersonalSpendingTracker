package com.PersonalSpendingTracker.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String name;
    private String email;
    private String createdDate;
    private String lastUpdatedDate;
}
