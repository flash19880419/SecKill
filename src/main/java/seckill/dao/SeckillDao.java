package seckill.dao;

import org.apache.ibatis.annotations.Param;
import seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by gaoshiqi on 2016/8/26.
 */
public interface SeckillDao {
    /**
     * 减少库存
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNum(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
