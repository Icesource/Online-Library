package com.ice.controller;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Function;
import com.ice.service.FunctionService;
import com.ice.service.impl.FunctionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/function")
public class FunctionController {

    @Autowired
    private FunctionServiceImpl functionService;

    @GetMapping("/list")
    public TMsg<Page<Function>> list(@RequestParam int currentPage,
                                     @RequestParam int pageSize){
        return functionService.list(currentPage,pageSize);
    }

    @PostMapping("/add")
    public TMsg<Function> add(@RequestParam String name,
                              @RequestParam String url){
        return functionService.add(name, url);
    }

    @PostMapping("/delete")
    public TMsg delete(@RequestParam String id) throws TException {
        return functionService.delete(id);
    }

    @PostMapping("/update")
    public TMsg<Function> update(@RequestParam String id,
                                 @RequestParam String name,
                                 @RequestParam String url) throws TException {
        return functionService.update(id, name, url);
    }

}
