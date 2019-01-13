package com.ice.service.impl;

import com.ice.common.Constant;
import com.ice.common.IDWorker;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Function;
import com.ice.repository.FunctionRepository;
import com.ice.repository.RoleFunctionRepository;
import com.ice.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private RoleFunctionRepository roleFunctionRepository;

    @Override
    public TMsg<Page<Function>> list(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage,pageSize, Sort.Direction.ASC,"id");
        return new TMsg<>(Constant.SUCCESS, "获取功能列表成功", functionRepository.findAll(pageable));
    }

    @Override
    public TMsg<Function> add(String name, String url) {
        IDWorker idWorker = new IDWorker();
        Function function = new Function();
        function.setId(idWorker.nextId());
        function.setName(name);
        function.setUrl(url);
        functionRepository.save(function);
        return new TMsg<>(Constant.SUCCESS, "增加功能成功", function);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    public TMsg delete(String id) throws TException {
        Optional<Function> functionOptional = functionRepository.findById(id);
        if(!functionOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要删除的功能不存在");
        }
        Function function = functionOptional.get();
        roleFunctionRepository.deleteAllByFunctionId(function.getId());
        functionRepository.delete(function);
        return new TMsg<>(Constant.SUCCESS, "删除功能及对应功能-角色联系成功", null);
    }

    @Override
    public TMsg<Function> update(String id, String name, String url) throws TException {
        Optional<Function> functionOptional = functionRepository.findById(id);
        if(!functionOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要更新的角色不存在");
        }
        Function function = functionOptional.get();
        function.setName(name);
        function.setUrl(url);
        return new TMsg<>(Constant.SUCCESS, "更新功能信息成功", function);
    }


}
