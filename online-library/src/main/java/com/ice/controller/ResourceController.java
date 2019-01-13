package com.ice.controller;

import com.ice.common.Constant;
import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Resource;
import com.ice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;


    @GetMapping("/list/{typeId}")
    public TMsg<Page<Resource>> resourceList(@RequestParam int currentPage,
                                             @RequestParam int pageSize,
                                             @PathVariable(name = "typeId", required = false) String typeId,
                                             @RequestParam(required = false) String uploaderId,
                                             @RequestParam(required = false) String search){
        return resourceService.list(currentPage, pageSize, typeId, uploaderId, search);
    }

    @PostMapping("/upload")
    public TMsg<Resource> uploadResource(@RequestParam MultipartFile file,
                                         @RequestParam(required = false) String name,
                                         @RequestParam String typeId) throws Exception {

        return resourceService.add(file,name,typeId);
    }

    @GetMapping("/delete")
    public TMsg deleteResource(@RequestParam String id) throws TException {
        return resourceService.delete(id);
    }

    @GetMapping("/download/{typeId}")
    public void downloadResource(@RequestParam String id,
                                 @PathVariable(name = "typeId", required = false) String typeId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws TException {
        resourceService.download(id, typeId ,request,response);
    }

    @GetMapping("/listAll")
    public TMsg<Page<Resource>> listAll(@RequestParam int currentPage,
                                             @RequestParam int pageSize,
                                             @RequestParam(required = false) String typeId,
                                             @RequestParam(required = false) String uploaderId,
                                             @RequestParam(required = false) String search){
        return resourceService.list(currentPage, pageSize, typeId, uploaderId, search);
    }
}
