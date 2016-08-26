package seckill.exception;

/**
 * Created by gaoshiqi on 2016/8/26.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillException(String message) {
        super(message);
    }
}
