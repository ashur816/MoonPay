import com.martin.service.impl.PayFlowService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ZXY
 * @ClassName: Test
 * @Description:
 * @date 2017/2/28 10:23
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-content.xml");
        PayFlowService math = ctx.getBean("payFlowService", PayFlowService.class);
        int n1 = 100, n2 = 5;
        math.addPayFlow(null);
    }


}
