package task;

import cn.leancloud.demo.task.LivePolling;
import org.junit.Test;

public class LivePollingTest {
    @Test
    public void testPolling(){
        LivePolling.poll();
    }
}
