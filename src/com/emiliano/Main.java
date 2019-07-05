package com.emiliano;

import java.io.File;
import java.util.List;

import com.emiliano.fmframework.core.FeatureModel;
import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.optimization.csa.CSA;

import fm.FeatureModelException;

public class Main {
	public static void main(String[] args) throws FeatureModelException {

		File file = new File("SPLOT-models/model-aircraft_fm.xml");
		
		FeatureModel model = SXFMtoFM.parse(file.getPath(), true);
		List<Configuration> confs = CSA.searchAllSolutions(model);

		for(int featureId=0;featureId<model.getNumFeatures();featureId++) {
			System.out.print(model.getFeature(featureId).getName()+",");
		}
		System.out.println();
		for(Configuration conf: confs) {
			for(int featureId=0;featureId<conf.getNumFeatures();featureId++) {
				System.out.print(conf.getFeatureState(featureId)+",");
			}
			System.out.println();
		}
		
	}
}
