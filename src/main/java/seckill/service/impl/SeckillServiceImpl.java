package seckill.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import seckill.dao.SeckillDao;
import seckill.dao.SuccessKilledDao;
import seckill.dto.Exposer;
import seckill.dto.SeckillExcution;
import seckill.entity.Seckill;
import seckill.entity.SuccessKilled;
import seckill.enums.SeckillStateEnum;
import seckill.exception.RepeatKillException;
import seckill.exception.SeckillCloseException;
import seckill.exception.SeckillException;
import seckill.service.SeckillService;

import java.util.Date;
import java.util.List;

/**
 * Created by gaoshiqi on 2016/8/26.
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    // md5盐值字符串，用于混淆MD5
    private final String slat = "ABCDEFGhijklmn123456!@#$%^";

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> list = seckillDao.queryAll(0, 3);
        return list;
    }

    @Override
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        return seckill;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点
     * 1、开发团队达成一致约定，明确标注事务方法的编程风格
     * 2、保证事务方法的的执行时间尽可能的短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
     * 3、不是所有方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public SeckillExcution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrites");
        }
        // 执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();
        try {
            int updateCount = seckillDao.reduceNum(seckillId, nowTime);
            if (updateCount <= 0) {
                // 没有更新到记录，秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                // 唯一:seckillId,userPhone
                if (insertCount <= 0) {
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (RepeatKillException e1) {
            throw e1;
        } catch (SeckillCloseException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常，转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
