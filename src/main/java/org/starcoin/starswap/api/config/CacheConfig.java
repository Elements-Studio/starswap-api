package org.starcoin.starswap.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);


    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 方案一(常用)：定制化缓存Cache
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(10_000))
        // 如果缓存种没有对应的value，通过createExpensiveGraph方法同步加载  buildAsync是异步加载
        //.build(key -> createExpensiveGraph(key))
        ;

        // 方案二：传入一个CaffeineSpec定制缓存，它的好处是可以把配置方便写在配置文件里
        //cacheManager.setCaffeineSpec(CaffeineSpec.parse("initialCapacity=50,maximumSize=500,expireAfterWrite=5s"));
        return cacheManager;
    }

    @CacheEvict(allEntries = true, cacheNames = {
            "tokenToUsdExchangeRateCache",
            "farmingTvlInUsdCache",
            "syrupPoolTvlInUsdCache",
            "shortestIndirectSwapPathCache",
            "allTokensCache",
            "allLiquidityTokensCache",
            "allLiquidityPoolsCache",
            "allLiquidityTokenFarmsCache",
            "allSyrupPoolsCache",
            "tokenExchangeRateCache",
            "sumReservesGroupByTokenCache"
    })
    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void evictCachesFast() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Evict Caches at " + Instant.now().atZone(ZoneId.of("UTC")));
        }
    }

    @CacheEvict(allEntries = true, cacheNames = {
            "tokenSwapRouterGetReservesCache"
    })
    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void evictCachesVeryFast() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Evict Caches at " + Instant.now().atZone(ZoneId.of("UTC")));
        }
    }

}

