import com.martin.utils.HttpUtils;
import com.martin.utils.JsonUtils;

import java.util.Map;

/**
 * @author ZXY
 * @ClassName: Test
 * @Description:
 * @date 2017/2/28 10:23
 */
public class Test {
    public static void main(String[] args) throws Exception {
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-content.xml");
//        PayFlowService math = ctx.getBean("payFlowService", PayFlowService.class);
//        int n1 = 100, n2 = 5;
//        math.addPayFlow(null);
        for (int i = 0; i < 1000; i++) {
            String userName ="";
            if(i<10){
                userName += "00" + i;
            }
            else if(i<100){
                userName += "0" + i;
            }
            System.out.println(userName);
            String url = "http://www.jlwater.com/api.do?act=checkusername&username=" + userName;
            String s = HttpUtils.sendPost(url,"","utf-8");
            Map map = JsonUtils.readMap(s);
            if(map.get("result").equals("0")){
                System.out.println(userName);
                break;
            }

        }
    }


}
