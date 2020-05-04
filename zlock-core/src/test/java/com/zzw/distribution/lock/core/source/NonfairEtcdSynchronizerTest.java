/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzw.distribution.lock.core.source;

import com.google.common.base.Charsets;
import com.zzw.distribution.lock.core.synchronizer.EtcdNonfairSynchronizer;
import com.zzw.distribution.lock.core.synchronizer.ZlockSynchronizer;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.PutOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * {@link EtcdNonfairSynchronizer} 锁测试类
 *
 * @author zhaozhiwei
 * @date 2019/11/30 1:01 下午
 * @see EtcdNonfairSynchronizer
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NonfairEtcdSynchronizerTest {

    private ZlockSynchronizer sync;
    private String lockName = "etcdTryAcquireLock";
    private String uuid;

    @BeforeAll
    public void beforeAll() {
        EtcdSource etcdSource = new EtcdSource("http://127.0.0.1:2379");
        sync = new EtcdNonfairSynchronizer(etcdSource.getClient(), lockName);
        uuid = sync.getUuid();
    }

    @Test
    public void tryAcquireTest() throws InterruptedException, ExecutionException {
        boolean result = sync.tryAcquire(1);
        Client client = Client.builder().endpoints("http://127.0.0.1:2379").build();
        KV kvClient = client.getKVClient();
        List<KeyValue> kvs = kvClient.get(getByteSeq("/zlock/" + lockName + "/" + uuid)).get().getKvs();
        for (KeyValue kv : kvs) {
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " create revision = " + kv.getCreateRevision());
            System.out.println(kv.getKey().toString(Charsets.UTF_8) + " modify revision = " + kv.getModRevision());
        }
        Assertions.assertTrue(result);
        Assertions.assertEquals(1, kvs.size());
    }

    @Test
    public void releaseTest() {
        boolean release = sync.release(1);
        Assertions.assertTrue(release);
    }

    @Test
    public void extendTest() {

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
