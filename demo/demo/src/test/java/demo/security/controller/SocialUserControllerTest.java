/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package demo.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static top.dcenter.ums.security.common.consts.SecurityConstants.CHARSET_UTF8;

/**
 * 用户控制器测试
 * @author zhailiang
 * @author  YongWu zheng
 * @version V1.0  Created by 2020/5/1 19:39
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@Slf4j
public class SocialUserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenQuerySucess() throws Exception {
        String result =
        mockMvc.perform(get("/user").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8)
                                .param("username", "jake")
                                .param("page", "5")
                                .param("size", "15")
                                .param("sort", "age,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }

    @Test
    public void whenGenInfoSuccess() throws Exception {
        String result = mockMvc.perform(get("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tom"))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }

    @Test
    public void whenGetInfoFail() throws Exception {
        mockMvc.perform(get("/user/a").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void whenCreateSuccess() throws Exception {

        String content = "{\"username\":\"tom\",\"password\":null, \"birthday\":"+ new Date().getTime() +"}";
        log.warn(content);
        String result = mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1 ))
                .andReturn().getResponse().getContentAsString();
        log.warn(result);
    }
    @Test
    public void whenUpdateSuccess() throws Exception {

        String content =
                "{\"id\":\"1\",\"username\":\"tom\",\"password\":\"111\", \"birthday\":"+ new Date().getTime() +"}";
        log.warn("test: " + content);
        String result = mockMvc.perform(put("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8).content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1 ))
                .andReturn().getResponse().getContentAsString();
        log.warn("test: " + result);
    }

    @Test
    public void whenDeleteSuccess() throws Exception {
        mockMvc.perform(delete("/user/1").contentType(MediaType.APPLICATION_JSON).characterEncoding(CHARSET_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUploadSuccess() throws Exception {
//        String result = mockMvc.perform(multipart("/file")
//                                              .file(new MockMultipartFile(
//                                                      "file",
//                                                      "test.txt",
//                                                      "multipart/form-data",
//                                                      ("hello world!!!!").getBytes())))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//        log.info("result = {}", result);

    }

    @Test
    public void download() throws Exception {
        // 1588426747049test.txt
//        MockHttpServletResponse response = mockMvc.perform(get("/file/1600439189151test.txt"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//        log.info("response.getContentAsString() = {}", response.getContentAsString());
//        log.info("response.getHeader(\"Content-Disposition\") = {}", response.getHeader("Content-Disposition"));
    }
}
