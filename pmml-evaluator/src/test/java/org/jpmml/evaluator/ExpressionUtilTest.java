/*
 * Copyright (c) 2013 University of Tartu
 */
package org.jpmml.evaluator;

import org.dmg.pmml.*;

import org.junit.*;

import static org.junit.Assert.*;

public class ExpressionUtilTest {

	@Test
	public void evaluateConstant(){
		Constant stringThree = new Constant("3");
		stringThree.setDataType(DataType.STRING);
		assertEquals("3", ExpressionUtil.evaluate(stringThree, null));

		Constant integerThree = new Constant("3");
		integerThree.setDataType(DataType.INTEGER);
		assertEquals(Integer.valueOf(3), ExpressionUtil.evaluate(integerThree, null));

		Constant floatThree = new Constant("3");
		floatThree.setDataType(DataType.FLOAT);
		assertEquals(Float.valueOf(3f), ExpressionUtil.evaluate(floatThree, null));
	}

	@Test
	public void evaluateFieldRef(){
		FieldName name = new FieldName("x");

		FieldRef nameRef = new FieldRef(name);
		assertEquals("3", ExpressionUtil.evaluate(nameRef, new LocalEvaluationContext(name, "3")));
	}

	@Test
	public void evaluateNormContinuous(){
		FieldName name = new FieldName("x");

		NormContinuous expression = new NormContinuous(name);

		expression.setMapMissingTo(5d);

		assertEquals(5d, ExpressionUtil.evaluate(expression, new LocalEvaluationContext(name, null)));
	}

	@Test
	public void evaluateNormDiscrete(){
		FieldName name = new FieldName("x");

		Double equals = Double.valueOf(1.0d);
		Double notEquals = Double.valueOf(0.0d);

		NormDiscrete stringThree = new NormDiscrete(name, "3");
		assertEquals(equals, ExpressionUtil.evaluate(stringThree, new LocalEvaluationContext(name, "3")));
		assertEquals(notEquals, ExpressionUtil.evaluate(stringThree, new LocalEvaluationContext(name, "1")));

		stringThree.setMapMissingTo(5d);

		assertEquals(5d, ExpressionUtil.evaluate(stringThree, new LocalEvaluationContext(name, null)));

		NormDiscrete integerThree = new NormDiscrete(name, "3");
		assertEquals(equals, ExpressionUtil.evaluate(integerThree, new LocalEvaluationContext(name, Integer.valueOf(3))));
		assertEquals(notEquals, ExpressionUtil.evaluate(integerThree, new LocalEvaluationContext(name, Integer.valueOf(1))));

		NormDiscrete floatThree = new NormDiscrete(name, "3.0");
		assertEquals(equals, ExpressionUtil.evaluate(floatThree, new LocalEvaluationContext(name, Float.valueOf(3.0f))));
		assertEquals(notEquals, ExpressionUtil.evaluate(floatThree, new LocalEvaluationContext(name, Float.valueOf(1.0f))));
	}

	@Test
	public void evaluateDiscretize(){
		FieldName name = new FieldName("x");

		Discretize discretize = new Discretize(name);

		assertEquals(null, ExpressionUtil.evaluate(discretize, new LocalEvaluationContext()));
		discretize.setMapMissingTo("Missing");
		assertEquals("Missing", ExpressionUtil.evaluate(discretize, new LocalEvaluationContext()));

		assertEquals(null, ExpressionUtil.evaluate(discretize, new LocalEvaluationContext(name, "3")));
		discretize.setDefaultValue("Default");
		assertEquals("Default", ExpressionUtil.evaluate(discretize, new LocalEvaluationContext(name, "3")));
	}

	@Test
	public void evaluateMapValues(){
		FieldName name = new FieldName("x");

		MapValues mapValue = new MapValues(null);
		(mapValue.getFieldColumnPairs()).add(new FieldColumnPair(name, null));

		assertEquals(null, ExpressionUtil.evaluate(mapValue, new LocalEvaluationContext()));
		mapValue.setMapMissingTo("Missing");
		assertEquals("Missing", ExpressionUtil.evaluate(mapValue, new LocalEvaluationContext()));

		assertEquals(null, ExpressionUtil.evaluate(mapValue, new LocalEvaluationContext(name, "3")));
		mapValue.setDefaultValue("Default");
		assertEquals("Default", ExpressionUtil.evaluate(mapValue, new LocalEvaluationContext(name, "3")));
	}
}