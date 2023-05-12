package flyffbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("liveness")
public class LivenessController {

    @PostMapping
    public void keepAlive(){
        //TODO: implement me
    }

    @GetMapping
    public boolean isAlive(){
        //TODO: implement me
        return true;
    }
}
