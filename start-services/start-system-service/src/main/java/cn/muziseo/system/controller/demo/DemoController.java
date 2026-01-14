package cn.muziseo.system.controller.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        return "Hello word";
    }

}
