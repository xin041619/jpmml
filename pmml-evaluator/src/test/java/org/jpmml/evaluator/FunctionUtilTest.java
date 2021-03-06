/*
 * Copyright (c) 2013 University of Tartu
 */
package org.jpmml.evaluator;

import java.util.*;

import org.junit.*;

import static org.junit.Assert.*;

public class FunctionUtilTest {

	@Test
	public void evaluateArithmeticFunctions(){
		Double one = Double.valueOf(1d);
		Double three = Double.valueOf(3d);

		assertEquals(Double.valueOf(1d + 3d), evaluate("+", one, three));
		assertEquals(Double.valueOf(1d - 3d), evaluate("-", one, three));
		assertEquals(Double.valueOf(1d * 3d), evaluate("*", one, three));
		assertEquals(Double.valueOf(1d / 3d), evaluate("/", one, three));

		assertEquals(null, evaluate("+", one, null));
		assertEquals(null, evaluate("+", null, three));
	}

	@Test
	public void evaluateValueFunctions(){
		assertEquals(Boolean.TRUE, evaluate("isMissing", (String)null));
		assertEquals(Boolean.FALSE, evaluate("isMissing", "value"));

		assertEquals(Boolean.TRUE, evaluate("isNotMissing", "value"));
		assertEquals(Boolean.FALSE, evaluate("isNotMissing", (String)null));
	}

	@Test
	public void evaluateComparisonFunctions(){
		Double one = Double.valueOf(1d);
		Double three = Double.valueOf(3d);

		assertEquals(Boolean.TRUE, evaluate("equal", one, one));
		assertEquals(Boolean.TRUE, evaluate("notEqual", one, three));

		assertEquals(Boolean.TRUE, evaluate("lessThan", one, three));
		assertEquals(Boolean.TRUE, evaluate("lessOrEqual", one, one));

		assertEquals(Boolean.TRUE, evaluate("greaterThan", three, one));
		assertEquals(Boolean.TRUE, evaluate("greaterOrEqual", three, three));
	}

	@Test
	public void evaluateIfFunction(){
		assertEquals("left", evaluate("if", Boolean.TRUE, "left"));
		assertEquals("left", evaluate("if", Boolean.TRUE, "left", "right"));

		assertEquals(null, evaluate("if", Boolean.FALSE, "left"));
		assertEquals("right", evaluate("if", Boolean.FALSE, "left", "right"));
	}

	static
	private Object evaluate(String name, Object... values){
		return FunctionUtil.evaluate(name, Arrays.asList(values));
	}
}