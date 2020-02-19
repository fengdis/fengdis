package com.fengdis.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * @version 1.0
 * @Descrittion: 序列工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class GenerationSequenceUtils {


    /**
     * 生成4位随机数
     * @param charCount
     * @return
     */
    public static String getRandNum(int charCount) {
        String charValue = "";
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;
    }
    private static  int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }


    /**
     * 生成UUID
     * @param prefix 前缀
     * @return
     */
    public static String generateUUID(String prefix){
        String uuid = (UUID.randomUUID().toString()).replaceAll("-","");
        if (!ComUtils.isEmpty(prefix)) {
            uuid = prefix + "-" + uuid;
        }
        return uuid;
    }

    public static void main(String[] args) {
        for(int i=0;i<50;i++){
            System.out.println(GenerationSequenceUtils.SnowflakeIdWorker.getNextValue());
        }
        //System.out.println(generateUUID("menu"));
    }

    /** 机器码 加 进程号 会导致生成的序列号很长, 基于这两个值做一些截取 */
    private static final String MP;
    /** 截取长度: 从最后面开始截取 */
    private static final int               MP_LEN                = 6;
    /** 日期格式化,是线程安全的 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER   = DateTimeFormatter.ofPattern( "yyyyMMddHHmmssSSS" );
    /** 原子类 */
    private static final LongAdder LONG_ADDER            = new LongAdder();
    private static final Long              INITIAL_VALUE         = 1000000L;
    private static final Long              NEXT_NUMBER_MAX_LIMIT = 9999999L;


    static {
        try {
            LONG_ADDER.add( INITIAL_VALUE );
            // 机器码 --> 本机 mac 地址的 hashcode 值
            int machineIdentifier = createMachineIdentifier();
            // 进程号 --> 当前运行的 jvm 进程号的 hashcode 值
            int    processIdentifier = createProcessIdentifier();
            String mp                = Integer.toString( Math.abs( ( machineIdentifier + "" + processIdentifier ).hashCode() ) );
            MP = ( mp.length() > MP_LEN ) ? mp.substring( mp.length() - MP_LEN , mp.length() ) : mp;
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * (机器码 + 进程号) + 随机数 + 时间 + 计数器
     *
     * @return 全局唯一ID
     */
    public static String globalUniqueId () {
        return globalUniqueId( 10 );
    }

    /**
     * (机器码 + 进程号) + 随机数 + 时间 + 计数器 + 用户ID
     *
     * @param userId : 用户ID
     * @return 全局唯一ID
     */
    public static String globalUniqueId ( final String userId ) {
        return globalUniqueId() + userId;
    }


    /**
     * (机器码 + 进程号) + 随机数 + 时间 + 计数器
     *
     * @param count 随机数长度
     * @return 全局唯一ID
     */
    private static String globalUniqueId ( final int count ) {
        // 随机数,该工具类是线程安全的
        final String randomNumber = RandomStringUtils.randomNumeric( count );
        // 时间
        final String now = DATE_TIME_FORMATTER.format( LocalDateTime.now() );
        // 下一个数
        final long nextNumber = getNextNumber();
        return MP + Thread.currentThread().getId() + randomNumber + now + nextNumber;
    }

    /**
     * 自增数
     */
    private static long getNextNumber () {
        final long next = LONG_ADDER.longValue();
        if ( next >= NEXT_NUMBER_MAX_LIMIT ) {
            LONG_ADDER.reset();
            LONG_ADDER.add( INITIAL_VALUE );
        }
        LONG_ADDER.increment();
        return next;
    }


    // 创建机器标识符
    private static int createMachineIdentifier () {
        // build a 2-byte machine piece based on NICs info
        int machinePiece;
        try {
            StringBuilder                   stringBuilder     = new StringBuilder();
            Enumeration< NetworkInterface > networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while ( networkInterfaces.hasMoreElements() ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                stringBuilder.append( networkInterface.toString() );
                byte[] mac = networkInterface.getHardwareAddress();
                if ( mac != null ) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap( mac );
                    try {
                        stringBuilder.append( byteBuffer.getChar() );
                        stringBuilder.append( byteBuffer.getChar() );
                        stringBuilder.append( byteBuffer.getChar() );
                    } catch ( BufferUnderflowException shortHardwareAddressException ) { //NOPMD
                        // mac with less than 6 bytes. continue
                    }
                }
            }
            machinePiece = stringBuilder.toString().hashCode();
        } catch ( Throwable t ) {
            // exception sometimes happens with IBM JVM, use random
            machinePiece = new SecureRandom().nextInt();
        }
        return machinePiece;
    }

    // Creates the process identifier. This does not have to be unique per class loader because
    // NEXT_COUNTER will provide the uniqueness.
    // 创建进程标识符。这并不是每个类装入器,因为必须是唯一的
    private static int createProcessIdentifier () {
        int processId;
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            if ( processName.contains( "@" ) ) {
                processId = Integer.parseInt( processName.substring( 0 , processName.indexOf( '@' ) ) );
            } else {
                processId = processName.hashCode();
            }
        } catch ( Throwable t ) {
            processId = new SecureRandom().nextInt();
        }
        return processId;
    }

    /**
     * 雪花算法生成唯一id
     */
    static class SnowflakeIdWorker{
        private static long workerId =new Random().nextInt(-1 ^ (-1 << 5L));

        private static long datacenterId=0L;

        private static long sequence = 0L;

        private static long twepoch = 1288834974657L;

        private static long workerIdBits = 5L;
        private static long datacenterIdBits = 5L;
        private static long sequenceBits = 12L;

        private static long workerIdShift = sequenceBits;
        private static long datacenterIdShift = sequenceBits + workerIdBits;
        private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        private static long sequenceMask = -1L ^ (-1L << sequenceBits);

        private static long lastTimestamp = -1L;

        public static synchronized long getNextValue() {
            long timestamp = timeGen();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format(
                        "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift) | sequence;
        }

        protected static long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        protected static long timeGen() {
            return System.currentTimeMillis();
        }
    }

}
