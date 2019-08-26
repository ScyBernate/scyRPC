package ot.tch.rpc.service.impl;

import ot.tch.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        System.out.println("hello");
    }
}
