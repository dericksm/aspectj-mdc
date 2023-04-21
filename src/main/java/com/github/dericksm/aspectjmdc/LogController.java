package com.github.dericksm.aspectjmdc;

import com.github.dericksm.aspectjmdc.aspectj.ClearMDC;
import com.github.dericksm.aspectjmdc.aspectj.LogMethodEntryAndExit;
import com.github.dericksm.aspectjmdc.aspectj.LogMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @GetMapping("/log-method")
    @ClearMDC
    @LogMDC
    public void logMdcOnMethod(String one, @RequestParam(required = false, value = "two") String two, String three){
        LOGGER.info("logMdcOnMethod");
    }

    @GetMapping("/log-parameter")
    @ClearMDC
    public void logMdcOnParameter(@LogMDC String one, @RequestParam(required = false, value = "two") String two, String three){
        LOGGER.info("logMdcOnParameter");
    }

    @PostMapping
    @LogMethodEntryAndExit
    public ProductResponse logMethod(@RequestBody Product product) {
        return new ProductResponse(product);
    }

}
