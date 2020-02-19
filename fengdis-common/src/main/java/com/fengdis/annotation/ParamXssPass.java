package com.fengdis.annotation;


import java.lang.annotation.*;

/**
 * @version 1.0
 * @Descrittion: 在Controller方法上加入该注解不会转义参数，
 *  *  如果不加该注解则会：<script>alert(1)</script> --> &lt;script&gt;alert(1)&lt;script&gt;
 * @author: fengdi
 * @since: 2019/09/05 16:05
 */
@Target( { ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface ParamXssPass {
}
