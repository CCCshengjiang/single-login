package com.wen.singlelogin.model;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author wen
 */
@Data
public class User implements Serializable {

    /**
     * id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 7042086086840559147L;
}
