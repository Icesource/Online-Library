package com.ice.service.impl;

import com.ice.common.*;
import com.ice.model.Resource;
import com.ice.model.ResourceType;
import com.ice.repository.ResourceRepository;
import com.ice.repository.ResourceTypeRepository;
import com.ice.repository.UserRepository;
import com.ice.repository.UserRoleRepository;
import com.ice.service.ResourceService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FtpUtil ftpUtil;

    @Value("${library.ftp.basePath}")
    private String ftpBasePath;

    @Value("${library.ftp.host}")
    private String ftpHost;

    @Value("${library.file.visit-path}")
    private String fileVisitPath;

    @Value("${library.file.base-path}")
    private String fileBasePath;

    @Value("${library.ftp.previewPath}")
    private String filePreviewPath;


    /**
     * 上传一个资源文件到服务器，name不传则默认为文件原始名字
     *
     * @param file
     * @param name
     * @param typeId
     * @return
     * @throws TException
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class, timeout = 5000)
    @Override
    public TMsg<Resource> add(MultipartFile file, String name, String typeId) throws Exception {

        ParamUtil.isNullParam(typeId);
        ParamUtil.isBlankParam(typeId);

        // 获取上传时当前的登陆用户
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        User user = (User) auth.getPrincipal();
        Optional<com.ice.model.User> userOptional = userRepository.findByUsername(user.getUsername());
//        authentication.getPrincipal();

        if (file.isEmpty()) {
            throw new TException(Constant.PARAM_ERROR, "上传文件为空");
        }
        Optional<ResourceType> resourceTypeOptional = resourceTypeRepository.findById(typeId);
        if (!resourceTypeOptional.isPresent()) {
            throw new TException(Constant.PARAM_ERROR, "类型指向空的资源类型");
        }

        IDWorker idWorker = new IDWorker();

        // 设置存储对象
        Resource resource = new Resource();
        resource.setId(idWorker.nextId());
        resource.setClickCount(0);
        resource.setUploaderid(userOptional.get().getId());
        resource.setUploaderName(userOptional.get().getUsername());
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        resource.setTypeid(typeId);
        resource.setTypeName(resourceTypeOptional.get().getName());
        if (name == null) {
            resource.setName(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")));
        } else {
            resource.setName(name);
        }

        // 将文件转存
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String filename = resource.getId() + '.' + suffix;
        boolean b = ftpUtil.uploadFile("/" + suffix, filename, new ByteArrayInputStream(file.getBytes()));
        if (!b) {
            throw new TException(Constant.PARAM_ERROR, "文件转存失败");
        }

        String link = fileVisitPath + suffix + "/" + filename;
        resource.setLink(link);
        resource.setFormat(suffix);

        resourceRepository.save(resource);
        return new TMsg<>(Constant.SUCCESS, "资源上传完成", resource);
    }


    // 根据id查找资源的具体信息
    @Override
    public TMsg<Resource> findById(String id) throws TException {
        ParamUtil.isBlankParam(id);
        ParamUtil.isNullParam(id);

        Optional<Resource> result = resourceRepository.findById(id);
        if (!result.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "没有对应ID的资源");
        }

        // 定义ResourceVO对象并查询
        Resource resource = result.get();
        Optional<ResourceType> resourceTypeOptional = resourceTypeRepository.findById(resource.getTypeid());
        if (!resourceTypeOptional.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "资源指向了一个空的资源类型");
        }
        return new TMsg<>(Constant.SUCCESS, "查找资源成功", resource);
    }


    // 查询资源列表
    @Override
    public TMsg<Page<Resource>> list(int currentPage, int pageSize, String typeId, String uploaderId, String search) {

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.Direction.DESC, "create_time");
        Page<Resource> page = resourceRepository.list(pageable, typeId, uploaderId, search);
        for (Resource resource: page.getContent()) {
            resource.setClickCount(resource.getClickCount()+1);
            resourceRepository.save(resource);
        }
        return new TMsg<>(Constant.SUCCESS, "查询成功", page);
    }

    @Override
    public TMsg delete(String id) throws TException {
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "要删除的对象不存在");
        }
        resourceRepository.deleteById(id);
        return new TMsg<>(Constant.SUCCESS, "删除成功", null);
    }

    @Override
    public void download(String id, String typeId, HttpServletRequest request, HttpServletResponse response) throws TException {
        Optional<Resource> resourceOptional = resourceRepository.findById(id);
        if (!resourceOptional.isPresent()) {
            throw new TException(Constant.NULL_ERROR, "下载的资源不存在");
        }
        Resource resource = resourceOptional.get();
        if (!resource.getTypeid().equals(typeId)) {
            throw new TException(Constant.TYPE_NOT_MATCH, "资源和类型不匹配，无法下载");
        }
        try {
            String remotePath = resource.getLink().substring(fileBasePath.length());
            byte[] bytes = ftpUtil.downloadFile(remotePath, resource.getId() + "." + resource.getFormat());
            String fileName = URLEncoder.encode(resource.getName(), "UTF-8");
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.addHeader("Content-Length", "" + bytes.length);
            response.setContentType("application/octet-stream;charset=UTF-8");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(bytes);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new TException(Constant.FILE_DOWNLOAD_FAIL, "找不到要下载的文件:" + e.getMessage());
        } catch (IOException e) {
            throw new TException(Constant.FILE_DOWNLOAD_FAIL, "IO失败：" + e.getMessage());
        }
    }
}
