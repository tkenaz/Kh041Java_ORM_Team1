package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    String mappedBy() default "";

//    CascadeType[] cascade() default {};

    boolean orphanRemoval() default false;

    //FetchType fetch() default FetchType.LAZY; //in future development
}
