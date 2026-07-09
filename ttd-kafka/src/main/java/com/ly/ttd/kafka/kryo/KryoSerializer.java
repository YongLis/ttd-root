package com.ly.ttd.kafka.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @author yong.li
 * @since 2026/3/13 10:26
 */
public class KryoSerializer<T> implements Serializer<T> {
    ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true); // 开启引用机制，避免循环引用导致的栈溢出
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, T t) {
        if(null == t){
             return null;
        }
        Kryo kryo = kryos.get();
        Output output = new Output(2048, -1);
        kryo.writeClassAndObject(output, t);
        return output.toBytes();
    }

    @Override
    public void close() {
        kryos.remove();
    }
}
