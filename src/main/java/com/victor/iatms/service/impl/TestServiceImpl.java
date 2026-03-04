package com.victor.iatms.service.impl;

import com.victor.iatms.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        log.info("TestService method invoked");
    }
}
