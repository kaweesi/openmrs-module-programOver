/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.programOver.web.controller;

import org.openmrs.module.programOver.GlobalProperties;

/**
 *
 */
public class ProgramCriteraList {
	
	public String[] getAllCriteriaBasedOnProgram(int programId) {
		
		String[] PMTCTcriteria = { "PatientCurrentlyinpmtct", "PMTCTpatientbetweenthereportingperiod",
		        "PositivePatientsInPMTCT", "NegativePatientsInPMTCT", "CPNPatientsBetweenReportingPeriod",
		        "PatientsWhoGaveBirthBetweenTheReportingPeriod", "PatientsExpectedInPMTCTBetweenTheReportingPeriod" };
		String[] criteriaWithParameters = { "ARVpatients", "prophylaxiPatients", "patientsOnFirstLine",
		        "patientsInSecondLine", "cumulativePatientsOnARVs", "cumulativePatientsOnProphylaxis", "newPatientsOnARVs",
		        "newPatientsOnProphylaxis", "transferredPatientsBetweenTwoDates", "diedPatientsBetweenTwoDates",
		        "consultedPatientsBetweenTwoDates", "patientsWhocameForpharmacyVisitBetweenTwoDates",
		        "patientsEnrolledInAProgram", "patientsOnARVWithoutVisitingPharmacyForXDays",
		        "patientsWithoutCD4CountsForXDays", "patientsCurrentlyActive", "patientsEligibleOnARVsButNotYetStarted",
		        "lostOnFollowUpPatientsOnARVsRegimen", "lostOnFollowUpPatients" };
		String[] HIVExposureCriteria = {};
		
		String[] criteriaBasedOnProgram = null;
		
//		if (programId == 1) {
//		criteriaBasedOnProgram= new String[PMTCTcriteria.length];
//		for (int i = 0; i < PMTCTcriteria.length; i++){
//			criteriaBasedOnProgram[i] = PMTCTcriteria[i];
//			
//		}
//		
//	} 
	if (programId == Integer.parseInt(GlobalProperties.gpGetHIVProgramId())){
		
		
		criteriaBasedOnProgram= new String[criteriaWithParameters.length];
		for (int i = 0; i < criteriaWithParameters.length; i++){
			
			
			criteriaBasedOnProgram[i] = criteriaWithParameters[i];
			
		}
		
	}
	
//	if(programId != Integer.parseInt(GlobalProperties.gpGetHIVProgramId()))
//	{
//		criteriaBasedOnProgram = null;
//	}
//	if (programId==3) {
//		
//		
//		
//        
//    }
	return  criteriaBasedOnProgram;
		
	}
	
}
