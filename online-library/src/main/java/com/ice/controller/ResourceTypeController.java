package com.ice.controller;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.ResourceType;
import com.ice.model.VO.ResourceTypeVO;
import com.ice.service.impl.ResourceTypeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/type")
public class ResourceTypeController {

    @Autowired
    private ResourceTypeServiceImpl resourceTypeService;

    @GetMapping("list")
    public TMsg<ResourceTypeVO> list(@RequestParam(required = false, defaultValue = "001") String id) throws TException {
        return resourceTypeService.list(id);
    }

    @PostMapping("add")
    public TMsg<ResourceType> add(@RequestParam String name,
                                  @RequestParam String superiorid) throws TException {
        return resourceTypeService.add(name,superiorid);
    }

    @PostMapping("delete")
    public TMsg delete(@RequestParam String id) throws TException {
        return resourceTypeService.delete(id);
    }

    @PostMapping("update")
    public TMsg update(@RequestParam String id,
                       @RequestParam String name) throws TException {
        return resourceTypeService.update(id, name);
    }

}
