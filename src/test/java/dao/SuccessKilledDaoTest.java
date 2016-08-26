package dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import seckill.dao.SuccessKilledDao;
import seckill.entity.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:config/spring/spring-dao.xml" })
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;

	@Test
	public void testInsertSuccessKilled() {
		long seckillId = 1000L;
		long userPhone = 18610846125L;
		int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
		System.out.println(insertCount);
	}

	@Test
	public void testQueryByIdWithSecKill() {
		long seckillId = 1000L;
		long user_phone = 18610846125L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, user_phone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}

}
