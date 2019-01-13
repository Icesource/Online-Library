package com.ice.service;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Function;
import org.springframework.data.domain.Page;

public interface FunctionService {

    TMsg<Page<Function>> list(int currentPage,int pageSize);

    TMsg<Function> add(String name, String url);

    TMsg delete(String id) throws TException;

    TMsg<Function> update(String id, String name, String url) throws TException;

}
