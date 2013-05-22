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
package org.openmrs.module.programOver.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.module.programOver.db.ProgramOverviewDAO;
import org.openmrs.module.programOver.service.ProgramOverviewService;

public class ProgramOverviewServiceImpl implements ProgramOverviewService {
	
	private ProgramOverviewDAO programOverviewDAO;
	
	/**
	 * @return the programOverviewDAO
	 */
	public ProgramOverviewDAO getProgramOverviewDAO() {
		return programOverviewDAO;
	}
	
	/**
	 * @param programOverviewDAO the programOverviewDAO to set
	 */
	public void setProgramOverviewDAO(ProgramOverviewDAO programOverviewDAO) {
		this.programOverviewDAO = programOverviewDAO;
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getPatientWhoDied(java.lang.String,
	 *      java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getPatientWhoDied(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getPatientWhoDied(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	public List<Object[]> getAllTransferedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                              Date maxAge) {
		
		return programOverviewDAO.getAllTransferedPatient(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	public List<Object[]> getAllConsultedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                             Date maxAge) {
		
		return programOverviewDAO.getAllConsultedPatient(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	public List<Object[]> getAllPatientPharmacyVisit(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge) {
		return programOverviewDAO.getAllPatientPharmacyVisit(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	public List<Object[]> getAllARVPatients(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge) {
		return programOverviewDAO.getAllARVPatients(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllProphylaxisPatient(int,
	 *      java.util.Date, java.util.Date)
	 */
	
	public List<Object[]> getAllProphylaxisPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                               Date maxAge) {
		// TODO Auto-generated method stub   	
		
		return programOverviewDAO.getAllProphylaxisPatient(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInSecondLine(int,
	 *      java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsInSecondLine(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInSecondLine(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getPatientsAttributes(java.lang.String,
	 *      java.util.Date, java.util.Date)
	 */
	public String getPatientsAttributes(String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getPatientsAttributes(gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsEnrolledInAProgram(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsEnrolledInAProgram(int programId, Date startDate, Date endDate, String gender,
	                                                       Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsEnrolledInAProgram(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllARVPatientsWithoutVisitingPharmacyForXDays(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllARVPatientsWithoutVisitingPharmacyForXDays(int programId, Date startDate, Date endDate,
	                                                                       String gender, Date minAge, Date maxAge,
	                                                                       int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllARVPatientsWithoutVisitingPharmacyForXDays(programId, startDate, endDate, gender,
		    minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsWithoutCD4CountsForXDays(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsWithoutCD4CountsForXDays(int programId, Date startDate, Date endDate, String gender,
	                                                             Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsWithoutCD4CountsForXDays(programId, startDate, endDate, gender, minAge,
		    maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsActive(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsActive(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsActive(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsNewOnARVsBetweenDate(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsNewOnARVsBetweenDate(int programId, Date startDate, Date endDate, String gender,
	                                                         Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsNewOnARVsBetweenDate(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsNewOnProphylaxisPatientsBetweenDate(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsNewOnProphylaxisBetweenDate(int programId, Date startDate, Date endDate,
	                                                                String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsNewOnProphylaxisBetweenDate(programId, startDate, endDate, gender, minAge,
		    maxAge);
	}
	
	public List<Object[]> getAllPatientsNewOnSecondLineRegimensBetweenDate(int programId, Date startDate, Date endDate,
	                                                                       String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsNewOnSecondLineRegimenBetweenDate(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsEligibleForARVsButNotYetStarted(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsEligibleForARVsButNotYetStarted(programId, startDate, endDate, gender,
		    minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInFirstLine(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsInFirstLine(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                                Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInFirstLine(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllCumulativePatientsOnARVs(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllCumulativePatientsOnARVs(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllCumulativePatientsOnARVs(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllCumulativePatientsOnProphylaxis(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllCumulativePatientsOnProphylaxis(int programId, Date startDate, Date endDate, String gender,
	                                                            Date minAge, Date maxAge) {
		// TODO Auto-generated method stubs
		return programOverviewDAO.getAllCumulativePatientsOnProphylaxis(programId, startDate, endDate, gender, minAge,
		    maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsWithNoEncountersBetweenTwoDates(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<Object[]> getAllPatientsWithNoEncountersBetweenTwoDates(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsWithNoEncountersBetweenTwoDates(programId, startDate, endDate, gender,
		    minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsWithNoEncountersInXMonths(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsWithNoEncountersInXMonths(int programId, Date startDate, Date endDate,
	                                                              String gender, Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsWithNoEncountersInXMonths(programId, startDate, endDate, gender, minAge,
		    maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllProphylaxisPatient(int,
	 *      java.util.Date, java.util.Date)
	 */
	
	public SortedMap<Date, Double> getSubMap(SortedMap<Date, Double> map, Date dateToSearch) {
		return programOverviewDAO.getSubMap(map, dateToSearch);
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientObs(org.openmrs.Patient,
	 *      org.openmrs.Concept)
	 */
	@Override
	public String getAllPatientObs(Patient p, Concept c) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientObs(p, c);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientObsList(org.openmrs.Patient,
	 *      org.openmrs.Concept)
	 */
	@Override
	public List<String> getAllPatientObsList(Patient p, Concept c) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientObsList(p, c);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getWhenPatientStarted(org.openmrs.Patient)
	 */
	@Override
	public Date getWhenPatientStarted(Patient patient) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getWhenPatientStarted(patient);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllARVPatientsLostOnFollowUp(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Object[]> getAllARVPatientsLostOnFollowUp(int programId, Date startDate, Date endDate, String gender,
	                                                      Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllARVPatientsLostOnFollowUp(programId, startDate, endDate, gender, minAge, maxAge);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsCurrentlyInPMTCT(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsCurrentlyInPMTCT(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsCurrentlyInPMTCT(programId, startDate, endDate, gender, minAge, maxAge,
		    numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTBetweenTheReportingPeriod(int programId, Date startDate, Date endDate,
	                                                                     String gender, Date minAge, Date maxAge,
	                                                                     int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTBetweenTheReportingPeriod(programId, startDate, endDate, gender,
		    minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(int programId,
	                                                                                              Date startDate,
	                                                                                              Date endDate,
	                                                                                              String gender,
	                                                                                              Date minAge, Date maxAge,
	                                                                                              int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(programId,
		    startDate, endDate, gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(programId, startDate, endDate,
		    gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                     Date endDate, String gender,
	                                                                                     Date minAge, Date maxAge,
	                                                                                     int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(programId, startDate,
		    endDate, gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(programId, startDate, endDate,
		    gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(programId, startDate, endDate,
		    gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsInPMTCTWhoseCoupleIsDiscordant(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsInPMTCTWhoseCoupleIsDiscordant(int programId, Date startDate, Date endDate,
	                                                                   String gender, Date minAge, Date maxAge,
	                                                                   int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllPatientsInPMTCTWhoseCoupleIsDiscordant(programId, startDate, endDate, gender,
		    minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsPreART(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsPreART(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		return programOverviewDAO
		        .getAllPatientsPreART(programId, startDate, endDate, gender, minAge, maxAge, numberOfMonths);
	}
	
	/**
	 * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllChildrenPatientsEligibleForARVsButNotYetStarted(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Object[]> getAllChildrenPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                            String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		return programOverviewDAO.getAllChildrenPatientsEligibleForARVsButNotYetStarted(programId, startDate, endDate,
		    gender, minAge, maxAge);
	}

	/**
     * @see org.openmrs.module.programOver.service.ProgramOverviewService#getAllPatientsNewOnSecondLineRegimenBetweenDate(int, java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public List<Object[]> getAllPatientsNewOnSecondLineRegimenBetweenDate(int programId, Date startDate, Date endDate,
                                                                          String gender, Date minAge, Date maxAge) {
	    // TODO Auto-generated method stub
	    return null;
    }
	
}
