package com.love.example.Controller;

import com.love.example.Exception.HttpResponse;
import com.love.example.Exception.HttpStatusTypeEnum;
import com.love.example.Exception.ResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    @GetMapping("/normal")
    public HttpResponse success() throws ResponseException {
        return HttpResponse.success();
    }
    @GetMapping("/err/match")
    public HttpResponse errMatch(int id) throws ResponseException {
        if(id == 0) {
            throw new ResponseException(HttpStatusTypeEnum.USER_INPUT_ERROR, "id不符合标准");
        } else {
            Map<String, Integer> data = new HashMap<>();
            data.put("id", id);
            return HttpResponse.success(data);
        }
    }
    @GetMapping("/err/system")
    public HttpResponse sysError() throws ResponseException {
        try{
            int i = 1/0;
        } catch(Exception e) {
            throw new ResponseException(HttpStatusTypeEnum.SYSTEM_ERROR.getCode(), "系统异常");
        }
        return HttpResponse.success();
    }
}
