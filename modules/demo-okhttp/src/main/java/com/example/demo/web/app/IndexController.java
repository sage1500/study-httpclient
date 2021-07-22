package com.example.demo.web.app;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("[!] hello");

        try {
            // @formatter:off
            Request request = new Request.Builder()
                    .url("http://localhost:8080/api/hello")
                    .addHeader("Authorization", Credentials.basic("clientId", "clientSecret"))
                    .addHeader("Accept", "application/json")
                    .addHeader("X-Forwarded-Proto", "https")
                    .addHeader("X-Forwarded-Port", "8443")
                    .addHeader("Host", "myhost")
                    .post(new FormBody.Builder()
                        .add("token", "token/123")
                        .build())
                    .build();
            // @formatter:on

            try (Response response = new OkHttpClient().newCall(request).execute()) {
                log.info("[!] Result:");
                log.info(" code={}", response.code());
                log.info(" contentType={}", response.body().contentType());
                log.info(" body={}", response.body().string());
            }
        } catch (Exception e) {
            log.error("[!] fail: {}", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/api/hello")
    @ResponseBody
    public String apiHello(WebRequest request, HttpServletRequest srvReq, UriComponentsBuilder urlbulder) {
        log.info("[!] api hello");

        log.info("scheme: {}", srvReq.getScheme());
        log.info("serverName: {}", srvReq.getServerName());
        log.info("serverPort: {}", srvReq.getServerPort());
        log.info("baseUrl: {}", urlbulder.path("/").build().toUri());

        log.info("Headers:");
        for (var i = request.getHeaderNames(); i.hasNext();) {
            var name = i.next();
            for (var value : request.getHeaderValues(name)) {
                log.info(" {}: {}", name, value);
            }
        }

        log.info("Parameters:");
        for (var ent : request.getParameterMap().entrySet()) {
            for (var value : ent.getValue()) {
                log.info(" {}: {}", ent.getKey(), value);
            }
        }

        return "hello";
    }
}
