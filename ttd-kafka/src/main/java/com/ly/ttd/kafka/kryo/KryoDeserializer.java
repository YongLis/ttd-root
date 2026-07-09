package com.ly.ttd.kafka.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author yong.li
 * @since 2026/3/13 11:00
 */
public class KryoDeserializer<T> implements Deserializer<T> {
    ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public T deserialize(String s, byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        Kryo kryo = kryos.get();
        return (T) kryo.readClassAndObject(new Input(bytes));
    }

    @Override
    public void close() {
        kryos.remove();
    }
}
