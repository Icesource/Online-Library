package com.ice.service;

import com.ice.common.TException;
import com.ice.common.TMsg;
import com.ice.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ResourceService {

    TMsg<Resource> add(MultipartFile file, String name, String typeId) throws Exception;

    TMsg<Resource> findById(String id) throws TException;

    TMsg<Page<Resource>> list(int currentPage, int pageSize, String typeId, String uploaderId, String search);

    TMsg delete(String id) throws TException;

    void download(String id,String typeId , HttpServletRequest request, HttpServletResponse response) throws TException;
}
