package ot.tch.rpc.provider.service.impl;

import ot.tch.rpc.provider.annotation.RPC;
import ot.tch.rpc.provider.service.CountService;

@RPC(name="CountService")
public class CountServiceImpl implements CountService {

    @Override
    public int count(int a, int b) {
        return a+b;
    }

}
