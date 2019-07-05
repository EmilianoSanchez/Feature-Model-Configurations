package com.emiliano.fmframework.core.constraints;

import java.io.Serializable;

import com.emiliano.fmframework.core.Configuration;

public interface Constraint extends Serializable{

	public boolean isSatisfied(Configuration configuration);

}
