package annotations;

import enums.GenerationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Id {
    GenerationType strategy() default GenerationType.SEQUENCE;
<<<<<<<< HEAD:src/main/java/annotations/Id.java

    String name();
}
========
}
>>>>>>>> fcb955c0ff6f02b35a6b48ffabf0104e47289ce0:src/main/java/annotations/GeneratedValue.java
