package ot.tch.rpc.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    private Object result;

    private Throwable exception;

    private String requestId;

    public boolean hasError(){
        return this.exception==null?false:true;
    }

}
