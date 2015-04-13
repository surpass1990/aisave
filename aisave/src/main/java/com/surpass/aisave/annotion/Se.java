package com.surpass.aisave.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用于Object属性注解之上，以解决属性之间的逻辑关系
 * 
 * @author surpassE
 *
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Se {

	/**
	 * key值
	 * @return
	 */
	String name() default "";
	/**
	 * 属性是不是section节点
	 * 默认不是节点
	 * @return
	 */
	boolean isSection() default false;
	
	/**
	 * 如果属性是section的节点，那么在获得section值之前执行preInit方法，如果preInit方法存在
	 * @return
	 */
	String assignedMethod() default "preInit";
	
	/**
	 * 属性的注释信息
	 * @return
	 */
	String comment() default "";
	
	/**
	 * 是否添加空白行
	 * @return
	 */
	boolean blankLine() default false;
	
	/**
	 * 属性值是null时是否保存
	 * 默认保存
	 * @return
	 */
	boolean nullToSave() default true;
	
	/**
	 * 含有@se注解的属性默认都是保存的
	 * @return
	 */
	boolean isSave() default true;
	
	/**
	 * 属性是null、false、
	 * @return
	 */
	IgnoreProperties[] ignoreProperties() default { };
}
