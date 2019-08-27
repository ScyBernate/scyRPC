package ot.tch.rpc.provider.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String version;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterType; //方法参数类型

    private Object[] parameter;    //方法入参

}
