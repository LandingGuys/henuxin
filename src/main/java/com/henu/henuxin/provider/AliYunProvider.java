package com.henu.henuxin.provider;

import com.aliyun.oss.OSSClient;
import com.henu.henuxin.enums.HenuxinErrCode;
import com.henu.henuxin.exception.HenuxinException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AliYunProvider {
    // Endpoint以杭州为例，其它Region请按实际情况填写。
    @Value("${endpoint}")
    private String endpoint;
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${accessKeySecret}")
    private String accessKeySecret;
    @Value("${bucketName}")
    private String bucketName;

    public String upload(InputStream fileStream,String fileName){
        String finalFileName;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyMMdd");
        String[] filePaths=fileName.split("\\.");
        if(filePaths.length>1){
            finalFileName=System.currentTimeMillis()+filePaths[0]+"."+filePaths[filePaths.length-1];
        }else{
            throw new HenuxinException(HenuxinErrCode.FILE_UPLOAD_FAIL);
        }
        String objectName=sdf.format(new Date())+"/"+finalFileName;
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        if (ossClient.doesBucketExist(bucketName)) {
            System.out.println("您已经创建Bucket：" + bucketName + "。");
        } else {
            System.out.println("您的Bucket不存在，创建Bucket：" + bucketName + "。");
            ossClient.createBucket(bucketName);
        }
        ossClient.putObject(bucketName, objectName, fileStream);
        
        Date expiration =new Date(new Date().getTime() + 3600L * 1000*24*365*10);
        URL url=ossClient.generatePresignedUrl(bucketName,objectName,expiration);
        ossClient.shutdown();
        return url.toString();
    }

}
