package com.fengdis.component.rpc.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @version 1.0
 * @Descrittion: redis缓存配置类（关于Redis的配置方式有很多，我知道有1.自动配置；2.手动配置；3.传统的xml文件也是可以的。在这里为第2中，第二种比较灵活，符合springBoot风格。)
 *               自动配置用如下注解
 *               @ConditionalOnClass(RedisOperations.class)
 *               @EnableConfigurationProperties(RedisProperties.class)
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Configuration
@EnableCaching
@ConditionalOnExpression("${redis.enabled:false}")
public class RedisConfig extends CachingConfigurerSupport {

    /*@Autowired
    private JedisConnectionFactory jedisConnectionFactory;*/

    /**
     * @description 自定义的缓存key的生成策略
     *              若想使用这个key只需要讲注解上keyGenerator的值设置为keyGenerator即可</br>
     * @return 自定义策略生成的key
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(){
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuffer sb = new StringBuffer();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for(Object obj:params){
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /*@Bean
    @Override
    public KeyGenerator keyGenerator() {
        //  设置自动key的生成规则，配置spring boot的注解，进行方法级别的缓存
        // 使用：进行分割，可以很多显示出层级关系
        // 这里其实就是new了一个KeyGenerator对象，只是这是lambda表达式的写法，我感觉很好用，大家感兴趣可以去了解下
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(":" + String.valueOf(obj));
            }
            String rsToUse = String.valueOf(sb);
            lg.info("自动生成Redis Key -> [{}]", rsToUse);
            return rsToUse;
        };
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        // 初始化缓存管理器，在这里我们可以缓存的整体过期时间什么的，我这里默认没有配置
        lg.info("初始化 -> [{}]", "CacheManager RedisCacheManager Start");
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(jedisConnectionFactory);
        return builder.build();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)).entryTtl(Duration.ofDays(1));
        return configuration;
    }*/

    /**
     * 初始化缓存管理器
     * 设置缓存的整体过期时间（默认1天）、设置@cacheable 序列化方式
     * 最常见的@Cacheable、@CachePut、@CacheEvict等默认都可以配合CacheManager使用
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        //Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        FastJson2JsonRedisSerializer jackson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);

        // 配置序列化、过期时间等
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues();

        /**
         * (自定义fastJson序列化器需要开启AutoType)
         */
        // 全局开启AutoType，不建议使用
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // 建议使用这种方式，小范围指定白名单
        /*ParserConfig.getGlobalInstance().addAccept("me.zhengjie.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.system.service.dto");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.system.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.quartz.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.monitor.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.security.security");*/

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
        return cacheManager;

        //return RedisCacheManager.create(redisConnectionFactory);
    }

    /**
     * RedisTemplate配置
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory ) {
        //设置序列化
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        //Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        FastJson2JsonRedisSerializer jackson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);

        /*ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);*/

        //配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer);//key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);//value序列化
        redisTemplate.setHashKeySerializer(stringSerializer);//Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);//Hash value序列化


        /**
         * (自定义fastJson序列化器需要开启AutoType)
         */
        // 全局开启AutoType，不建议使用
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // 建议使用这种方式，小范围指定白名单
        /*ParserConfig.getGlobalInstance().addAccept("me.zhengjie.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.system.service.dto");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.system.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.quartz.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.monitor.domain");
        ParserConfig.getGlobalInstance().addAccept("me.zhengjie.modules.security.security");*/

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}