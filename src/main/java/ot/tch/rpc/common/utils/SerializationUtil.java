package ot.tch.rpc.common.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import ot.tch.rpc.common.model.RpcResponse;


/**
 * @ClassName SerializationUtil
 * @Description  protostuff序列化
 * @Author shengchongyang
 * @DATE 2019/8/27 14:14
 * @Version 1.0
 **/
public class SerializationUtil {

    private SerializationUtil() {
    }

    /**
     * 序列化单个对象
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj){
        if (null == obj) {
            throw new RuntimeException("序列化对象不能为空:" + obj);
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        byte[] protostuff = null;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("序列化->" + obj.getClass() + "对象" + obj + "发生异常", e);
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    /**
     * 反序列化单个对象
     * @param paramArrayOfByte
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (null == paramArrayOfByte || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常，byte序列为空");
        }
        T instance = null;
        try {
            instance = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("反序列化过程中创建对象失败", e);
        }
        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
        return instance;
    }

    public static void main(String[] args) {
        RpcResponse resp = new RpcResponse();
        byte[] bytes = SerializationUtil.serialize(resp);
        System.out.println(bytes.length);
    }

}
