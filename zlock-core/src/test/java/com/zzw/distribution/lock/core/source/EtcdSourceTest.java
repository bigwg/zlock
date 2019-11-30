package com.zzw.distribution.lock.core.source;

import com.google.common.base.Charsets;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * {@link EtcdSource} 锁测试类
 *
 * @author zhaozhiwei
 * @date 2019/11/30 1:01 下午
 * @see EtcdSource
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EtcdSourceTest {

    private Source source;

    private String lockName = "etcdTryAcquireLock";

    @BeforeAll
    public void beforeAll() {
        source = new EtcdSource("http://127.0.0.1:2379");
    }

    @Test
    public void tryAcquireTest() throws InterruptedException {
        boolean result = source.tryAcquire(lockName, 1);
        Assertions.assertTrue(result);
        Thread.sleep(20000);
        tryReleaseTest();
    }

    @Test
    public void tryReleaseTest() {
        boolean release = source.release(lockName, 1);
        Assertions.assertTrue(release);
    }

    @Test
    public void etcdKVTest() throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        KV kvClient = client.getKVClient();
        Lease leaseClient = client.getLeaseClient();
        long startTime = System.currentTimeMillis();
        long leaseId = leaseClient.grant(60).get().getID();
        PutResponse putResponse = kvClient.put(getByteSeq("/zlock/test/127.0.0.1"), getByteSeq("127.0.0.1"),
                PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        long revision = putResponse.getHeader().getRevision();
        System.out.println("revision = " + revision);
        List<KeyValue> kvs = kvClient.get(getByteSeq("/zlock/test/127.0.0.1")).get().getKvs();
        for (KeyValue kv : kvs) {
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " create revision = " + kv.getCreateRevision());
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " modify revision = " + kv.getModRevision());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("duration = " + (endTime - startTime));
    }

    @Test
    public void etcdLeaseTest() throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        KV kvClient = client.getKVClient();
        Lease leaseClient = client.getLeaseClient();
        long startTime = System.currentTimeMillis();
        List<KeyValue> kvs = kvClient.get(getByteSeq("/zlock/test/127.0.0.1")).get().getKvs();
        for (KeyValue kv : kvs) {
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " create revision = " + kv.getCreateRevision());
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " modify revision = " + kv.getModRevision());
            long leaseId = kv.getLease();
            long ttl = leaseClient.keepAliveOnce(leaseId).get().getTTL();
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " ttl = " + ttl);
            long extendId = leaseClient.grant(30).get().getID();
            PutResponse putResponse = kvClient.put(getByteSeq("/zlock/test/127.0.0.1"), getByteSeq("127.0.0.1"),
                    PutOption.newBuilder().withLeaseId(extendId).build()).get();
//            leaseClient.revoke(leaseId).get();
        }
        List<KeyValue> newkvs = kvClient.get(getByteSeq("/zlock/test/127.0.0.1")).get().getKvs();
        System.out.println("newkv count = " + newkvs.size());
        for (KeyValue newkv : newkvs) {
            System.out.println("newkv " + newkv.getKey().toString(Charsets.UTF_8) + " create revision = " + newkv.getCreateRevision());
            System.out.println("newkv " + newkv.getKey().toString(Charsets.UTF_8) + " modify revision = " + newkv.getModRevision());
            System.out.println("newkv leaseId = " + newkv.getLease());
            System.out.println("newkv ttl = " + leaseClient.keepAliveOnce(newkv.getLease()).get().getTTL());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("duration = " + (endTime - startTime));
    }

    private ByteSequence getByteSeq(String str) {
        return ByteSequence.from(str, Charsets.UTF_8);
    }
}
