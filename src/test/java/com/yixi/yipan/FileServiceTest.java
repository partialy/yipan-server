package com.yixi.yipan;

import com.yixi.yipan.entity.FileInfo;
import com.yixi.yipan.service.FileService;
import com.yixi.yipan.service.UserService;
import com.yixi.yipan.service.impl.FileServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileServiceImpl fileServiceImpl;

//    @Test
//    void testDeleteFile(){
//        String userId = "1";
//        String fileId = "fb410b97-1184-47f2-9bfa-4cdc718d547d";
//        fileServiceImpl.deleteFile(fileId);
//        assertEquals(1, 1);
//
//    }

}