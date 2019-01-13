package com.ice.service.impl;

import com.ice.common.Constant;
import com.ice.common.IDWorker;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.ResourceType;
import com.ice.model.VO.ResourceTypeVO;
import com.ice.repository.ResourceTypeRepository;
import com.ice.service.ResourceTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.TabExpander;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 提供资源类型维护的service
 */
@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    // 查询传入id的根节点并递归查找所有子节点，封装传入id的ResourceTypeVO
    @Override
    public TMsg<ResourceTypeVO> list(String id) throws TException {
        ResourceTypeVO result = pakResourceVO("001");
        return new TMsg<>(Constant.SUCCESS, "查询类型成功", result);
    }

    @Override
    public TMsg<ResourceType> add(String name, String superiorid) throws TException {
        int level = -1;
        Optional<ResourceType> superiorType = resourceTypeRepository.findById(superiorid);
        if (!superiorType.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "新建的类型父类型为空");
        } else {
            level = superiorType.get().getLevel() + 1;
        }

        IDWorker idWorker = new IDWorker();
        ResourceType resourceType = new ResourceType();
        resourceType.setId(idWorker.nextId());
        resourceType.setName(name);
        resourceType.setSuperiorid(superiorid);
        resourceType.setLevel(level);
        return new TMsg<>(Constant.SUCCESS, "新增类型成功", resourceTypeRepository.save(resourceType));
    }

    @Override
    public TMsg delete(String id) throws TException {
        Optional<ResourceType> resourceTypeOptional =resourceTypeRepository.findById(id);
        if(!resourceTypeOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要删除的资源类型为空");
        }
        resourceTypeRepository.deleteById(id);
        return new TMsg<>(Constant.SUCCESS, "删除资源类型成功", null);
    }

    @Override
    public TMsg<ResourceType> update(String id, String name) throws TException {
        Optional<ResourceType> resourceTypeOptional = resourceTypeRepository.findById(id);
        if(!resourceTypeOptional.isPresent()){
            throw new TException(Constant.NULL_ERROR, "要更新的资源类型不存在");
        }
        ResourceType resourceType = resourceTypeOptional.get();
        resourceType.setName(name);
        return new TMsg<>(Constant.SUCCESS,"更新资源类型成功",resourceType);
    }


    // 递归封装resourceType的方法
    private ResourceTypeVO pakResourceVO(String id) throws TException {

        // 先找到要查找的根节点的type
        ResourceTypeVO result = new ResourceTypeVO();
        Optional<ResourceType> root = resourceTypeRepository.findById(id);
        if (!root.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "无法找到对应id的资源类型");
        }
        BeanUtils.copyProperties(root.get(), result);

        // 遍历所有子节点，递归封装子节点
        List<ResourceTypeVO> subTypes = new ArrayList<>();
        List<ResourceType> resourceTypeList = resourceTypeRepository.findAllBySuperiorid(id);
        if (!resourceTypeList.isEmpty()) {
            for (ResourceType reourceType : resourceTypeList) {
                ResourceTypeVO temp = pakResourceVO(reourceType.getId());
                subTypes.add(temp);
            }
            result.setSubtypes(subTypes);
        }
        return result;
    }

}
