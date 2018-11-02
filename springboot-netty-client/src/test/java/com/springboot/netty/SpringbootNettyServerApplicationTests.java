package com.springboot.netty;

import com.springboot.netty.client.NettyClient;
import com.springboot.netty.model.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootNettyServerApplicationTests {

    @Autowired
    private NettyClient nettyClient;

    @Test
    public void contextLoads() {
        Message message = Message.build().setId(1).setMsg("test 11111");
        nettyClient.startClient(message);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
