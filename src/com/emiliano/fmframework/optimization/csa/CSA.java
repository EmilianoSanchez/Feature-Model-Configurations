package com.emiliano.fmframework.optimization.csa;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.FeatureModel;
import com.emiliano.fmframework.core.FeatureState;

import com.emiliano.fmframework.operations.ConfOperations;

import com.emiliano.fmframework.optimization.csa.constraintPropagator.ConstraintPropagator;
import com.emiliano.fmframework.optimization.csa.constraintPropagator.ConstraintPropagators;


public abstract class CSA {
	
	public static List<Configuration> searchAllSolutions(FeatureModel model) {
		List<Configuration> solutions=new LinkedList<>();
		Configuration initialConfiguration = ConfOperations.getPartialConfiguration(model);
		Stack<Configuration> open=new Stack<>();
		ConstraintPropagator cpropagator = ConstraintPropagators.clauseBasedConstraintPropagator;
		if(initialConfiguration!=null){
			open.push(initialConfiguration);
			
			while (!open.isEmpty()) {
				Configuration current = open.pop();
				int unassignedVariable = selectUnassignedVariable(current);
				if (unassignedVariable == NO_UNASSIGNED_VARIABLES) {
					solutions.add(current);
				} else {
					Configuration succesor1 = (Configuration) current.clone();
					if (cpropagator.assignFeature(succesor1, unassignedVariable, FeatureState.SELECTED)) {
						open.push(succesor1);
					}
					Configuration succesor2 = (Configuration) current.clone();
					if (cpropagator.assignFeature(succesor2, unassignedVariable, FeatureState.DESELECTED)) {
						open.push(succesor2);
					}
				}
			}
		}
		return solutions;
	}
	
	private static int selectUnassignedVariable(Configuration conf) {
		for(int i=0;i<conf.getNumFeatures();i++){
			if(conf.getFeatureState(i)==FeatureState.UNSELECTED)
				return i;
		}
		return NO_UNASSIGNED_VARIABLES;
	}	
	
	private static final int NO_UNASSIGNED_VARIABLES = -1;
}
