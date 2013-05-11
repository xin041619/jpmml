/*
 * Copyright (c) 2013 University of Tartu
 */
package org.jpmml.evaluator;

import java.util.*;

import org.jpmml.manager.*;

import org.dmg.pmml.*;

public class ExpressionUtil {

	private ExpressionUtil(){
	}

	static
	public Object getValue(FieldName name, ModelManager<?> modelManager, Map<FieldName, ?> parameters){

		// XXX: Should not accept <code>null</code> parameter
		if(modelManager != null){
			DerivedField derivedField = modelManager.resolve(name);

			if(derivedField != null){
				return getValue(derivedField, modelManager, parameters);
			}
		}

		return parameters.get(name);
	}

	static
	public Object getValue(DerivedField derivedField, ModelManager<?> modelManager, Map<FieldName, ?> parameters){
		return getValue(derivedField.getExpression(), modelManager, parameters);
	}

	static
	public Object getValue(Expression expression, ModelManager<?> modelManager, Map<FieldName, ?> parameters){

		if(expression instanceof Constant){
			Constant constant = (Constant)expression;

			String value = constant.getValue();

			DataType dataType = constant.getDataType();
			if(dataType == null){
				dataType = ParameterUtil.getDataType(value);
			}

			return ParameterUtil.parse(dataType, value);
		} else

		if(expression instanceof FieldRef){
			FieldRef fieldRef = (FieldRef)expression;

			return getValue(fieldRef.getField(), modelManager, parameters);
		} else

		if(expression instanceof NormContinuous){
			NormContinuous normContinuous = (NormContinuous)expression;

			Number value = (Number)getValue(normContinuous.getField(), modelManager, parameters);
			if(value == null){
				return normContinuous.getMapMissingTo();
			}

			return NormalizationUtil.normalize(normContinuous, value.doubleValue());
		} else

		if(expression instanceof NormDiscrete){
			NormDiscrete normDiscrete = (NormDiscrete)expression;

			Object value = getValue(normDiscrete.getField(), modelManager, parameters);
			if(value == null){
				return normDiscrete.getMapMissingTo();
			}

			DataType dataType = ParameterUtil.getDataType(value);

			boolean equals = (ParameterUtil.parse(dataType, normDiscrete.getValue())).equals(value);

			return Double.valueOf(equals ? 1.0 : 0.0);
		} else

		if(expression instanceof MapValues){
			MapValues mapValues = (MapValues)expression;

			DataType dataType = mapValues.getDataType();
			if(dataType == null){
				dataType = DataType.STRING; // XXX
			}

			Map<String, Object> values = new LinkedHashMap<String, Object>();

			List<FieldColumnPair> fieldColumnPairs = mapValues.getFieldColumnPairs();
			for(FieldColumnPair fieldColumnPair : fieldColumnPairs){
				Object value = getValue(fieldColumnPair.getField(), modelManager, parameters);
				if(value == null){
					String missingValue = mapValues.getMapMissingTo();
					if(missingValue != null){
						return ParameterUtil.parse(dataType, missingValue);
					}

					return null;
				}

				values.put(fieldColumnPair.getColumn(), value);
			}

			List<Map<String, String>> rows;

			InlineTable table = mapValues.getInlineTable();
			if(table == null){
				TableLocator tableLocator = mapValues.getTableLocator();
				if(tableLocator != null){
					throw new UnsupportedFeatureException(tableLocator);
				}

				rows = Collections.<Map<String, String>>emptyList();
			} else

			{
				rows = TableUtil.parse(table);
			}

			Map<String, String> row = TableUtil.match(rows, values);

			if(row != null){
				String value = row.get(mapValues.getOutputColumn());

				return ParameterUtil.parse(dataType, value);
			} else

			{
				String defaultValue = mapValues.getDefaultValue();
				if(defaultValue != null){
					return ParameterUtil.parse(dataType, defaultValue);
				}

				return null;
			}
		} else

		if(expression instanceof Apply){
			Apply apply = (Apply)expression;

			List<Object> values = new ArrayList<Object>();

			List<Expression> arguments = apply.getExpressions();
			for(Expression argument : arguments){
				Object value = getValue(argument, modelManager, parameters);

				values.add(value);
			}

			return FunctionUtil.evaluate(apply.getFunction(), values);
		}

		throw new UnsupportedFeatureException(expression);
	}
}