package com.victor.iatms.service.impl;

import com.victor.iatms.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        System.out.println("test");
    }
}
