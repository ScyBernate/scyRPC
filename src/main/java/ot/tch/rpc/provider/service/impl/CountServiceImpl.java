package ot.tch.rpc.provider.service.impl;

import lombok.extern.slf4j.Slf4j;
import ot.tch.rpc.provider.annotation.RPC;
import ot.tch.rpc.provider.service.CountService;

@Slf4j
@RPC(name="CountService")
public class CountServiceImpl implements CountService {

    @Override
    public int count(int a, int b) {
        log.info("执行count方法");
        return a+b;
    }

}
