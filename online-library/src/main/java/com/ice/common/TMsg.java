package com.ice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一的消息返回格式
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMsg<T> {

    /**
     * 消息编码
     */
    private String code;
    /**
     * 消息描述
     */
    private String desc;
    /**
     * 消息数据
     */
    private T data;

}