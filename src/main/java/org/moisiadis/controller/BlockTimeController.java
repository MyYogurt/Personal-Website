package org.moisiadis.controller;

import org.moisiadis.service.IPService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockTimeController {

    @GetMapping("/block-time")
    public int getBlockTime() {
        return IPService.getBlockTime();
    }
}
