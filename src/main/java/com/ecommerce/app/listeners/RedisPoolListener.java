package com.ecommerce.app.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisPoolListener implements ServletContextListener {

    private JedisPool pool;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(50);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWait(Duration.ofSeconds(10));

        pool = new JedisPool(jedisPoolConfig, "localhost", 7000);

        sce.getServletContext().setAttribute("JEDIS_POOL", pool);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        pool.destroy();
    }
}
