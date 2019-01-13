package com.ice.service;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.ResourceType;
import com.ice.model.VO.ResourceTypeVO;

public interface ResourceTypeService {

    TMsg<ResourceTypeVO> list(String id) throws TException;

    TMsg<ResourceType> add(String name, String superiorid) throws TException;

    TMsg delete(String id) throws TException;

    TMsg<ResourceType> update(String id, String name) throws TException;

}
