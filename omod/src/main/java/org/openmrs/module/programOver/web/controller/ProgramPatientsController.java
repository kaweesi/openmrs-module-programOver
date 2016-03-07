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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.programOver.advice.UsageStatsUtils;
import org.openmrs.module.programOver.db.hibernate.ProgramOverviewDAOimpl;
import org.openmrs.module.programOver.service.ProgramOverviewService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 *
 */
public class ProgramPatientsController {
	
	public static List<Object[]> checkTypeController(String programIdKey, int programId, Date startDate, Date endDate,
	                                                 String gender, Date minAge, Date maxAge, int numberOfMonths) {
		
		List<Patient> patients = new ArrayList<Patient>();
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		ProgramOverviewService service = Context.getService(ProgramOverviewService.class);
		if (programIdKey.equals("diedPatientsBetweenTwoDates")) {
			listPatientHistory = service.getPatientWhoDied(programId, startDate, endDate, gender, minAge, maxAge);
		} else if (programIdKey.equals("transferredPatientsBetweenTwoDates")) {
			listPatientHistory = service.getAllTransferedPatient(programId, startDate, endDate, gender, minAge, maxAge);
		}
		
		else if (programIdKey.equals("patientsWhocameForpharmacyVisitBetweenTwoDates")) {
			listPatientHistory = service.getAllPatientPharmacyVisit(programId, startDate, endDate, gender, minAge, maxAge);
		}
		
		else if (programIdKey.equals("consultedPatientsBetweenTwoDates")) {
			listPatientHistory = service.getAllConsultedPatient(programId, startDate, endDate, gender, minAge, maxAge);
		} else if (programIdKey.equals("ARVpatients")) {
			listPatientHistory = service.getAllARVPatients(programId, startDate, endDate, gender, minAge, maxAge);
		}
		
		else if (programIdKey.equals("prophylaxiPatients")) {
			listPatientHistory = service.getAllProphylaxisPatient(programId, startDate, endDate, gender, minAge, maxAge);
		} else if (programIdKey.equals("patientsInSecondLine")) {
			listPatientHistory = service.getAllPatientsInSecondLine(programId, startDate, endDate, gender, minAge, maxAge);
			
		} else if (programIdKey.equals("patientsEnrolledInAProgram")) {
			listPatientHistory = service.getAllPatientsEnrolledInAProgram(programId, startDate, endDate, gender, minAge,
			    maxAge);
			
		} else if (programIdKey.equals("patientsOnARVWithoutVisitingPharmacyForXDays")) {
			listPatientHistory = service.getAllARVPatientsWithoutVisitingPharmacyForXDays(programId, startDate, endDate,
			    gender, minAge, maxAge, numberOfMonths);
			
		} else if (programIdKey.equals("patientsWithoutCD4CountsForXDays")) {
			listPatientHistory = service.getAllPatientsWithoutCD4CountsForXDays(programId, startDate, endDate, gender,
			    minAge, maxAge, numberOfMonths);
			
		}
		
		else if (programIdKey.equals("newPatientsOnARVs")) {
			listPatientHistory = service.getAllPatientsNewOnARVsBetweenDate(programId, startDate, endDate, gender, minAge,
			    maxAge);
			
		}
		
		else if (programIdKey.equals("newPatientsOnProphylaxis")) {
			listPatientHistory = service.getAllPatientsNewOnProphylaxisBetweenDate(programId, startDate, endDate, gender,
			    minAge, maxAge);
			
		}
		
		else if (programIdKey.equals("patientsEligibleOnARVsButNotYetStarted")) {
			listPatientHistory = service.getAllPatientsEligibleForARVsButNotYetStarted(programId, startDate, endDate,
			    gender, minAge, maxAge);
		}
		
		else if (programIdKey.equals("patientsOnFirstLine")) {
			listPatientHistory = service.getAllPatientsInFirstLine(programId, startDate, endDate, gender, minAge, maxAge);
			
		}
		
		else if (programIdKey.equals("cumulativePatientsOnARVs")) {
			listPatientHistory = service.getAllCumulativePatientsOnARVs(programId, startDate, endDate, gender, minAge,
			    maxAge);
			
		} else if (programIdKey.equals("cumulativePatientsOnProphylaxis")) {
			listPatientHistory = service.getAllCumulativePatientsOnProphylaxis(programId, startDate, endDate, gender,
			    minAge, maxAge);
			
		} else if (programIdKey.equals("patientsCurrentlyActive")) {
			listPatientHistory = service.getAllPatientsActive(programId, startDate, endDate, gender, minAge, maxAge);
			
		}
		
		else if (programIdKey.equals("lostOnFollowUpPatients")) {
			listPatientHistory = service.getAllPatientsWithNoEncountersBetweenTwoDates(programId, startDate, endDate,
			    gender, minAge, maxAge);
			
		}
		
		else if (programIdKey.equals("lostOnFollowUpPatientsOnARVsRegimen")) {
			listPatientHistory = service.getAllARVPatientsLostOnFollowUp(programId, startDate, endDate, gender, minAge,
			    maxAge);
		}
		
		else if (programIdKey.equals("patientsNewOnSecondLineRegimen")) {
			listPatientHistory = service.getAllPatientsNewOnSecondLineRegimenBetweenDate(programId, startDate, endDate,
			    gender, minAge, maxAge);
		}
		
		else if (programIdKey.equals("patientsTwelveMonthsOnTreatmentAfterInitialisationBetweenReportingPeriod")) {
			listPatientHistory = service.getAllPatientsOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(
			    programId, startDate, endDate, gender, minAge, maxAge);
		} else if (programIdKey
		        .equals("cumulativesPatientsTwelveMonthsOnTreatmentAfterInitialisationBetweenReportingPeriod")) {
			listPatientHistory = service.getAllPatientsEvenLostOnFollowUPOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(programId, startDate, endDate, gender, minAge, maxAge);
		}
		// else if (programIdKey.equals("PositivePatientsInPMTCT")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(programId,
		//			    startDate, endDate, gender, minAge, maxAge, numberOfMonths);
		//		} else if (programIdKey.equals("NegativePatientsInPMTCT")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(programId, startDate,
		//			    endDate, gender, minAge, maxAge, numberOfMonths);
		//		} else if (programIdKey.equals("CPNPatientsBetweenReportingPeriod")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(programId, startDate, endDate, gender, minAge, maxAge, numberOfMonths);
		//		} else if (programIdKey.equals("CouplesDiscordants")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoseCoupleIsDiscordant(programId, startDate, endDate, gender,
		//			    minAge, maxAge, numberOfMonths);
		//		} else if (programIdKey.equals("PatientsWhoGaveBirthBetweenTheReportingPeriod")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(programId, startDate,
		//			    endDate, gender, minAge, maxAge, numberOfMonths);
		//		} else if (programIdKey.equals("PatientsExpectedInPMTCTBetweenTheReportingPeriod")) {
		//			listPatientHistory = service.getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(programId,
		//			    startDate, endDate, gender, minAge, maxAge, numberOfMonths);
		//		}
		
		return listPatientHistory;
		
	}
	
	/**
	 * 	 
	 *
	 */
	
	public Map<String, List<Object[]>> getAllCriteria(int programId, Date startDate, Date endDate, String gender,
	                                                  Date minAge, Date maxAge, int numberOfMonths) {
		Map<String, List<Object[]>> allInOne = new HashMap<String, List<Object[]>>();
		
		ProgramCriteraList pg = new ProgramCriteraList();
		String[] criteriaKeys = pg.getAllCriteriaBasedOnProgram(programId);
		System.out.println("=======All program overview:======");
		
		for (String key : criteriaKeys) {
			if (key.equals("AllCriteria")) {
				continue;
			}
			allInOne.put(key,
			    checkTypeController(key, programId, startDate, endDate, gender, minAge, maxAge, numberOfMonths));
			System.out.println("All criteria" + key);
		}
		
		return allInOne;
		
	}
	
	/**
	 * get number of patient for each year Map each year with corresponding number of patient
	 */
	
	@SuppressWarnings("deprecation")
	public static Map<Integer, Integer> getAllPatientSizeByYear(String programIdKey, int programId, Date startDate,
	                                                            Date endDate, String gender, Date minAge, Date maxAge,
	                                                            int numberOfMonths) {
		//Map that holds the year(as key) and number of patients (as value)
		TreeMap<Integer, Integer> numberOfPatientByYear = new TreeMap<Integer, Integer>();
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		int startYear = startDate.getYear() + 1900;
		int endYear = endDate.getYear() + 1900;
		
		int startMonths = startDate.getMonth() + 1;
		int endMonth = endDate.getMonth() + 1;
		int startDay = startDate.getDay();
		int endDay = endDate.getDay();
		
		// get start  year and  increment it        
		// Now, for each year  there is  a specified number of patients
		
		if (startYear != endYear) {
			for (int year = startYear; year <= endYear; year = year + 1) {
				
				try {
					//define the start date and and date of each year
					//populate the map
					if (year == startYear) {
						
						Date startDat = startDate;
						Date endDat = df.parse("31/12/" + year);
						numberOfPatientByYear.put(
						    year,
						    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
						        numberOfMonths).size());
						
						;
						continue;
						
					}
					if (year == endYear) {
						Date startDat = df.parse("01/01/" + year);
						Date endDat = endDate;
						
						numberOfPatientByYear.put(
						    year,
						    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
						        numberOfMonths).size());
						
						continue;
						
					}
					Date startDat = df.parse("01/01/" + year);
					Date endDat = df.parse("31/12/" + year);
					
					numberOfPatientByYear.put(
					    year,
					    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
					        numberOfMonths).size());
					continue;
					
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		//starting month is the month corresponding to the selected  starting date
		
		if (startYear == endYear) {
			
			for (int months = startMonths; months <= endMonth; months = months + 1) {
				
				try {
					//define the start date and and date of each year
					//populate the map
					
					if (months == startMonths) {
						
						Date startDat = startDate;
						Date endDat = UsageStatsUtils.getTheLastDayOfThemonth(startYear, months - 1);
						
						numberOfPatientByYear.put(
						    months,
						    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
						        numberOfMonths).size());
						continue;
						
					}
					if (months == endMonth) {
						
						Date startDat = df.parse("01/" + months + "/" + startYear);
						Date endDat = endDate;
						
						numberOfPatientByYear.put(
						    months,
						    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
						        numberOfMonths).size());
						continue;
						
					}
					
					Date startDat = df.parse("01/" + months + "/" + startYear);
					Date endDat = UsageStatsUtils.getTheLastDayOfThemonth(startYear, months - 1);
					
					numberOfPatientByYear.put(
					    months,
					    checkTypeController(programIdKey, programId, startDat, endDat, gender, minAge, maxAge,
					        numberOfMonths).size());
					
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		//System.out.println(">>>>>>when checked>>>>>>number of patient by year"+numberOfPatientByYear.size());
		return numberOfPatientByYear;
		
	}
	
}
