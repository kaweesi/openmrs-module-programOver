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
package org.openmrs.module.programOver.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;

/**
 *
 */
public interface ProgramOverviewService {
	
	public List<Object[]> getPatientWhoDied(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge);
	
	public List<Object[]> getAllTransferedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                              Date maxAge);
	
	public List<Object[]> getAllPatientPharmacyVisit(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge);
	
	public List<Object[]> getAllConsultedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                             Date maxAge);
	
	public List<Object[]> getAllARVPatients(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge);
	
	public List<Object[]> getAllProphylaxisPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                               Date maxAge);
	
	public List<Object[]> getAllPatientsInSecondLine(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsEnrolledInAProgram(int programId, Date startDate, Date endDate, String gender,
	                                                       Date minAge, Date maxAge);
	
	public List<Object[]> getAllARVPatientsWithoutVisitingPharmacyForXDays(int programId, Date startDate, Date endDate,
	                                                                       String gender, Date minAge, Date maxAge,
	                                                                       int numberOfMonths);
	
	public List<Object[]> getAllPatientsWithoutCD4CountsForXDays(int programId, Date startDate, Date endDate, String gender,
	                                                             Date minAge, Date maxAge, int numberOfMonths);
	
	public List<Object[]> getAllPatientsActive(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge);
	
	public List<Object[]> getAllPatientsNewOnARVsBetweenDate(int programId, Date startDate, Date endDate, String gender,
	                                                         Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsNewOnProphylaxisBetweenDate(int programId, Date startDate, Date endDate,
	                                                                String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsNewOnSecondLineRegimenBetweenDate(int programId, Date startDate, Date endDate,
	                                                                      String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllChildrenPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                            String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsInFirstLine(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                                Date maxAge);
	
	public List<Object[]> getAllCumulativePatientsOnARVs(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge);
	
	public List<Object[]> getAllCumulativePatientsOnProphylaxis(int programId, Date startDate, Date endDate, String gender,
	                                                            Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsWithNoEncountersBetweenTwoDates(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsWithNoEncountersInXMonths(int programId, Date startDate, Date endDate,
	                                                              String gender, Date minAge, Date maxAge, int numberOfMonths);
	
	public String getPatientsAttributes(String gender, Date minAge, Date maxAge);
	
	public SortedMap<Date, Double> getSubMap(SortedMap<Date, Double> map, Date dateToSearch);
	
	public List<String> getAllPatientObsList(Patient p, Concept c);
	
	public String getAllPatientObs(Patient p, Concept c);
	
	public Date getWhenPatientStarted(Patient patient);
	
	public List<Object[]> getAllARVPatientsLostOnFollowUp(int programId, Date startDate, Date endDate, String gender,
	                                                      Date minAge, Date maxAge);
	
	public List<Object[]> getAllPatientsPreART(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge, int numberOfMonths);
	
	public List<Object[]> getAllPatientsCurrentlyInPMTCT(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge, int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTBetweenTheReportingPeriod(int programId, Date startDate, Date endDate,
	                                                                     String gender, Date minAge, Date maxAge,
	                                                                     int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                     Date endDate, String gender,
	                                                                                     Date minAge, Date maxAge,
	                                                                                     int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoseCoupleIsDiscordant(int programId, Date startDate, Date endDate,
	                                                                   String gender, Date minAge, Date maxAge,
	                                                                   int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths);
	
	public List<Object[]> getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(int programId,
	                                                                                              Date startDate,
	                                                                                              Date endDate,
	                                                                                              String gender,
	                                                                                              Date minAge, Date maxAge,
	                                                                                              int numberOfMonths);
	
	public List<Object[]>getAllPatientsOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int programId,Date startDate,Date endDate, String gender, Date minAge, Date maxAge);
	
	
	public List<Object[]>getAllPatientsEvenLostOnFollowUPOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int programId,Date startDate,Date endDate, String gender, Date minAge, Date maxAge);
	
	public List<Object[]> getAllNewPregnantWomenOnARVs(int programId, Date startDate, Date endDate, String gender, Date minAge, Date maxAge);
	
}
