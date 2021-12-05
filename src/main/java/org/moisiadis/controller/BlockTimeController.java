package org.moisiadis.controller;

import org.moisiadis.Main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockTimeController {

    private final int blockTime = Main.getBlockTime();

    @GetMapping("/block-time")
    public int getBlockTime() {
        return blockTime;
    }
}
