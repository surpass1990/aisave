package com.surpass.aisave.annotion;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ })
@Retention(RUNTIME)
public @interface IgnoreProperties {

	/**
	 * 如果是0(-1或是其他自定义的值)表示忽略params定义的属性值
	 * @return
	 */
	String flag() default "-1";
	
	/**
	 * 不需要操作的属性值
	 * @return
	 */
	String[] params();
	
	
}
