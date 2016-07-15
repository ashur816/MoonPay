import com.martin.bean.PutRuleBean;
import com.martin.bean.VoucherBean;
import com.martin.service.impl.RuleService;
import com.martin.service.impl.VoucherService;
import com.martin.utils.RandomUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: Test
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author ZXY
 * @date 2016/7/14 17:22
 */
public class AATest extends BaseJunit4Test {
    @Resource
    private RuleService ruleService;

    @Resource
    private VoucherService voucherService;

    @Test
    public void cc() throws Exception {
        int policyId = 1;
        List<PutRuleBean> beanList = ruleService.getPutRules(policyId);
        PutRuleBean putRuleBean = null;
        Set<Long> set = new HashSet<>();
        Long voucherId = 0L;
        for (int i = 0; i < beanList.size(); i++) {
            putRuleBean = beanList.get(i);
            int a = putRuleBean.getFaceValue();
            int b = putRuleBean.getInitialAmount();
            for (int j = 0; j < b; j++) {
                while (true) {
                    voucherId = Long.parseLong(RandomUtils.generateRandomNum(10));
                    if (!set.contains(voucherId)) {
                        break;
                    }
                }
                set.add(voucherId);
                VoucherBean voucherBean = new VoucherBean();
                voucherBean.setVoucherId(voucherId);
                voucherBean.setFaceValue(a);
                voucherBean.setPolicyId(policyId);
                voucherService.createVoucher(voucherBean);
            }
        }
    }
}
