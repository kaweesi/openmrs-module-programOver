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
package org.openmrs.module.programOver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.module.programOver.GlobalProperties;


/**
 *
 */
public class QueryUtility {
	public static String createPatientMaxEncounterQuery(int patientId,Date endDate){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb = new StringBuffer();		
		
			sb.append(" select cast(max(encounter_datetime)as DATE) from encounter");
			sb.append(" where (select(cast(max(encounter_datetime)as Date))) <= '" + df.format(endDate)+" '");			
			sb.append("  and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "+ patientId+ "");			
			return sb.toString();
		
	}
	public static String CreatePatientMaxReturnVisitDay(int patientId,Date endDate){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb = new StringBuffer();		
		
			sb.append(" select cast(max(value_datetime) as DATE ) from obs");
			sb.append(" where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)+ "' ");
			sb.append(" and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())+" ");
			sb.append("  and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = " + patientId+ "");			
			return sb.toString();
		
	}

}
