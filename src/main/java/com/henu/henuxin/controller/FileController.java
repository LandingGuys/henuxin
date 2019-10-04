package com.henu.henuxin.controller;

import com.henu.henuxin.dto.UserDTO;
import com.henu.henuxin.mapper.UserMapper;
import com.henu.henuxin.model.User;
import com.henu.henuxin.model.UserExample;
import com.henu.henuxin.provider.AliYunProvider;
import com.henu.henuxin.utils.ByteUtil;
import com.henu.henuxin.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Controller
@Slf4j
public class FileController {
    @Autowired
    private AliYunProvider aliYunProvider;
    @Autowired
    private UserMapper userMapper;
//    @ResponseBody()
//    @RequestMapping("/file/upload")
//    public FileVO upload(HttpServletRequest request){
//        MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest)request;
//        MultipartFile file = multipartRequest.getFile("editormd-image-file");
//        try {
//            String fileName= aliYunProvider.upload(file.getInputStream(),file.getOriginalFilename());
//            FileVO fileVO = new FileVO();
//            fileVO.setSuccess(1);
//            fileVO.setUrl(fileName);
//            return  fileVO;
//        } catch (Exception e) {
//            log.error("upload error", e);
//            e.printStackTrace();
//            FileVO fileVO = new FileVO();
//            fileVO.setSuccess(0);
//            fileVO.setMessage("上传失败");
//            return fileVO;
//        }
//    }
    @ResponseBody
    @RequestMapping("/user/imageUpload")
    public Object updateImage(@RequestBody UserDTO userDTO) {
        try {

            String base64=userDTO.getFaceData();
            byte[] bytes = ByteUtil.base64ToByte(base64);
            InputStream inputStream=new ByteArrayInputStream(bytes);
            String fileNameUrl=userDTO.getUserId()+".png";
            String fileName= aliYunProvider.upload(inputStream,fileNameUrl);
            User dbUser = userMapper.selectByPrimaryKey(userDTO.getUserId());
            User user = new User();
            user.setFaceImage(fileName);
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(user, userExample);
            User selectUser = userMapper.selectByPrimaryKey(userDTO.getUserId());
            return ResultVOUtil.success(selectUser);
        } catch (Exception e) {
            log.error("upload error", e);
            e.printStackTrace();
            return ResultVOUtil.error(1004,"上传失败");
        }

    }


}
