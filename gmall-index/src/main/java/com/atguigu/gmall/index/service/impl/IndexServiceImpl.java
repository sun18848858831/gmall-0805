package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author shkstart
 * @create 2020-01-13 19:21
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final String KEY_PREFIX = "index:cates:";

    @Override
    public List<CategoryEntity> queryLvl1Categories() {

        Resp<List<CategoryEntity>> categoriesResp = this.pmsClient.queryCategoriesByLevelOrPid(1, null);
        List<CategoryEntity> categoryEntities = categoriesResp.getData();

        return categoryEntities;
    }

    @Override
    @GmallCache(value = "index:cates:",timeout = 7200,bound = 100,lockName = "lock")
    public List<CategoryVO> queryCategoresWithSub(Long pid) {

        //获取缓存中的数据
        /*String cateJson = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);

        //有,直接返回
        if(StringUtils.isNotEmpty(cateJson)){
            return JSON.parseArray(cateJson, CategoryVO.class);
        }

        //加分布式锁
        RLock lock = this.redissonClient.getLock("lock"+pid);
        lock.lock();

        String cateJson2 = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //有，直接返回
        if(StringUtils.isNotEmpty(cateJson2)){
            lock.unlock();
            return JSON.parseArray(cateJson2,CategoryVO.class);
        }*/

        //没有，远程调用查询
        Resp<List<CategoryVO>> listResp = this.pmsClient.queryCategoriesWithSub(pid);
        List<CategoryVO> vos = listResp.getData();


        //查询完成之后放入缓存
        /*this.stringRedisTemplate.opsForValue().set(KEY_PREFIX+pid,JSON.toJSONString(vos),3+new Random().nextInt(5), TimeUnit.DAYS);

        lock.unlock();*/

        return vos;
    }

    /*public synchronized void testLock(){
        String numString = this.stringRedisTemplate.opsForValue().get("num");
        if(numString==null){
            return;
        }
        Integer num = new Integer(numString);
        this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));
    }*/

    public void testLock(){

        //加锁
        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        String numString = this.stringRedisTemplate.opsForValue().get("num");
        if(numString==null){
            return;
        }
        Integer num = new Integer(numString);
        this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));
        lock.unlock();

    }

    public void testLock1(){

        //所有请求执行setnx，返回值为true，说明获取到锁
        String uuid = UUID.randomUUID().toString();
        Boolean flag = this.stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,5,TimeUnit.SECONDS);
        //如果返回值值true，执行业务逻辑
        if(flag){
            String numString = this.stringRedisTemplate.opsForValue().get("num");
            if(numString==null){
                return;
            }
            Integer num = new Integer(numString);
            this.stringRedisTemplate.opsForValue().set("num",String.valueOf(++num));
            //执行完业务逻辑之后，要释放锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            this.stringRedisTemplate.execute(new DefaultRedisScript<>(script,Long.class), Arrays.asList("lock"),uuid);
            /*String lock = this.stringRedisTemplate.opsForValue().get("lock");
            if(StringUtils.equals(lock,uuid)){
                this.stringRedisTemplate.delete("lock");
            }*/
        }else {
            //如果没有获取到锁，重试
            try {
                TimeUnit.SECONDS.sleep(1);
                this.testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
