package com.ice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一异常处理 自定义错误
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TException extends Exception {
    /**
     * 错误编码
     */
    private String code;
    /**
     * 错误描述
     */
    private String desc;
}
