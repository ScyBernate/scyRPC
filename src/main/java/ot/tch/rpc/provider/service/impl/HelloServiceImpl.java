package ot.tch.rpc.provider.service.impl;

import lombok.extern.slf4j.Slf4j;
import ot.tch.rpc.provider.annotation.RPC;
import ot.tch.rpc.provider.service.HelloService;

@RPC(name="HelloService")
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        log.info("执行hello方法");
    }
}
