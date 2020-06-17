package top.dcenter.security.social.repository.jdbc;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author zyw
 * @version V1.0  Created by 2020/6/14 21:07
 */
@Component("userIdKeyGenerator")
public class UserIdKeyGenerator extends BaseKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = (String) super.generate(target, method, params);
        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        return sb.toString();
    }
}
