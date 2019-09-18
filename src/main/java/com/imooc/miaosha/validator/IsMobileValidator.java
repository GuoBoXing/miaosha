package com.imooc.miaosha.validator;

import com.imooc.miaosha.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 必须让注解实现这样的接口，第一个是注解接口，第二个是注解修饰字段的类型
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean  required  = false;
    //初始化方法
    @Override
    public void initialize(IsMobile isMobile) {
        required = isMobile.required();
    }
    //这里的value就是传进啦的值
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return ValidatorUtil.isMobile(value);
        } else {
            if (StringUtils.isEmpty(value)){
                return true;
            } else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
