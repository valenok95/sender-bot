package senderbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import senderbot.services.RestService;
import senderbot.services.SchedulerService;
import senderbot.services.SenderService;

@RestController
@RequestMapping("test/")
public class TestController {
    private SenderService senderService;
    private RestService restService;
    private SchedulerService schedulerService;

    @Autowired
    public TestController(SenderService senderService, RestService restService,
                          SchedulerService schedulerService) {
        this.senderService = senderService;
        this.restService = restService;
        this.schedulerService = schedulerService;
    }

    @CrossOrigin
    @PostMapping("update-new-text")
    public void updatePosts(@RequestBody String newText) {
        senderService.updatePosts(newText);
    }

    @CrossOrigin
    @PostMapping("update-rates")
    public void updateRates() {
        schedulerService.updateRates();
    }

    @CrossOrigin
    @PostMapping("naver-rate")
    public int getSwiftRate() {
        return restService.getNaverRate();
    }

    @CrossOrigin
    @PostMapping("upbit-rate")
    public int getCashRate() {
        return restService.getUpBitRate();
    }

}
