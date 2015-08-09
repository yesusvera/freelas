package br.com.iejb.sgi.customSearch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author eccardoso
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) 
public @interface CustomSearch {
 	
	boolean like() default true;
	EnumCustomDirection direction() default EnumCustomDirection.BOTH;
 
}