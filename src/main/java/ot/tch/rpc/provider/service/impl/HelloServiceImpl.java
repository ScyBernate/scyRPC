package ot.tch.rpc.provider.service.impl;

import ot.tch.rpc.provider.annotation.RPC;
import ot.tch.rpc.provider.service.HelloService;

@RPC(name="HelloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        System.out.println("hello");
    }
}
