package ot.tch.rpc.provider.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface RPC {

    String name() default "";

}
