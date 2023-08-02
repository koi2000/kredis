package com.koi.kredis.aof;

import com.koi.kredis.RedisCore;
import com.koi.kredis.command.Command;
import com.koi.kredis.command.CommandFactory;
import com.koi.kredis.command.WriteCommand;
import com.koi.kredis.resp.Resp;
import com.koi.kredis.resp.RespArray;
import com.koi.kredis.util.Format;
import com.koi.kredis.util.PropertiesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.log4j.Logger;

import javax.swing.text.Segment;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * @author koi
 * @date 2023/7/30 12:24
 */
public class Aof {
    private static final Logger LOGGER = Logger.getLogger(Aof.class);

    private static final String suffix = ".aof";

    /**
     * 1,经过大量测试，使用过3年以上机械磁盘的最大性能为26
     * 2,存盘偏移量，控制单个持久化文件大小
     */
    public static final int shiftBit = 26;

    private Long aofPutIndex = 0L;

    private String fileName = PropertiesUtil.getAofPath();

    private RingBlockingQueue<Resp> runtimeRespQueue = new RingBlockingQueue<>(8888, 888888);

    ByteBuf bufferPolled = new PooledByteBufAllocator().buffer(8888, 2147483647);

    private ScheduledThreadPoolExecutor persistenceExecutor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Aof_Single_Thread");
        }
    });

    private final RedisCore redisCore;

    final ReadWriteLock reentrantLock = new ReentrantReadWriteLock();

    public Aof(RedisCore redisCore) {
        this.redisCore = redisCore;
        File file = new File(this.fileName + suffix);
        if (!file.isDirectory()) {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
        start();
    }

    public void put(Resp resp) {
        runtimeRespQueue.offer(resp);
    }

    public void start() {
        /**
         * 谁先执行需要顺序异步执行
         */
        persistenceExecutor.execute(this::pickupDiskDataAllSegment);
        persistenceExecutor.scheduleAtFixedRate(this::downDiskAllSegment, 10, 1, TimeUnit.SECONDS);
    }

    public void close() {
        try {
            persistenceExecutor.shutdown();
        } catch (Exception exp) {
            LOGGER.warn("Exception!", exp);
        }
    }

    // 面向过程分段存储所有数据
    public void downDiskAllSegment() {
        if (reentrantLock.writeLock().tryLock()) {
            try {
                long segmentId = -1;
                long segmentGroupHead = -1;
                // 池化内存
                Segment:
                while (segmentId != (aofPutIndex >> shiftBit)) {
                    // 要后26位
                    segmentId = (aofPutIndex >> shiftBit);
                    // 获得文件的随机读写
                    RandomAccessFile randomAccessFile = new RandomAccessFile(fileName + "_" + segmentId + suffix, "rw");
                    FileChannel channel = randomAccessFile.getChannel();
                    long len = channel.size();
                    int putIndex = Format.uintNbit(aofPutIndex, shiftBit);
                    long baseOffset = aofPutIndex - putIndex;
                    if (len - putIndex < 1L << (shiftBit - 2)) {
                        len = segmentId + 1 << (shiftBit - 2);
                    }
                    // 获得一个文件到内存的映射
                    MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, len);
                    do {
                        // 从队列里拿出指令
                        Resp resp = runtimeRespQueue.peek();
                        if (resp == null) {
                            clean(mappedByteBuffer);
                            randomAccessFile.close();
                            break Segment;
                        }
                        // 命令写入bufferpool
                        Resp.write(resp, bufferPolled);
                        // 当前已经在bufferpool中的命令
                        int respLen = bufferPolled.readableBytes();
                        //
                        if (mappedByteBuffer.capacity() <= respLen + putIndex) {
                            len += 1L << (shiftBit - 3);
                            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, len);
                            if (len > (1 << shiftBit)) {
                                bufferPolled.release();
                                aofPutIndex = baseOffset + 1 << shiftBit;
                                break;
                            }
                        }
                        while (respLen > 0) {
                            respLen--;
                            mappedByteBuffer.put(putIndex++, bufferPolled.readByte());
                        }
                        // 完成消费
                        aofPutIndex = baseOffset + putIndex;
                        runtimeRespQueue.poll();
                        bufferPolled.clear();
                        if (len - putIndex < (1L << (shiftBit - 3))) {
                            len += 1L << (shiftBit - 3);
                            if (len > (1 << shiftBit)) {
                                bufferPolled.release();
                                clean(mappedByteBuffer);
                                aofPutIndex = baseOffset + 1 << shiftBit;
                                break;
                            }
                            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, len);
                        }
                    } while (true);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                LOGGER.error("aof IOException ", e);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                LOGGER.error("aof Exception", e);
            } finally {
                reentrantLock.writeLock().unlock();
            }
        }
    }

    // 分段拾起所有数据
    public void pickupDiskDataAllSegment() {
        if (reentrantLock.writeLock().tryLock()) {
            try {
                long segmentId = -1;
                Segment:
                while (segmentId != (aofPutIndex >> shiftBit)) {
                    // 只要后26位
                    RandomAccessFile randomAccessFile = new RandomAccessFile(fileName + "_" + segmentId + suffix, "r");
                    FileChannel channel = randomAccessFile.getChannel();
                    long len = channel.size();
                    int putIndex = Format.uintNbit(aofPutIndex, shiftBit);
                    long baseOffset = aofPutIndex - putIndex;
                    MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, len);
                    ByteBuf bufferPolled = new PooledByteBufAllocator().buffer((int) len);
                    bufferPolled.writeBytes(mappedByteBuffer);

                    do {
                        Resp resp = null;
                        try {
                            resp = Resp.decode(bufferPolled);
                        } catch (Exception e) {
                            clean(mappedByteBuffer);
                            randomAccessFile.close();
                            bufferPolled.release();
                            break Segment;
                        }
                        Command command = CommandFactory.from((RespArray) resp);
                        WriteCommand writeCommand = (WriteCommand) command;
                        assert writeCommand != null;
                        writeCommand.handle(this.redisCore);
                        putIndex = bufferPolled.readerIndex();
                        aofPutIndex = putIndex + baseOffset;
                        if (putIndex > (1 << shiftBit)) {
                            bufferPolled.release();
                            clean(mappedByteBuffer);
                            aofPutIndex = baseOffset + 1 << shiftBit;
                            break;
                        }
                    } while (true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.writeLock().unlock();
            }
        }
    }

    public static void clean(final MappedByteBuffer buffer) throws Exception {
        if (buffer == null) {
            return;
        }
        buffer.force();
        //Privileged特权
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                // System.out.println(buffer.getClass().getName());
                Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                getCleanerMethod.setAccessible(true);
                sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                cleaner.clean();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
