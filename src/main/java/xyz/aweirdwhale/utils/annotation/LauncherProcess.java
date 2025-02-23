package xyz.aweirdwhale.utils.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)

public @interface LauncherProcess {
    String value() default "";
}
//We can delete it, never use.