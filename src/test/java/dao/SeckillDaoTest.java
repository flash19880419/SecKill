package dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import seckill.dao.SeckillDao;
import seckill.entity.Seckill;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:config/spring/spring-dao.xml" })
public class SeckillDaoTest {

	@Autowired
	private SeckillDao seckillDao;

	@Test
	public void testQueryById() {
		long id = 1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getNumber());
		System.out.println(seckill);
	}

	@Test
	public void testQueryAll() {
		List<Seckill> list = seckillDao.queryAll(0, 100);
		for(int i = 0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}

	@Test
	public void testReduceNum() {
		Date killDate = new Date();
		int updateCount = seckillDao.reduceNum(1000, killDate);
		System.out.println(updateCount);
	}

}
