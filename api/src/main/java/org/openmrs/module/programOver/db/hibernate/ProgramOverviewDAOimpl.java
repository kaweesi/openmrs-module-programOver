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
package org.openmrs.module.programOver.db.hibernate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.programOver.GlobalProperties;
import org.openmrs.module.programOver.advice.UsageStatsUtils;
import org.openmrs.module.programOver.db.ProgramOverviewDAO;
import org.openmrs.module.programOver.utils.QueryUtility;
import org.openmrs.module.programOver.utils.RegimenUtils;
import org.openmrs.module.regimenhistory.Regimen;
import org.openmrs.module.regimenhistory.RegimenComponent;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 *
 */
public class ProgramOverviewDAOimpl implements ProgramOverviewDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getPatientWhoDied(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge) {
		PatientService patientService = Context.getPatientService();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("unused")
		String datef = null;
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String deathDate = new String("Death Date");
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId()) + " and ob.value_coded = "
			        + Integer.parseInt(GlobalProperties.gpGetExitFromCareDiedConceptId())
			        + " and (cast(ob.obs_datetime as DATE)) >= " + "'" + df.format(startDate) + "'"
			        + " and (cast(ob.obs_datetime as DATE)) <= " + "'" + df.format(endDate) + "'"
			        + " and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			        + programId);
			
			List<Integer> patientIds = query.list();
			log.info(">>>>>>>>>>>>>>minage date >>>>>>>>>" + minAge);
			for (Integer patientId : patientIds) {
				
				SQLQuery queryDate = session
				        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId())
				                + " and value_coded= "
				                + Integer.parseInt(GlobalProperties.gpGetExitFromCareDiedConceptId())
				                + " and (select cast(max(obs_datetime)as DATE)) is not null and (select cast(max(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id = " + patientId);
				List<Date> dateOfDeath = queryDate.list();
				
				if ((dateOfDeath.get(0).getTime() >= startDate.getTime())
				        && (dateOfDeath.get(0).getTime() <= endDate.getTime()))
				
				{
					
					patientSatatus = new Object[] { patientService.getPatient(patientId).getPatientIdentifier(),
					        dateOfDeath.get(0), deathDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllTransferedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                              Date maxAge) {
		PatientService patientService = Context.getPatientService();
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		@SuppressWarnings("unused")
		String datef = null;
		Session session = getSessionFactory().getCurrentSession();
		String transferredOutdate = new String("Transfer Date");
		
		try {
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + "and ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId()) + " and ob.value_coded = "
			        + Integer.parseInt(GlobalProperties.gpGetExitFromTransferredOutConceptId())
			        + " and (cast(ob.obs_datetime as DATE)) >= " + "'" + df.format(startDate) + "'"
			        + " and (cast(ob.obs_datetime as DATE)) <= " + "'" + df.format(endDate) + "'"
			        + " and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			        + programId);
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId())
				                + " and value_coded = "
				                + Integer.parseInt(GlobalProperties.gpGetExitFromTransferredOutConceptId())
				                + " and (select cast(max(obs_datetime)as DATE)) is not null and (select cast(max(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id=" + patientId);
				
				List<Date> maxObsDateTime = queryDate1.list();
				
				if ((maxObsDateTime.get(0).getTime() >= startDate.getTime())
				        && (maxObsDateTime.get(0).getTime() <= endDate.getTime()))
				
				{
					
					patientSatatus = new Object[] { patientService.getPatient(patientId).getPatientIdentifier(),
					        maxObsDateTime.get(0), transferredOutdate };
					listPatientHistory.add(patientSatatus);
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientPharmacyVisit(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge) {
		
		PatientService patientService = Context.getPatientService();
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastPharmacyVisitDate = new String("Last Pharmacy Visit Date");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			Session session = getSessionFactory().getCurrentSession();
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId()) + " and ob.value_coded =  "
			        + Integer.parseInt(GlobalProperties.gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId())
			        + " and (cast(ob.obs_datetime as DATE)) >= " + "'" + df.format(startDate) + "'"
			        + " and (cast(ob.obs_datetime as DATE)) <= " + "'" + df.format(endDate) + "'"
			        + " and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			        + programId);
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery queryDate3 = session
				        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs ob where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId())
				                + " and value_coded = "
				                + Integer.parseInt(GlobalProperties.gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId())
				                + " and (select cast(max(obs_datetime)as DATE))>= '"
				                + df.format(startDate)
				                + "' and (select cast(max(obs_datetime)as DATE))<= '"
				                + df.format(endDate)
				                + "' and (select cast(max(obs_datetime)as DATE)) is not null and ob.voided = 0 and ob.person_id = "
				                + patientId);
				
				List<Date> lastPharmacyDates = queryDate3.list();
				
				if ((lastPharmacyDates.get(0).getTime() >= startDate.getTime())
				        && (lastPharmacyDates.get(0).getTime() <= endDate.getTime()))
				
				{
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
					                + "(select(cast(max(encounter_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
					        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
					        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
					        + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					patientSatatus = new Object[] { patientService.getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        lastPharmacyDates.get(0), lastPharmacyVisitDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listPatientHistory;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllConsultedPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                             Date maxAge) {
		PatientService patientService = Context.getPatientService();
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastConsultation = new String("Last Consultation Date");
		List<Regimen> regimens = new ArrayList<Regimen>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId()) + " and ob.value_coded = "
			        + Integer.parseInt(GlobalProperties.gpGetOutPatientConsultationAsAnswerToReasonForVisitConceptId())
			        + " and (cast(ob.obs_datetime as DATE)) >= " + "'" + df.format(startDate) + "'"
			        + " and (cast(ob.obs_datetime as DATE)) <= " + "'" + df.format(endDate) + "'"
			        + " and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			        + programId);
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session.createSQLQuery("select distinct o.person_id from obs o where o.concept_id = "
				        + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId())
				        + " and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate)
				        + "' and o.voided = 0 and o.person_id=" + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate3 = session
					        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
					                + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId())
					                + " and value_coded = "
					                + Integer.parseInt(GlobalProperties
					                        .gpGetOutPatientConsultationAsAnswerToReasonForVisitConceptId())
					                + " and (select cast(max(obs_datetime)as DATE)) is not null and obs_datetime>= '"
					                + df.format(startDate) + "' and obs_datetime <= '" + df.format(endDate)
					                + "' and voided = 0 and person_id = " + patientId);
					
					List<Date> consultationDate = queryDate3.list();
					
					if ((consultationDate.get(0).getTime() >= startDate.getTime())
					        && (consultationDate.get(0).getTime() <= endDate.getTime())) {
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
							
							if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
							        .get(0).getTime()) <= endDate.getTime())
							        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
							                .get(0).getTime()) <= endDate.getTime())) {
								//							
								//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//							        .getRegimenList().size() != 0)
								//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//								        .getRegimenList();
								//							
								//							for (Regimen r : regimens) {
								//								components = r.getComponents();
								//							}
								
								patientSatatus = new Object[] {
								        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay, consultationDate.get(0), lastConsultation };
								listPatientHistory.add(patientSatatus);
								
							}
						}
						
						else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
							
							if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
							        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
								
								//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//							        .getRegimenList().size() != 0)
								//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//								        .getRegimenList();
								//							
								//							for (Regimen r : regimens) {
								//								components = r.getComponents();
								//							}
								
								patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay, consultationDate.get(0), lastConsultation };
								listPatientHistory.add(patientSatatus);
								
							}
						} else if ((maxReturnVisitDay.get(0) != null)
						        && (maxReturnVisitDay.get(0).getTime()) > endDate.getTime())
						
						{
							
							//						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							//						        .getRegimenList().size() != 0)
							//							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							//							        .getRegimenList();
							//						
							//						for (Regimen r : regimens) {
							//							components = r.getComponents();
							//						}
							
							patientSatatus = new Object[] { patientService.getPatient(patientId).getPatientIdentifier(),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, consultationDate.get(0), lastConsultation };
							listPatientHistory.add(patientSatatus);
							
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @throws java.text.ParseException
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllARVPatients(int,
	 *      java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllARVPatients(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                        Date maxAge) {
		
		// TODO Auto-generated method stub	
		PatientService patientService = Context.getPatientService();
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		        // + "inner join drug_order do on ord.order_id = do.order_id "
		        //+ "inner join drug d on do.drug_inventory_id = d.drug_id "
		        + "where ((pg.date_completed is null) or(cast(pg.date_completed as DATE)> ' " + df.format(endDate) + " ')) "
		        + " and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfARVsDrugs()
		        + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
		        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= "
		        + programId + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
		
		//log.info(" Patients on ART>>>>>>>>>>" + query1.toString());
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			Person person = Context.getPersonService().getPerson(patientId);
			Date birthDate = person.getBirthdate();
			int age = UsageStatsUtils.calculateAgeFromBirthDateToAnyDate(birthDate, endDate);
			
			SQLQuery queryDate1 = session.createSQLQuery(QueryUtility.createPatientMaxEncounterQuery(patientId, endDate));
			
			List<Date> maxEnocunterDateTime = queryDate1.list();
			
			SQLQuery queryDate2 = session.createSQLQuery(QueryUtility.CreatePatientMaxReturnVisitDay(patientId, endDate));
			
			/*("select cast(max(value_datetime) as DATE ) "
			        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
			        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
			        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
			        + patientId);*/
			
			List<Date> maxReturnVisitDay = queryDate2.list();
			
			patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
			        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
			listPatientHistory.add(patientSatatus);
			
		}
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllProphylaxisPatient(int,
	 *      java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllProphylaxisPatient(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                               Date maxAge) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("unused")
		String datef = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		//try {
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		        + "where ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate) + "'))"
		        + "and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfProphylaxisDrugs()
		        + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 " + " and pg.date_enrolled <= '"
		        + df.format(endDate) + "' and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
		        + "' and pg.program_id= " + programId);
		//log.info(">>>>>>>>>>>>>>>>>>>>first line"+query1.toString());
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			
			SQLQuery query2 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id " 
			        + "where ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfARVsDrugs() + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
			        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "'"
			        + " and pg.patient_id=" + patientId);
			
			List<Integer> patientIds2 = query2.list();
			
			if (patientIds2.size() == 0) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '"
				                + df.format(endDate)
				                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
				                + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
				        + patientId);
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
				        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
				listPatientHistory.add(patientSatatus);
				
			}
			
		}
		/*}
		
		catch (Exception e) {
			e.printStackTrace();
		}*/
		
		return listPatientHistory;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsInSecondLine(int programId, Date startDate, Date endDate, String gender,
	                                                 Date minAge, Date maxAge) {
		
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        // + "inner join drug_order do on ord.order_id = do.order_id "
			        // + "inner join drug d on do.drug_inventory_id = d.drug_id "
			        + "where ((pg.date_completed is null) or(cast(pg.date_completed as DATE)> ' " + df.format(endDate)
			        + " ')) "
			        
			        + " and pg.patient_id in (select person_id from person "
			        
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfSecondLineDrugs() + ") "
			        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
			        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
			        + "' and pg.program_id= " + programId);
			
			//log.info(">>>>>>>>>>>>patient in second line" + query1.toString());
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '"
				                + df.format(endDate)
				                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
				                + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
				        + patientId);
				
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				/*if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					        .get(0).getTime()) <= endDate.getTime())
					        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
					                .get(0).getTime()) <= endDate.getTime())) {
						
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
						        lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}

				else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
					        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
						
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
						        lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}

				else if ((maxReturnVisitDay.get(0) != null) && (maxReturnVisitDay.get(0).getTime()) > endDate.getTime())

				{
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
					        .getRegimenList().size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					*/
				patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
				        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
				listPatientHistory.add(patientSatatus);
				
			}
			
			//}
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
		
	}
	
	public String getRegimensAsString(Set<RegimenComponent> regimens) {
		StringBuffer sb = new StringBuffer();
		RegimenComponent[] components = regimens.toArray(new RegimenComponent[0]);
		
		for (int r = 0; r < components.length; r++) {
			
			RegimenComponent reg = components[r];
			RegimenComponent nextReg = (r < components.length - 1) ? components[r + 1] : null;
			
			if (nextReg == null || !reg.getStartDate().equals(nextReg.getStartDate()))
				sb.append(reg.toString() + "  ");
			else
				sb.append(reg.getDrug().getName() + "-");
		}
		return sb.toString();
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getPatientsAttributes(java.lang.String,
	 *      java.util.Date, java.util.Date)
	 */
	public String getPatientsAttributes(String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer attributeQPortion = new StringBuffer();
		//if (!gender.equals("")) {
		if (!gender.equals("any")) {
			attributeQPortion.append(" gender='" + gender + "' ");
		}
		//			else {
		//				attributeQPortion.append(" gender='m' OR gender='f'");
		//			}
		//	}
		/*===============================================================================================================*/
		
		if (maxAge != null || minAge != null) {
			if (minAge != null && maxAge == null) {
				attributeQPortion.append(((gender.equals("any")) ? " '" : " and '") + df.format(minAge)
				        + "' >= pe.birthdate ");
			} else if (maxAge != null && minAge == null) {
				attributeQPortion.append(((gender.equals("any")) ? " '" : " and '") + df.format(maxAge)
				        + "' <= pe.birthdate ");
			} else if (maxAge != null && minAge != null) {
				attributeQPortion.append(((gender.equals("any")) ? " " : " and ") + " pe.birthdate  between '"
				        + df.format(maxAge) + "' and '" + df.format(minAge) + "'");
			}
		}
		
		return (attributeQPortion.length() > 0) ? " where " + attributeQPortion.toString() : "";
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsEnrolledInAProgram(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsEnrolledInAProgram(int programId, Date startDate, Date endDate, String gender,
	                                                       Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where((pg.date_completed is null) or(cast(pg.date_completed as DATE) > ' " + df.format(endDate)
			        + " ')) " + "and pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and (cast(pg.date_enrolled as DATE)) >= "
			        + "'" + df.format(startDate) + "'" + " and (cast(pg.date_enrolled as DATE)) <= " + "'"
			        + df.format(endDate) + "'"
			        + " and pg.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = " + programId);
			log.info(">>>>>>>sql enrollement" + query.toString());
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				/*SQLQuery query2 = session.createSQLQuery("select distinct o.person_id from obs o where o.concept_id = "
				        + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId())
				        + " and (cast(o.obs_datetime as DATE)) <= " + "'" + df.format(endDate) + "'"
				        + " and o.voided = 0 and o.person_id=" + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {*/
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(min(date_enrolled) as DATE) from patient_program where (select cast(min(date_enrolled) as DATE)) is not null and patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					if (dateEnrolled.get(0) != null) {
						
						if ((dateEnrolled.get(0).getTime() >= startDate.getTime())
						        && (dateEnrolled.get(0).getTime() <= endDate.getTime()))
						
						{
							SQLQuery queryDate1 = session
							        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
							                + "(select(cast(max(encounter_datetime)as Date))) <= '"
							                + df.format(endDate)
							                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
							                + patientId);
							
							List<Date> maxEnocunterDateTime = queryDate1.list();
							
							SQLQuery queryDate2 = session
							        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
							                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
							                + df.format(endDate)
							                + "' and concept_id = "
							                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
							                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
							                + patientId);
							
							List<Date> maxReturnVisitDay = queryDate2.list();
							
							patientSatatus = new Object[] {
							        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, dateEnrolled.get(0), enrollementDate };
							listPatientHistory.add(patientSatatus);
							
						}
						
					}
				}
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllARVPatientsWithoutVisitingPharmacyForXDays(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllARVPatientsWithoutVisitingPharmacyForXDays(int programId, Date startDate, Date endDate,
	                                                                       String gender, Date minAge, Date maxAge,
	                                                                       int numberOfMonths) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastPharmacyVisitDate = new String("Last Pharmacy Visit Date");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		int withoutPharmacyVisitForXdays = numberOfMonths;
		
		int indicator = 0;
		
		Date dateWithoutVisitingPharmacyForXDays = UsageStatsUtils.addDaysToDate(endDate, -withoutPharmacyVisitForXdays);
		
		Session session = getSessionFactory().getCurrentSession();
		
		//		Date today = new Date();
		//		HashMap<Integer, Long> patientMap = new HashMap<Integer, Long>();
		
		try {
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pe.person_id = o.person_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        //+ "inner join drug_order do on ord.order_id = do.order_id "
			        /* + "inner join drug d on do.drug_inventory_id = d.drug_id "*/
			        + " where ((pg.date_completed is null) or (pg.date_completed > '" + endDate
			        + "')) and pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfARVsDrugs() + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
			        + "and o.voided = 0 and pa.voided = 0 " + " and o.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId()) + " and o.value_coded = "
			        + Integer.parseInt(GlobalProperties.gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId())
			        + " and pg.program_id= " + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate = session
				        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId()) + " and value_coded = "
				                + Integer.parseInt(GlobalProperties.gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId())
				                + " and (select cast(max(obs_datetime)as DATE)) is not null and voided = 0 and person_id = "
				                + patientId);
				Date lastPharmacyVisitdate = (Date) queryDate.list().get(0);
				
				//				if (lastPharmacyVisitdate != null && today != null) {
				//					long diffdays = UsageStatsUtils.calculateDiffDays(today, lastPharmacyVisitdate);
				//					patientMap.put(patientId, diffdays / 30);
				//					
				//				}
				//				
				//			}
				//		}
				//		
				//		for (Integer key : patientMap.keySet()) {
				//			
				//			try {
				//				
				//				if (patientMap.get(key) == withoutPharmacyVisitFordays) {
				
				if ((lastPharmacyVisitdate.getTime()) >= dateWithoutVisitingPharmacyForXDays.getTime()
				        && (lastPharmacyVisitdate.getTime()) <= endDate.getTime())
				
				{
					
					indicator++;
				}
				
				else if ((lastPharmacyVisitdate.getTime() >= endDate.getTime())) {
					indicator++;
				}
				
				else
				
				{
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
					                + patientId);
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where concept_id = "
					        + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
					        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
					        + patientId);
					
					SQLQuery queryDate3 = session
					        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
					                + Integer.parseInt(GlobalProperties.gpGetReasonForVisitConceptId())
					                + " and (select cast(max(obs_datetime)as DATE)) is not null and value_coded = "
					                + Integer.parseInt(GlobalProperties
					                        .gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId()) + " and person_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					List<Date> maxReturnVisitDay = queryDate2.list();
					//List<Date> lastPharmacyDates = queryDate3.list();
					
					//					if ((maxReturnVisitDay.get(0)) != null) {
					//						
					//						if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					//						        .get(0).getTime()) <= endDate.getTime())
					//						        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
					//						                .get(0).getTime()) <= endDate.getTime())) {
					//							
					//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key)).getRegimenList()
					//							        .size() != 0)
					//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key))
					//								        .getRegimenList();
					//							
					//							for (Regimen r : regimens) {
					//								components = r.getComponents();
					//							}
					//							
					//							patientSatatus = new Object[] { Context.getPatientService().getPatient(key),
					//							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay, lastReturnVisitDay,
					//							        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
					//							        lastPharmacyVisitDate };
					//							listPatientHistory.add(patientSatatus);
					//							
					//						}
					//					}
					//
					//					else if ((maxReturnVisitDay.get(0)) == null) {
					//						
					//						if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
					//						        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
					//							
					//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key)).getRegimenList()
					//							        .size() != 0)
					//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key))
					//								        .getRegimenList();
					//							
					//							for (Regimen r : regimens) {
					//								components = r.getComponents();
					//							}
					//							
					//							patientSatatus = new Object[] { Context.getPatientService().getPatient(key),
					//							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay, lastReturnVisitDay,
					//							        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
					//							        lastPharmacyVisitDate };
					//							listPatientHistory.add(patientSatatus);
					//							
					//						}
					//					} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
					//
					//					{
					
					//					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList().size() != 0)
					//						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
					//						        .getRegimenList();
					//					
					//					for (Regimen r : regimens) {
					//						components = r.getComponents();
					//					}
					
					patientSatatus = new Object[] {
					        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        lastPharmacyVisitdate, lastPharmacyVisitDate };
					listPatientHistory.add(patientSatatus);
					
				}
				
			}
			
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsWithoutCD4CountsForXDays(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsWithoutCD4CountsForXDays(int programId, Date startDate, Date endDate, String gender,
	                                                             Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastPharmacyVisitDate = new String("Last CD4 Count Test Date");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		//		Date today = new Date();
		//		HashMap<Integer, Long> patientMap = new HashMap<Integer, Long>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		int withoutCD4CountTestForXdays = numberOfMonths;
		
		int indicator = 0;
		
		Date dateWithoutVisitingPharmacyForXDays = UsageStatsUtils.addDaysToDate(endDate, -withoutCD4CountTestForXdays);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + " where ((pg.date_completed is null) or (pg.date_completed > '" + endDate
			        + "')) and pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId())
			        + " and (pg.voided = 0 and pe.voided = 0 and ob.voided = 0 and pa.voided = 0) and pg.program_id = "
			        + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate = session
				        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId())
				                + " and (select cast(max(obs_datetime)as DATE)) is not null and person_id = " + patientId);
				Date lastCD4CountTestDate = (Date) queryDate.list().get(0);
				
				//				if (lastPharmacyVisitdate != null && today != null) {
				//					long diffdays = UsageStatsUtils.calculateDiffDays(today, lastPharmacyVisitdate);
				//					patientMap.put(patientId, diffdays / 30);
				//					
				//				}
				//				
				//			}
				//		}
				//		
				//		for (Integer key : patientMap.keySet()) {
				//			
				//			try {
				//				
				//				if (patientMap.get(key) == withoutPharmacyVisitFordays) {
				
				if ((lastCD4CountTestDate.getTime()) >= dateWithoutVisitingPharmacyForXDays.getTime()
				        && (lastCD4CountTestDate.getTime()) <= endDate.getTime())
				
				{
					
					indicator++;
				}
				
				else if ((lastCD4CountTestDate.getTime() >= endDate.getTime()))
				
				{
					indicator++;
				}
				
				else
				
				{
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE)) is not null and patient_id = "
					                + patientId);
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select cast(max(value_datetime) as DATE )) is not null and concept_id = "
					        + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId()) + " and person_id = "
					        + patientId);
					
					SQLQuery queryDate3 = session
					        .createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where (select cast(max(obs_datetime)as DATE)) is not null and concept_id = "
					                + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId())
					                + " and person_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					List<Date> maxReturnVisitDay = queryDate2.list();
					//List<Date> lastPharmacyDates = queryDate3.list();
					
					//					if ((maxReturnVisitDay.get(0)) != null) {
					//						
					//						if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					//						        .get(0).getTime()) <= endDate.getTime())
					//						        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
					//						                .get(0).getTime()) <= endDate.getTime())) {
					//							
					//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key)).getRegimenList()
					//							        .size() != 0)
					//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key))
					//								        .getRegimenList();
					//							
					//							for (Regimen r : regimens) {
					//								components = r.getComponents();
					//							}
					//							
					//							patientSatatus = new Object[] { Context.getPatientService().getPatient(key),
					//							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay, lastReturnVisitDay,
					//							        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
					//							        lastPharmacyVisitDate };
					//							listPatientHistory.add(patientSatatus);
					//							
					//						}
					//					}
					//
					//					else if ((maxReturnVisitDay.get(0)) == null) {
					//						
					//						if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
					//						        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
					//							
					//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key)).getRegimenList()
					//							        .size() != 0)
					//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(key))
					//								        .getRegimenList();
					//							
					//							for (Regimen r : regimens) {
					//								components = r.getComponents();
					//							}
					//							
					//							patientSatatus = new Object[] { Context.getPatientService().getPatient(key),
					//							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay, lastReturnVisitDay,
					//							        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
					//							        lastPharmacyVisitDate };
					//							listPatientHistory.add(patientSatatus);
					//							
					//						}
					//					} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
					//
					//					{
					
					//					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList().size() != 0)
					//						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
					//						        .getRegimenList();
					//					
					//					for (Regimen r : regimens) {
					//						components = r.getComponents();
					//					}
					
					patientSatatus = new Object[] {
					        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        lastCD4CountTestDate, lastPharmacyVisitDate };
					listPatientHistory.add(patientSatatus);
					
				}
				
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsActive(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsActive(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Session session = getSessionFactory().getCurrentSession();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        // + "inner join obs o on pe.person_id = o.person_id "
			        // + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "where ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate) + "'))"
			        + " and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
			        + ") " + " and pg.voided = 0 and pe.voided = 0 " + " and pa.voided = 0 " + " and pg.date_enrolled <= '"
			        + df.format(endDate) + "' and pg.program_id= " + programId);
			log.info("active patients>>>>>>>" + query1.toString());
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '"
				                + df.format(endDate)
				                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
				                + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
				        + patientId);
				
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
				        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
				listPatientHistory.add(patientSatatus);
				
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsNewOnARVsBetweenDate(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsNewOnARVsBetweenDate(int programId, Date startDate, Date endDate, String gender,
	                                                         Date minAge, Date maxAge) {
		
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//try {
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		        //+ "where ((pg.date_completed is null) or(cast(pg.date_completed as DATE)> ' " + df.format(endDate)  + " ')) "
		        + " where pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfARVsDrugs() + ") "
		        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
		        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
		        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
		        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			
				
				SQLQuery queryMinStartDate = session
				        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
				                // + " inner join drug_order do on ord.order_id = do.order_id "
				                // + " inner join drug d on do.drug_inventory_id = d.drug_id "
				                + " where ord.concept_id IN ("
				                + GlobalProperties.gpGetListOfARVsDrugs()
				                + ") "
				                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
				                + patientId);
				
				List<Date> patientIdsMinStartDate = queryMinStartDate.list();
				
				if (patientIdsMinStartDate.get(0) != null) {
					
					if ((patientIdsMinStartDate.get(0).getTime() >= startDate.getTime())
					        && patientIdsMinStartDate.get(0).getTime() <= endDate.getTime()) {
											
						
						SQLQuery queryTransferInDate = session.createSQLQuery("select cast(min(obs_datetime)as DATE) from obs where concept_id = "
							
								+ Integer.parseInt(GlobalProperties.gpGetTransferredInConceptId()) + " and value_coded = "
						        + Integer.parseInt(GlobalProperties.gpGetyesAsAnswerToTransferredInConceptId())
						        + " and (select cast(min(obs_datetime)as DATE)) is not null and (select cast(min(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id=" + patientId);
						
						List<Date> patientIdsTransferInDate = queryTransferInDate.list();
						log.info(">>>>>>>>>>>>>>>"+queryTransferInDate.toString());
						
						if (patientIdsTransferInDate.get(0)!= null) {
							
							
							if (patientIdsMinStartDate.get(0).getTime() >= patientIdsTransferInDate.get(0).getTime()) {
							
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
					
				}
				
			}
					
			if ((patientIdsMinStartDate.get(0).getTime() >= startDate.getTime())
					        && patientIdsMinStartDate.get(0).getTime() <= endDate.getTime()) {
											
						
				SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where  ob.concept_id = "
			        + Integer.parseInt(GlobalProperties.gpGetTransferredInConceptId()) + " and ob.value_coded = "
			        + Integer.parseInt(GlobalProperties.gpGetNonAsAnswerToTransferredInConceptId())			      
			        + " and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.patient_id = " + patientId);
			
			List<Integer> patientIds = query.list();
						
						if (patientIds.size() !=0) {
							
							
							
							SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
					                + "(select(cast(max(encounter_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session
					        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
					                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and concept_id = "
					                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
					                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
					                + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					patientSatatus = new Object[] {
					        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
					listPatientHistory.add(patientSatatus);
					
							
							
							
							
				}
						}
						
			
		}
		
		}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsNewOnProphylaxisBetweenDate(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsNewOnProphylaxisBetweenDate(int programId, Date startDate, Date endDate,
	                                                                String gender, Date minAge, Date maxAge) {
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("unused")
		String datef = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        // + "inner join drug_order do on ord.order_id = do.order_id "
			        // + "inner join drug d on do.drug_inventory_id = d.drug_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfProphylaxisDrugs() + ") "
			        + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
			        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
			        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
			        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
				                + "inner join person pe on pg.patient_id = pe.person_id "
				                + "inner join patient pa on pg.patient_id = pa.patient_id "
				                + "inner join orders ord on pg.patient_id = ord.patient_id "
				                // + "inner join drug_order do on ord.order_id = do.order_id "
				                //+ "inner join drug d on do.drug_inventory_id = d.drug_id "
				                + "where pg.patient_id in (select person_id from person "
				                + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
				                + GlobalProperties.gpGetListOfARVsDrugs() + ") "
				                + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
				                + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
				                + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= "
				                + programId + " and pg.date_enrolled <= '" + df.format(endDate) + "' and pg.patient_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0)
				
				{
					
					SQLQuery queryMinStartDate = session
					        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
					                // + " inner join drug_order do on ord.order_id = do.order_id "
					                // + " inner join drug d on do.drug_inventory_id = d.drug_id "
					                + " where ord.concept_id IN ("
					                + GlobalProperties.gpGetListOfProphylaxisDrugs()
					                + ") "
					                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
					                + patientId);
					
					List<Date> patientIdsMinStartDate = queryMinStartDate.list();
					
					if ((patientIdsMinStartDate.get(0).getTime() >= startDate.getTime())
					        && (patientIdsMinStartDate.get(0).getTime() <= endDate.getTime())) {
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}
				
			}
			//		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsEligibleForARVsButNotYetStarted(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		double val = 0;
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastPharmacyVisitDate = new String("Last CD4 Count Test");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "inner join obs o on pg.patient_id = o.person_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge) + ") "
			                + " and ((pg.date_completed is null) or(cast(pg.date_completed as DATE)> ' "
			                + df.format(endDate) + " ')) " + " and pg.voided = 0 and pe.voided = 0 and o.voided = 0 "
			                + " and pa.voided = 0 and o.concept_id = "
			                + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId())
			                + " and (cast(o.obs_datetime as DATE)) >= '" + df.format(startDate)
			                + "' and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= "
			                + programId);
			List<Integer> patientIds1 = query1.list();
			
			List<Date> maxReturnVisitDay = new ArrayList<Date>();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2Date = session
				        .createSQLQuery("select cast(max(obs_datetime) as DATE) from obs where concept_id = "
				                + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId())
				                + " and (select cast(max(obs_datetime) as DATE)) >= '" + df.format(startDate)
				                + "' and (select cast(max(obs_datetime) as DATE)) <= '" + df.format(endDate)
				                + "' and (select cast(max(obs_datetime) as DATE)) is not null and voided=0 and person_id = "
				                + patientId);
				List<Date> maxObsDateTimeCD4Count = query2Date.list();
				
				if ((maxObsDateTimeCD4Count.get(0).getTime() >= startDate.getTime())
				        && (maxObsDateTimeCD4Count.get(0).getTime() <= endDate.getTime())) {
					SQLQuery query3 = session.createSQLQuery("select value_numeric from obs where concept_id = "
					        + Integer.parseInt(GlobalProperties.gpGetCD4CountConceptId()) + " and obs_datetime = '"
					        + maxObsDateTimeCD4Count.get(0)
					        + "' and value_numeric is not null and voided=0 and person_id = " + patientId);
					
					List<Double> maxValueNumericCD4Count = query3.list();
					
					//val = (maxValueNumericCD4Count.size() > 0) ? maxValueNumericCD4Count.get(0) : 400;
					
					if (maxValueNumericCD4Count.size() != 0)
					
					{
						
						if (maxValueNumericCD4Count.get(0) < 350.0) {
							
							SQLQuery query4 = session
							        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
							                + "inner join person pe on pg.patient_id = pe.person_id "
							                + "inner join patient pa on pg.patient_id = pa.patient_id "
							                + "inner join orders ord on pg.patient_id = ord.patient_id "
							                //+ "inner join drug_order do on ord.order_id = do.order_id "
							                // + "inner join drug d on do.drug_inventory_id = d.drug_id "
							                + "where pg.patient_id in (select person_id from person "
							                + getPatientsAttributes(gender, minAge, maxAge)
							                + ") "
							                + " and ord.concept_id IN ("
							                + GlobalProperties.gpGetListOfARVsDrugs()
							                + ") "
							                + " and (cast(ord.date_activated as DATE)) <= '"
							                + df.format(endDate)
							                + "' and pg.voided= 0 and pe.voided = 0 and pa.voided = 0 and ord.voided = 0 and pg.program_id= "
							                + programId + " and pg.patient_id =  " + patientId);
							
							List<Integer> patientIds4 = query4.list();
							
							if (patientIds4.size() == 0) {
								
								SQLQuery queryDate1 = session
								        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
								                + df.format(endDate)
								                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
								                + patientId);
								
								List<Date> maxEnocunterDateTime = queryDate1.list();
								
								SQLQuery queryDate2 = session
								        .createSQLQuery("select cast(max(value_datetime) as DATE )"
								                + "from obs where (select cast(max(value_datetime)as DATE))<= '"
								                + df.format(endDate)
								                + "' and concept_id = "
								                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
								                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
								                + patientId);
								
								maxReturnVisitDay = queryDate2.list();
								
								/*if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
									
									if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
									        .get(0).getTime()) <= endDate.getTime())
									        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate
									                .getTime() && (maxReturnVisitDay.get(0).getTime()) <= endDate
									                .getTime())) {
										
										//										if (RegimenUtils
										//										        .getRegimenHistory(Context.getPatientService().getPatient(patientId))
										//										        .getRegimenList().size() != 0)
										//											regimens = RegimenUtils.getRegimenHistory(
										//											    Context.getPatientService().getPatient(patientId)).getRegimenList();
										//										
										//										for (Regimen r : regimens) {
										//											components = r.getComponents();
										//										}
										
										patientSatatus = new Object[] {
										        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
										        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
										        lastReturnVisitDay, maxObsDateTimeCD4Count.get(0), lastPharmacyVisitDate };
										listPatientHistory.add(patientSatatus);
										
									}
								}
								
								else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
									
									if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
									        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
										
										//										if (RegimenUtils
										//										        .getRegimenHistory(Context.getPatientService().getPatient(patientId))
										//										        .getRegimenList().size() != 0)
										//											regimens = RegimenUtils.getRegimenHistory(
										//											    Context.getPatientService().getPatient(patientId)).getRegimenList();
										//										
										//										for (Regimen r : regimens) {
										//											components = r.getComponents();
										//										}
										
										patientSatatus = new Object[] {
										        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
										        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
										        lastReturnVisitDay, maxObsDateTimeCD4Count.get(0), lastPharmacyVisitDate };
										listPatientHistory.add(patientSatatus);
										
									}
								}
								
								else if ((maxReturnVisitDay.get(0) != null)
								        && (maxReturnVisitDay.get(0).getTime()) > endDate.getTime())
								
								{
									
									//									if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
									//									        .getRegimenList().size() != 0)
									//										regimens = RegimenUtils.getRegimenHistory(
									//										    Context.getPatientService().getPatient(patientId)).getRegimenList();
									//									
									//									for (Regimen r : regimens) {
									//										components = r.getComponents();
									//									}
								*/
								patientSatatus = new Object[] {
								        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay, maxObsDateTimeCD4Count.get(0), lastPharmacyVisitDate };
								listPatientHistory.add(patientSatatus);
								
							}
						}
					}
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInFirstLine(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsInFirstLine(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                                Date maxAge) {
		// TODO Auto-generated method stub
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		@SuppressWarnings("unused")
		String datef = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "where ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate) + "'))"
			        + "and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
			        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfFirstLineDrugs()
			        + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 " + " and pg.date_enrolled <= '"
			        + df.format(endDate) + "' and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '"
			        + df.format(endDate) + "' and pg.program_id= " + programId);
			log.info(">>>>>>>>>>>>>>>>>>>>first line" + query1.toString());
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
				        + "inner join person pe on pg.patient_id = pe.person_id "
				        + "inner join patient pa on pg.patient_id = pa.patient_id "
				        + "inner join orders ord on pg.patient_id = ord.patient_id " + "where ord.concept_id IN ("
				        + GlobalProperties.gpGetListOfSecondLineDrugs()
				        + ") and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
				        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "'"
				        + " and pg.patient_id=" + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
					                + "(select(cast(max(encounter_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
					        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
					        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
					        + patientId);
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					patientSatatus = new Object[] {
					        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
					listPatientHistory.add(patientSatatus);
					
				}
				
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllCumulativePatientsOnARVs(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllCumulativePatientsOnARVs(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        // + "where ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate)	+ "')) "
			        + " and  pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfARVsDrugs() + ") " + " " + "and (cast(ord.date_activated as DATE)) <= '"
			        + df.format(endDate) + "' and pg.program_id= " + programId);
			//log.info(">>>>>>>>>>>query cumulative>>>>"+query1.toString());
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '"
				                + df.format(endDate)
				                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
				                + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
				        + patientId);
				
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
				        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
				listPatientHistory.add(patientSatatus);
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllCumulativePatientsOnProphylaxis(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllCumulativePatientsOnProphylaxis(int programId, Date startDate, Date endDate, String gender,
	                                                            Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        //+ "where ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate)	+ "')) "
			        + " and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
			        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfProphylaxisDrugs() + ") "
			        + " and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' " + " and pg.date_enrolled <= '"
			        + df.format(endDate) + "' and pg.program_id= " + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
				        + "inner join person pe on pg.patient_id = pe.person_id "
				        + "inner join patient pa on pg.patient_id = pa.patient_id "
				        + "inner join orders ord on pg.patient_id = ord.patient_id "
				        //+ "inner join drug_order do on ord.order_id = do.order_id "
				        //  + "inner join drug d on do.drug_inventory_id = d.drug_id "
				        + "where pg.patient_id in (select person_id from person "
				        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
				        + GlobalProperties.gpGetListOfARVsDrugs() + ") " + " and pg.program_id= " + programId
				        + " and pg.voided = 0 and pe.voided = 0 and pa.voided = 0 and ord.voided = 0 and pg.patient_id="
				        + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
					                + "(select(cast(max(encounter_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
					        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
					        + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
					        + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
					        .size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					
					patientSatatus = new Object[] {
					        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
					listPatientHistory.add(patientSatatus);
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsWithNoEncountersBetweenTwoDates(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsWithNoEncountersBetweenTwoDates(int programId, Date startDate, Date endDate,
	                                                                    String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		ArrayList<Integer> patientsNotLostToFollowUp = new ArrayList<Integer>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") "
			        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 and o.voided = 0 "
			        + " and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
			        + "' and ((pg.date_completed is null) or (pg.date_completed > '" + endDate + "')) and pg.program_id= "
			        + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '" + df.format(endDate)
				                + "' and voided = 0 and patient_id = " + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and voided = 0 and person_id = " + patientId);
				
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					        .get(0).getTime()) <= endDate.getTime())
					        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
					                .get(0).getTime()) <= endDate.getTime())) {
						
						patientsNotLostToFollowUp.add(patientId);
					}
					
					else {
						
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}
				
				else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					        .get(0).getTime()) <= endDate.getTime())) {
						
						patientsNotLostToFollowUp.add(patientId);
						
					}
					
					else {
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listPatientHistory;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsOnARVsWithNoEncountersBetweenTwoDates(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllPatientsWithNoEncountersInXMonths(int programId, Date startDate, Date endDate,
	                                                              String gender, Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String datef = null;
		Session session = getSessionFactory().getCurrentSession();
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join encounter en on pg.patient_id = en.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id = 1482 "
			        + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 and o.voided = 0 "
			        + "and pa.voided = 0 and en.voided = 0 and o.concept_id <> 1811 and (cast(o.obs_datetime as Date)) >= '"
			        + df.format(startDate) + "' and (cast(o.obs_datetime as Date)) <= '" + df.format(endDate)
			        + "' and  pg.program_id= " + programId + " and pg.date_completed is null ");
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery query2Date = session
					        .createSQLQuery("select min(start_date) from orders where (select min(start_date)) is not null and patient_id = "
					                + patientId);
					List<Date> date = query2Date.list();
					
					SQLQuery query3 = session.createSQLQuery("select patient_id from orders where '"
					        + df.format(date.get(0)) + "' >= '" + df.format(startDate) + "' and '" + df.format(date.get(0))
					        + "' <= '" + df.format(endDate) + "' and patient_id = " + patientId);
					List<Integer> patientIds3 = query3.list();
					if (patientIds3.size() != 0) {
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
						                + df.format(startDate)
						                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select cast(max(value_datetime)as DATE))>= '"
						                + df.format(startDate)
						                + "' and (select cast(max(value_datetime)as DATE))<= '"
						                + df.format(endDate)
						                + "' and (select cast(max(value_datetime) as DATE )) is not null and concept_id = 5096 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						if (maxReturnVisitDay.get(0).getTime() < endDate.getTime()) {
							
							if (((maxEnocunterDateTime.get(0).getTime() > threeMonthsBeforeEndDate.getTime() && maxEnocunterDateTime
							        .get(0).getTime() < endDate.getTime()) || (maxReturnVisitDay.get(0).getTime() > threeMonthsBeforeEndDate
							        .getTime() && maxReturnVisitDay.get(0).getTime() < endDate.getTime()))) {
								
								if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList().size() != 0)
									regimens = RegimenUtils.getRegimenHistory(
									    Context.getPatientService().getPatient(patientId)).getRegimenList();
								
								for (Regimen r : regimens) {
									components = r.getComponents();
								}
								
								patientSatatus = new Object[] {
								        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay };
								listPatientHistory.add(patientSatatus);
								
							}
						}
						
						else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
							
							if (((maxEnocunterDateTime.get(0).getTime() > threeMonthsBeforeEndDate.getTime() && maxEnocunterDateTime
							        .get(0).getTime() < endDate.getTime()))) {
								
								if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList().size() != 0)
									regimens = RegimenUtils.getRegimenHistory(
									    Context.getPatientService().getPatient(patientId)).getRegimenList();
								
								for (Regimen r : regimens) {
									components = r.getComponents();
								}
								
								patientSatatus = new Object[] {
								        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay };
								listPatientHistory.add(patientSatatus);
								
							}
						} else if (maxReturnVisitDay.get(0).getTime() > endDate.getTime()) {
							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList().size() != 0)
								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList();
							
							for (Regimen r : regimens) {
								components = r.getComponents();
							}
							
							patientSatatus = new Object[] {
							        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay };
							listPatientHistory.add(patientSatatus);
							
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	@SuppressWarnings("unused")
	private List<Patient> listOfPatients(List<Integer> patientIds, Date startDate) throws ParseException,
	                                                                              java.text.ParseException {
		
		List<Patient> patients = new ArrayList<Patient>();
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date startingDate = startDate;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startingDate);
		
		//subtracting 3 months to the given date.
		cal.add(2, -2);
		
		String realDate = cal.get(cal.YEAR) + "/" + cal.get(cal.MONTH) + "/" + cal.get(cal.DATE);
		startingDate = sdf.parse(realDate);
		
		for (Integer patientId : patientIds) {
			
			if (!maxDateForNextVisit(patientId).equals("")) {
				Date maxDate = sdf.parse(maxDateForNextVisit(patientId));
				
				if (maxDate.compareTo(startingDate) < 0) {
					
					Patient patient = Context.getPatientService().getPatient(patientId);
					
					if (!patient.getPersonVoided()) {
						
						patients.add(patient);
					}
				}
			}
		}
		
		return patients;
	}
	
	private String maxDateForNextVisit(int patientId) throws ParseException {
		
		Date maxDate;
		String returnedDate = "";
		
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
		        .createSQLQuery("select cast(MAX(obs.value_datetime) as DATE) from obs where obs.concept_id = 5096 "
		                + " and obs.person_id = " + patientId);
		
		Date record = (Date) query.uniqueResult();
		
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		if (record != null) {
			
			maxDate = record;
			returnedDate = sdf.format(maxDate);
		}
		
		String returnValue = returnedDate;
		
		return returnValue;
		
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsCurrentlyInPMTCT(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsCurrentlyInPMTCT(int programId, Date startDate, Date endDate, String gender,
	                                                     Date minAge, Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + " and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + " and ob.concept_id <> 1811 and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			                + programId + " and pg.date_completed is null");
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
					                + df.format(endDate) + "' and patient_id = " + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE )"
					        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
					        + "' and concept_id = 5096 and person_id = " + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
					        .size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					
					patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTBetweenTheReportingPeriod(int programId, Date startDate, Date endDate,
	                                                                     String gender, Date minAge, Date maxAge,
	                                                                     int numberOfMonths) {
		
		// TODO Auto-generated method stub
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + " and (cast(pg.date_enrolled as DATE)) >= "
			                + "'"
			                + df.format(startDate)
			                + "'"
			                + " and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + " and ob.concept_id <> 1811 and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			                + programId + " and pg.date_completed is null");
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
					                + df.format(startDate)
					                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
					                + df.format(endDate) + "' and patient_id = " + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select cast(max(value_datetime)as DATE))>= '" + df.format(startDate)
					        + "' and (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
					        + "' and concept_id = 5096 and person_id = " + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
					        .size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					
					patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoAreSeroPositifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                     Date endDate, String gender,
	                                                                                     Date minAge, Date maxAge,
	                                                                                     int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + " and ob.concept_id = 2169 and ob.value_coded in (664,703) "
			                + " and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + " and ob.concept_id <> 1811 and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			                + programId + " and pg.date_completed is null group by pg.patient_id");
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryHIVResult = session
					        .createSQLQuery("select max(o.value_coded) from obs o where o.concept_id = 2169 and o.value_coded in (703,664)"
					                + "and (cast(o.obs_datetime as DATE)) <= "
					                + "'"
					                + df.format(endDate)
					                + "'"
					                + " and o.person_id=" + patientId);
					List<Integer> HivTestResult = queryHIVResult.list();
					
					if (HivTestResult.get(0) != null) {
						
						if (HivTestResult.get(0) == 703) {
							
							SQLQuery queryDate1 = session
							        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
							                + df.format(endDate) + "' and patient_id = " + patientId);
							
							List<Date> maxEnocunterDateTime = queryDate1.list();
							
							SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
							        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
							        + "' and concept_id = 5096 and person_id = " + patientId);
							
							List<Date> maxReturnVisitDay = queryDate2.list();
							
							SQLQuery queryDate = session
							        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
							                + patientId);
							List<Date> dateEnrolled = queryDate.list();
							
							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList().size() != 0)
								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList();
							
							for (Regimen r : regimens) {
								components = r.getComponents();
							}
							
							patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, getRegimensAsString(components), regimen, dateEnrolled.get(0),
							        enrollementDate };
							listPatientHistory.add(patientSatatus);
							
						}
					}
				}
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoAreNegatifBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + " and ob.concept_id = 2169 and ob.value_coded in (664,703) "
			                + "and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and ob.concept_id <> 1811 and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			                + programId + " and pg.date_completed is null group by pg.patient_id");
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryHIVResult = session
					        .createSQLQuery("select max(o.value_coded) from obs o where o.concept_id = 2169 and o.value_coded in (703,664) "
					                + "and (cast(o.obs_datetime as DATE)) <= "
					                + "'"
					                + df.format(endDate)
					                + "'"
					                + " and o.person_id=" + patientId);
					List<Integer> HivTestResult = queryHIVResult.list();
					
					if (HivTestResult.get(0) != null) {
						
						if (HivTestResult.get(0) == 664) {
							
							SQLQuery queryDate1 = session
							        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
							                + df.format(endDate) + "' and patient_id = " + patientId);
							
							List<Date> maxEnocunterDateTime = queryDate1.list();
							
							SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
							        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
							        + "' and concept_id = 5096 and person_id = " + patientId);
							
							List<Date> maxReturnVisitDay = queryDate2.list();
							
							SQLQuery queryDate = session
							        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
							                + patientId);
							List<Date> dateEnrolled = queryDate.list();
							
							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList().size() != 0)
								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList();
							
							for (Regimen r : regimens) {
								components = r.getComponents();
							}
							
							patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, getRegimensAsString(components), regimen, dateEnrolled.get(0),
							        enrollementDate };
							listPatientHistory.add(patientSatatus);
							
						}
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoGotCPNTestBetweenTheReportinPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("CPN Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "inner join encounter enc on pg.patient_id = enc.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + "and enc.encounter_type= 5 and (select(cast(enc.encounter_datetime as DATE))) >= "
			                + "'"
			                + df.format(startDate)
			                + "'"
			                + " and (cast(enc.encounter_datetime as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and ob.concept_id <> 1811 and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and enc.voided=0 and pg.program_id = "
			                + programId);
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
					                + df.format(startDate)
					                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
					                + df.format(endDate) + "' and encounter_type = 5 and patient_id = " + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select cast(max(value_datetime)as DATE))>= '" + df.format(startDate)
					        + "' and (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
					        + "' and concept_id = 5096 and person_id = " + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
					        .size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					
					patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
					listPatientHistory.add(patientSatatus);
					
				}
				
			}
		}
		
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoseCoupleIsDiscordant(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoseCoupleIsDiscordant(int programId, Date startDate, Date endDate,
	                                                                   String gender, Date minAge, Date maxAge,
	                                                                   int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join obs ob on pg.patient_id = ob.person_id "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") "
			        + "and ob.concept_id <> 1811 and pe.gender = 'f' and ob.concept_id = 2169 and ob.concept_id = 703 "
			        + "and ob.concept_id = 3082 and ob.value_coded = 703 "
			        + "and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and pg.program_id = "
			        + programId + " and pg.date_completed is null group by pg.patient_id");
			
			List<Integer> patientIds = query.list();
			Date maxReturnVisitDay = null;
			Date returnVisitDayPlusThreeMonths = null;
			
			for (Integer patientId : patientIds) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
				                + df.format(startDate)
				                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
				                + df.format(endDate) + "' and patient_id = " + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session
				        .createSQLQuery("select cast(max(value_datetime) as DATE ),(select cast(date_add(max(value_datetime),interval 3 month)as date)) "
				                + "from obs where (select cast(max(value_datetime)as DATE))>= '"
				                + df.format(startDate)
				                + "' and (select cast(max(value_datetime)as DATE))<= '"
				                + df.format(endDate)
				                + "' and concept_id = 5096 and person_id = " + patientId);
				
				List<Object[]> objects = queryDate2.list();
				for (Object[] ob : objects) {
					maxReturnVisitDay = (Date) ob[0];
					returnVisitDayPlusThreeMonths = (Date) ob[1];
				}
				
				SQLQuery queryDate = session
				        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
				                + patientId);
				List<Date> dateEnrolled = queryDate.list();
				
				if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
				        .size() != 0)
					regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
					        .getRegimenList();
				
				for (Regimen r : regimens) {
					components = r.getComponents();
				}
				
				patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
				        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay, lastReturnVisitDay,
				        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
				listPatientHistory.add(patientSatatus);
				
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoGaveBirthBetweenTheReportingPeriod(int programId, Date startDate,
	                                                                                 Date endDate, String gender,
	                                                                                 Date minAge, Date maxAge,
	                                                                                 int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("Date of delivery");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "inner join encounter enc on pg.patient_id = enc.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + "and enc.encounter_type= 6 and (select(cast(enc.encounter_datetime as DATE))) >= "
			                + "'"
			                + df.format(startDate)
			                + "'"
			                + " and (cast(enc.encounter_datetime as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and ob.concept_id = 5599 and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and enc.voided=0 and pg.program_id = "
			                + programId);
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
					                + df.format(startDate)
					                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
					                + df.format(endDate) + "' and encounter_type = 6 and patient_id = " + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select cast(max(value_datetime)as DATE))>= '" + df.format(startDate)
					        + "' and (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
					        + "' and concept_id = 5096 and person_id = " + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId)).getRegimenList()
					        .size() != 0)
						regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList();
					
					for (Regimen r : regimens) {
						components = r.getComponents();
					}
					
					patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
			
		}
		
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	public List<Object[]> getAllPatientsInPMTCTWhoAreExpectedInMaternityBetweenTheReportingPeriod(int programId,
	                                                                                              Date startDate,
	                                                                                              Date endDate,
	                                                                                              String gender,
	                                                                                              Date minAge, Date maxAge,
	                                                                                              int numberOfMonths) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		String enrollementDate = new String("Enrollement Date");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String lastEncountDate = new String("CPN Date");
		String lastReturnVisitDay = new String("Estimated Date of Delivery");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		Session session = getSessionFactory().getCurrentSession();
		
		try {
			
			SQLQuery query = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join obs ob on pg.patient_id = ob.person_id "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "inner join encounter enc on pg.patient_id = enc.patient_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + "and enc.encounter_type= 5 "
			                + "and enc.encounter_type <> 6 and ob.concept_id = 5596 and (cast(ob.value_datetime as DATE)) >= "
			                + "'"
			                + df.format(startDate)
			                + "'"
			                + " and (cast(ob.value_datetime as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + " and (cast(pg.date_enrolled as DATE)) <= "
			                + "'"
			                + df.format(endDate)
			                + "'"
			                + "and pg.voided = 0 and ob.voided = 0 and pe.voided = 0 and pa.voided = 0 and enc.voided=0 and pg.program_id = "
			                + programId + " and pg.date_completed is null");
			
			List<Integer> patientIds = query.list();
			
			for (Integer patientId : patientIds) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))>= '"
					                + df.format(startDate)
					                + "' and (select cast(max(encounter_datetime)as DATE))<= '"
					                + df.format(endDate)
					                + "' and encounter_type = 5 and encounter_type<> 6 and patient_id = " + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
					        + "from obs where (select cast(max(value_datetime)as DATE))>= '" + df.format(startDate)
					        + "' and (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
					        + "' and concept_id = 5596 and person_id = " + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					SQLQuery queryDate = session
					        .createSQLQuery("select cast(max(date_enrolled) as DATE) from patient_program where patient_id = "
					                + patientId);
					List<Date> dateEnrolled = queryDate.list();
					
					patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
					        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
					        getRegimensAsString(components), regimen, dateEnrolled.get(0), enrollementDate };
					listPatientHistory.add(patientSatatus);
					
				}
			}
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	public SortedMap<Date, Double> getSubMap(SortedMap<Date, Double> map, Date dateToSearch) {
		SortedMap<Date, Double> newMap = new TreeMap<Date, Double>();
		
		Iterator it = map.keySet().iterator();
		List<Date> dates = new ArrayList<Date>();
		
		while (it.hasNext()) {
			dates.add((Date) it.next());
			
		}
		int index = 0;
		for (int i = 0; i < dates.size(); i++) {
			index = i;
			if (dates != null && dateToSearch != null)
				if (dates.get(i).getTime() == dateToSearch.getTime() || dates.get(i).getTime() > dateToSearch.getTime()) {
					break;
				}
		}
		if (dates.size() != 0 && dates != null) {
			// sub map starts with the first concept value(ex:cd4count) 
			//up to the last value (or last cd4 cout) i.e the cd4 count he has now
			
			// submap(firstkey,laskey) 
			if (index != 0)
				newMap = map.subMap(dates.get(index - 1), new Date());
			else if (index == 0)
				newMap = map.subMap(dates.get(index), new Date());
		}
		
		return newMap;
		
	}
	
	public Date getWhenPatientStarted(Patient patient) {
		SQLQuery query = null;
		Session session = sessionFactory.getCurrentSession();
		
		StringBuffer strbuf = new StringBuffer();
		
		strbuf.append("SELECT min(o.date_activated) FROM orders o  ");
		strbuf.append("INNER JOIN drug_order dro on dro.order_id = o.order_id  AND ");
		strbuf.append("dro.drug_inventory_id<>22 and dro.drug_inventory_id<>27 AND dro.drug_inventory_id<>37 "
		        + "AND dro.drug_inventory_id<>23 AND dro.drug_inventory_id<>24 ");
		strbuf.append(" AND o.patient_id = ");
		strbuf.append(patient.getPatientId());
		
		query = session.createSQLQuery(strbuf.toString());
		
		List<Date> dates = query.list();
		Date whenPatientStarted = dates.get(0);
		
		return whenPatientStarted;
		
	}
	
	public String getAllPatientObs(Patient p, Concept c) {
		
		SQLQuery query = null;
		
		SortedMap<Date, Double> cd4CountAndDateSorted = new TreeMap<Date, Double>();
		
		Date minDate = getWhenPatientStarted(p);
		
		String values = new String();
		
		if (c != null && p != null)
			query = sessionFactory.getCurrentSession().createSQLQuery(
			    "(select obs_datetime,value_numeric from obs where  person_id= " + p.getPatientId() + " and concept_id= "
			            + c.getConceptId() + ")ORDER BY obs_datetime asc");
		
		List<Object[]> obj = query.list();
		
		for (Object[] ob : obj) {
			Date date = (Date) ob[0];
			
			Double conceptValue = (Double) ob[1];
			cd4CountAndDateSorted.put(date, conceptValue);
		}
		cd4CountAndDateSorted = getSubMap(cd4CountAndDateSorted, minDate);
		for (Date d : cd4CountAndDateSorted.keySet()) {
			values += cd4CountAndDateSorted.get(d) + "," + df.format(d) + ",";
		}
		
		return values;
	}
	
	public List<String> getAllPatientObsList(Patient p, Concept c) {
		
		SQLQuery query = null;
		
		SortedMap<Date, Double> cd4CountAndDateSorted = new TreeMap<Date, Double>();
		
		Date whenPatientHasStarted = getWhenPatientStarted(p);
		
		if (c != null && p != null) {
			query = sessionFactory.getCurrentSession().createSQLQuery(
			    "(select obs_datetime,value_numeric from obs where  person_id= " + p.getPatientId() + " and concept_id= "
			            + c.getConceptId() + " )ORDER BY obs_datetime asc");
		}
		List<Object[]> obj = query.list();
		
		List<String> conceptValueAndDate = new ArrayList<String>();
		
		for (Object[] ob : obj) {
			Date date = (Date) ob[0];
			
			Double conceptValue = (Double) ob[1];
			
			cd4CountAndDateSorted.put(date, conceptValue);
			
			conceptValueAndDate.add(cd4CountAndDateSorted.get(date) + "(" + df.format(date) + ")");
		}
		
		cd4CountAndDateSorted = getSubMap(cd4CountAndDateSorted, whenPatientHasStarted);
		return conceptValueAndDate;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllARVPatientsLostOnFollowUp(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getAllARVPatientsLostOnFollowUp(int programId, Date startDate, Date endDate, String gender,
	                                                      Date minAge, Date maxAge) {
		// TODO Auto-generated method stub	
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		ArrayList<Integer> patientsNotLostToFollowUp = new ArrayList<Integer>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfARVsDrugs() + ") "
			        + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 and o.voided = 0 "
			        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
			        + "' and ((pg.date_completed is null) or (pg.date_completed > '" + endDate + "')) and pg.program_id= "
			        + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryDate1 = session
				        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
				                + "(select(cast(max(encounter_datetime)as Date))) <= '" + df.format(endDate)
				                + "' and voided = 0 and patient_id = " + patientId);
				
				List<Date> maxEnocunterDateTime = queryDate1.list();
				
				SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE ) "
				        + "from obs where (select(cast(max(value_datetime)as Date))) <= '" + df.format(endDate)
				        + "' and concept_id = " + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
				        + " and voided = 0 and person_id = " + patientId);
				
				List<Date> maxReturnVisitDay = queryDate2.list();
				
				if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					        .get(0).getTime()) <= endDate.getTime())
					        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
					                .get(0).getTime()) <= endDate.getTime())) {
						
						patientsNotLostToFollowUp.add(patientId);
					}
					
					else {
						
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}
				
				else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
					
					if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
					        .get(0).getTime()) <= endDate.getTime())) {
						
						patientsNotLostToFollowUp.add(patientId);
						
					}
					
					else {
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsPreART(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
	 */
	@Override
	public List<Object[]> getAllPatientsPreART(int programId, Date startDate, Date endDate, String gender, Date minAge,
	                                           Date maxAge, int numberOfMonths) {
		// TODO Auto-generated method stub
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String datef = null;
		Session session = getSessionFactory().getCurrentSession();
		Object patientSatatus[] = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session
			        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			                + "inner join person pe on pg.patient_id = pe.person_id "
			                + "inner join patient pa on pg.patient_id = pa.patient_id "
			                + "inner join obs o on pg.patient_id = o.person_id "
			                + "inner join encounter en on pg.patient_id = en.patient_id "
			                + "inner join orders ord on pg.patient_id = ord.patient_id "
			                + "inner join drug_order do on ord.order_id = do.order_id "
			                + "inner join drug d on do.drug_inventory_id = d.drug_id "
			                + "where pg.patient_id in (select person_id from person "
			                + getPatientsAttributes(gender, minAge, maxAge)
			                + ") "
			                + " and ord.concept_id in (796,633,628,635,631,625,802,797,2203,1613,814,5424,792,5811,630,2833) "
			                + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 and o.voided = 0 "
			                + "and pa.voided = 0 and en.voided = 0 and o.concept_id <> 1811  and (cast(ord.date_activated as DATE)) <= '"
			                + df.format(endDate) + "' and o.concept_id = 5096 " + "and pg.program_id= " + programId);
			
			List<Integer> patientIds1 = query1.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
				                + "inner join person pe on pg.patient_id = pe.person_id "
				                + "inner join patient pa on pg.patient_id = pa.patient_id "
				                + "inner join obs o on pg.patient_id = o.person_id "
				                + "inner join encounter en on pg.patient_id = en.patient_id "
				                + "inner join orders ord on pg.patient_id = ord.patient_id "
				                + "where pg.patient_id in (select person_id from person "
				                + getPatientsAttributes(gender, minAge, maxAge)
				                + ") "
				                + " and (ord.concept_id = 794 or ord.concept_id = 749 or ord.concept_id = 795) "
				                + "and ord.date_stopped is null and (pg.voided = 0 and pe.voided = 0 and ord.voided = 0 and o.voided = 0 "
				                + "and pa.voided = 0 and en.voided = 0) and o.concept_id <> 1811 and (cast(ord.date_activated as DATE)) <= '"
				                + df.format(endDate) + "' and o.concept_id = 5096 and pg.program_id= " + programId
				                + " and pg.patient_id=" + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				SQLQuery query2Date = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds3 = query2Date.list();
				
				if ((patientIds2.size() == 0) && (patientIds3.size() == 0)) {
					
					SQLQuery queryDate1 = session
					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
					                + "(select(cast(max(encounter_datetime)as Date))) <= '" + df.format(endDate)
					                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and patient_id = "
					                + patientId);
					
					List<Date> maxEnocunterDateTime = queryDate1.list();
					
					SQLQuery queryDate2 = session
					        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
					                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
					                + df.format(endDate)
					                + "' and (select cast(max(value_datetime) as DATE )) is not null and concept_id = 5096 and person_id = "
					                + patientId);
					
					List<Date> maxReturnVisitDay = queryDate2.list();
					
					if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
						
						if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
						        .get(0).getTime()) <= endDate.getTime())
						        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
						                .get(0).getTime()) <= endDate.getTime())) {
							
							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList().size() != 0)
								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList();
							
							for (Regimen r : regimens) {
								components = r.getComponents();
							}
							
							patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, getRegimensAsString(components), regimen };
							listPatientHistory.add(patientSatatus);
							
						}
					}
					
					else if ((maxReturnVisitDay.get(0)) == null) {
						
						if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
						        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
							
							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList().size() != 0)
								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								        .getRegimenList();
							
							for (Regimen r : regimens) {
								components = r.getComponents();
							}
							
							patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay, getRegimensAsString(components), regimen };
							listPatientHistory.add(patientSatatus);
							
						}
					} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
					
					{
						
						if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
						        .getRegimenList().size() != 0)
							regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							        .getRegimenList();
						
						for (Regimen r : regimens) {
							components = r.getComponents();
						}
						
						patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay,
						        getRegimensAsString(components), regimen };
						listPatientHistory.add(patientSatatus);
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllChildrenPatientsEligibleForARVsButNotYetStarted(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Object[]> getAllChildrenPatientsEligibleForARVsButNotYetStarted(int programId, Date startDate, Date endDate,
	                                                                            String gender, Date minAge, Date maxAge) {
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		Object patientSatatus[] = null;
		String datef = null;
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		String lastPharmacyVisitDate = new String("Last CD4 Count Test");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Session session = getSessionFactory().getCurrentSession();
		
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		try {
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join encounter en on pg.patient_id = en.patient_id "
			        + "where  ((pg.date_completed is null) or (pg.date_completed > '" + df.format(endDate) + "'))"
			        + " pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
			        + ") " + " and o.concept_id <> 1811 and (pg.voided = 0 and pe.voided = 0 and o.voided = 0 "
			        + " and pa.voided = 0 and en.voided = 0) and (o.concept_id = 5497 "
			        + " and (cast(o.obs_datetime as DATE)) >= '" + df.format(startDate)
			        + "' and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate) + "' ) "
			        + " and o.concept_id = 1480 and o.value_coded = 1220 or o.value_coded= 1221 "
			        + " and o.concept_id = 730 " + " and DATE_FORMAT(FROM_DAYS(TO_DAYS('" + df.format(endDate)
			        + "') - TO_DAYS(pe.birthdate)), '%Y')+0 < 15 " + " and pg.program_id= " + programId);
			List<Integer> patientIds1 = query1.list();
			
			List<Date> maxReturnVisitDay = new ArrayList<Date>();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0) {
					
					try {
						
						SQLQuery query2Date = session
						        .createSQLQuery("select cast(max(obs_datetime) as DATE) from obs where concept_id = 5497 and (select cast(max(obs_datetime) as DATE)) >= '"
						                + df.format(startDate)
						                + "' and (select cast(max(obs_datetime) as DATE)) <= '"
						                + df.format(endDate)
						                + "' and concept_id <> 1811 and voided=0 and person_id = "
						                + patientId);
						List<Date> maxObsDateTimeCD4Count = query2Date.list();
						
						SQLQuery queryWhoStage = session
						        .createSQLQuery("select max(value_coded) from obs where concept_id = 1480 and value_coded in (1220,1221) and person_id="
						                + patientId);
						
						List<Integer> patientIdsWhoStage = queryWhoStage.list();
						
						SQLQuery queryCD4Percent = session
						        .createSQLQuery("select max(value_numeric) from obs where concept_id = 730 and person_id="
						                + patientId);
						
						List<Integer> patientIdsCD4Percent = queryCD4Percent.list();
						
						SQLQuery query3 = session
						        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
						                + maxObsDateTimeCD4Count.get(0)
						                + "' and concept_id <> 1811 and voided=0 and person_id = " + patientId);
						
						List<Double> maxValueNumericCD4Count = query3.list();
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
						                + df.format(endDate) + "' and patient_id = " + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE )"
						        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
						        + "' and concept_id = 5096 and person_id = " + patientId);
						
						maxReturnVisitDay = queryDate2.list();
						
						Double val = (maxValueNumericCD4Count.size() > 0) ? maxValueNumericCD4Count.get(0) : 1000;
						
						if (val < 1000.0) {
							if ((patientIdsWhoStage.get(0) == 1220) || (patientIdsWhoStage.get(0) == 1221))
								
								if (patientIdsCD4Percent.get(0) < 25)
								
								{
									
									SQLQuery query4 = session
									        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
									                + "inner join person pe on pg.patient_id = pe.person_id "
									                + "inner join patient pa on pg.patient_id = pa.patient_id "
									                + "inner join obs o on pe.person_id = o.person_id "
									                + "inner join encounter en on pg.patient_id = en.patient_id "
									                + "inner join orders ord on pg.patient_id = ord.patient_id "
									                + "inner join drug_order do on ord.order_id = do.order_id "
									                /*+ "inner join drug d on do.drug_inventory_id = d.drug_id "*/
									                + "where pg.patient_id in (select person_id from person "
									                + getPatientsAttributes(gender, minAge, maxAge)
									                + ") "
									                + " and ord.concept_id in (796,633,628,794,635,631,625,802,797,2203,1613,749,795,814,5424,792,5811,630,2833) "
									                + "and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
									                + "' and pg.program_id= " + programId + " and ord.patient_id =  "
									                + patientId);
									
									List<Integer> patientIds4 = query4.list();
									
									if (patientIds4.size() == 0) {
										
										try {
											
											SQLQuery queryDate3 = session
											        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
											                + maxObsDateTimeCD4Count.get(0)
											                + "' and person_id = "
											                + patientId);
											
											List<Date> lastPharmacyDates = queryDate3.list();
											
											if (((maxReturnVisitDay.get(0)) != null)
											        && (maxEnocunterDateTime.get(0) != null)) {
												
												if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime() && (maxEnocunterDateTime.get(0).getTime()) <= endDate
												        .getTime())
												        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate
												                .getTime() && (maxReturnVisitDay.get(0).getTime()) <= endDate
												                .getTime())) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											}
											
											else if ((maxReturnVisitDay.get(0)) == null) {
												
												if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime()
												        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
											
											{
												
												if (RegimenUtils
												        .getRegimenHistory(Context.getPatientService().getPatient(patientId))
												        .getRegimenList().size() != 0)
													regimens = RegimenUtils.getRegimenHistory(
													    Context.getPatientService().getPatient(patientId)).getRegimenList();
												
												for (Regimen r : regimens) {
													components = r.getComponents();
												}
												
												patientSatatus = new Object[] {
												        Context.getPatientService().getPatient(patientId),
												        maxEnocunterDateTime.get(0), lastEncountDate,
												        maxReturnVisitDay.get(0), lastReturnVisitDay,
												        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
												        lastPharmacyVisitDate };
												listPatientHistory.add(patientSatatus);
												
											}
											
										}
										
										catch (Exception e) {
											// TODO: handle exception
										}
										
									}
								}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
			SQLQuery query2 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join encounter en on pg.patient_id = en.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") "
			        + " and o.concept_id <> 1811 and (pg.voided = 0 and pe.voided = 0 and o.voided = 0 "
			        + " and pa.voided = 0 and en.voided = 0) and (o.concept_id = 5497 "
			        + " and (cast(o.obs_datetime as DATE)) >= '" + df.format(startDate)
			        + "' and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate) + "' ) "
			        + " and o.concept_id = 1480 and o.value_coded = 1220 or o.value_coded= 1221 "
			        + " and o.concept_id = 730 " + " and DATE_FORMAT(FROM_DAYS(TO_DAYS('" + df.format(endDate)
			        + "') - TO_DAYS(pe.birthdate)), '%Y')+0 < 15 " + " and pg.program_id= " + programId);
			List<Integer> patientIds2 = query2.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryExitFromCare = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIdsExitFromCare = queryExitFromCare.list();
				
				if (patientIdsExitFromCare.size() == 0) {
					
					try {
						
						SQLQuery query2Date = session
						        .createSQLQuery("select cast(max(obs_datetime) as DATE) from obs where concept_id = 5497 and (select cast(max(obs_datetime) as DATE)) >= '"
						                + df.format(startDate)
						                + "' and (select cast(max(obs_datetime) as DATE)) <= '"
						                + df.format(endDate)
						                + "' and concept_id <> 1811 and voided=0 and person_id = "
						                + patientId);
						List<Date> maxObsDateTimeCD4Count = query2Date.list();
						
						SQLQuery queryWhoStage = session
						        .createSQLQuery("select max(value_coded) from obs where concept_id = 1480 and value_coded in (1220,1221) and person_id="
						                + patientId);
						
						List<Integer> patientIdsWhoStage = queryWhoStage.list();
						
						SQLQuery queryCD4Percent = session
						        .createSQLQuery("select value_numeric from obs where concept_id = 730 and person_id="
						                + patientId);
						
						List<Integer> patientIdsCD4Percent = queryCD4Percent.list();
						
						SQLQuery query3 = session
						        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
						                + maxObsDateTimeCD4Count.get(0)
						                + "' and concept_id <> 1811 and voided=0 and person_id = " + patientId);
						
						List<Double> maxValueNumericCD4Count = query3.list();
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
						                + df.format(endDate) + "' and patient_id = " + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE )"
						        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
						        + "' and concept_id = 5096 and person_id = " + patientId);
						
						maxReturnVisitDay = queryDate2.list();
						
						Double val = (maxValueNumericCD4Count.size() > 0) ? maxValueNumericCD4Count.get(0) : 1000;
						
						if (val < 750.0) {
							if ((patientIdsWhoStage.get(0) == 1220) || (patientIdsWhoStage.get(0) == 1221))
								
								if (patientIdsCD4Percent.get(0) < 20)
								
								{
									
									SQLQuery query4 = session
									        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
									                + "inner join person pe on pg.patient_id = pe.person_id "
									                + "inner join patient pa on pg.patient_id = pa.patient_id "
									                + "inner join obs o on pe.person_id = o.person_id "
									                + "inner join encounter en on pg.patient_id = en.patient_id "
									                + "inner join orders ord on pg.patient_id = ord.patient_id "
									                + "inner join drug_order do on ord.order_id = do.order_id "
									                + "inner join drug d on do.drug_inventory_id = d.drug_id "
									                + "where pg.patient_id in (select person_id from person "
									                + getPatientsAttributes(gender, minAge, maxAge)
									                + ") "
									                + " and ord.concept_id in (796,633,628,794,635,631,625,802,797,2203,1613,749,795,814,5424,792,5811,630,2833) "
									                + "and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
									                + "' and pg.program_id= " + programId + " and ord.patient_id =  "
									                + patientId);
									
									List<Integer> patientIds4 = query4.list();
									
									if (patientIds4.size() == 0) {
										
										try {
											
											SQLQuery queryDate3 = session
											        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
											                + maxObsDateTimeCD4Count.get(0)
											                + "' and person_id = "
											                + patientId);
											
											List<Date> lastPharmacyDates = queryDate3.list();
											
											if (((maxReturnVisitDay.get(0)) != null)
											        && (maxEnocunterDateTime.get(0) != null)) {
												
												if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime() && (maxEnocunterDateTime.get(0).getTime()) <= endDate
												        .getTime())
												        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate
												                .getTime() && (maxReturnVisitDay.get(0).getTime()) <= endDate
												                .getTime())) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											}
											
											else if ((maxReturnVisitDay.get(0)) == null) {
												
												if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime()
												        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
											
											{
												
												if (RegimenUtils
												        .getRegimenHistory(Context.getPatientService().getPatient(patientId))
												        .getRegimenList().size() != 0)
													regimens = RegimenUtils.getRegimenHistory(
													    Context.getPatientService().getPatient(patientId)).getRegimenList();
												
												for (Regimen r : regimens) {
													components = r.getComponents();
												}
												
												patientSatatus = new Object[] {
												        Context.getPatientService().getPatient(patientId),
												        maxEnocunterDateTime.get(0), lastEncountDate,
												        maxReturnVisitDay.get(0), lastReturnVisitDay,
												        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
												        lastPharmacyVisitDate };
												listPatientHistory.add(patientSatatus);
												
											}
											
										}
										
										catch (Exception e) {
											// TODO: handle exception
										}
										
									}
								}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
			SQLQuery query3 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join obs o on pg.patient_id = o.person_id "
			        + "inner join encounter en on pg.patient_id = en.patient_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") "
			        + " and o.concept_id <> 1811 and (pg.voided = 0 and pe.voided = 0 and o.voided = 0 "
			        + " and pa.voided = 0 and en.voided = 0) and (o.concept_id = 5497 "
			        + " and (cast(o.obs_datetime as DATE)) >= '" + df.format(startDate)
			        + "' and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate) + "' ) "
			        + " and o.concept_id = 1480 and o.value_coded = 1220 or o.value_coded= 1221 "
			        + " and o.concept_id = 730 " + " and DATE_FORMAT(FROM_DAYS(TO_DAYS('" + df.format(endDate)
			        + "') - TO_DAYS(pe.birthdate)), '%Y')+0 < 15 " + " and pg.program_id= " + programId);
			List<Integer> patientIdsWhoStage2 = query3.list();
			
			for (Integer patientId : patientIds1) {
				
				SQLQuery queryExitFromCare = session
				        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
				                + patientId);
				
				List<Integer> patientIdsExitFromCare = queryExitFromCare.list();
				
				if (patientIdsExitFromCare.size() == 0) {
					
					try {
						
						SQLQuery query2Date = session
						        .createSQLQuery("select cast(max(obs_datetime) as DATE) from obs where concept_id = 5497 and (select cast(max(obs_datetime) as DATE)) >= '"
						                + df.format(startDate)
						                + "' and (select cast(max(obs_datetime) as DATE)) <= '"
						                + df.format(endDate)
						                + "' and concept_id <> 1811 and voided=0 and person_id = "
						                + patientId);
						List<Date> maxObsDateTimeCD4Count = query2Date.list();
						
						SQLQuery queryWhoStage = session
						        .createSQLQuery("select max(value_coded) from obs where concept_id = 1480 and value_coded in (1220,1221) and person_id="
						                + patientId);
						
						List<Integer> patientIdsWhoStage = queryWhoStage.list();
						
						SQLQuery queryCD4Percent = session
						        .createSQLQuery("select value_numeric from obs where concept_id = 730 and person_id="
						                + patientId);
						
						List<Integer> patientIdsCD4Percent = queryCD4Percent.list();
						
						SQLQuery queryCD4Count = session
						        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
						                + maxObsDateTimeCD4Count.get(0)
						                + "' and concept_id <> 1811 and voided=0 and person_id = " + patientId);
						
						List<Double> maxValueNumericCD4Count = queryCD4Count.list();
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
						                + df.format(endDate) + "' and patient_id = " + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE )"
						        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
						        + "' and concept_id = 5096 and person_id = " + patientId);
						
						maxReturnVisitDay = queryDate2.list();
						
						Double val = (maxValueNumericCD4Count.size() > 0) ? maxValueNumericCD4Count.get(0) : 1000;
						
						if (val < 350.0) {
							if ((patientIdsWhoStage.get(0) == 1220) || (patientIdsWhoStage.get(0) == 1221))
								
								if (patientIdsCD4Percent.get(0) < 20)
								
								{
									
									SQLQuery query4 = session
									        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
									                + "inner join person pe on pg.patient_id = pe.person_id "
									                + "inner join patient pa on pg.patient_id = pa.patient_id "
									                + "inner join obs o on pe.person_id = o.person_id "
									                + "inner join encounter en on pg.patient_id = en.patient_id "
									                + "inner join orders ord on pg.patient_id = ord.patient_id "
									                + "inner join drug_order do on ord.order_id = do.order_id "
									                + "inner join drug d on do.drug_inventory_id = d.drug_id "
									                + "where pg.patient_id in (select person_id from person "
									                + getPatientsAttributes(gender, minAge, maxAge)
									                + ") "
									                + " and ord.concept_id in (796,633,628,794,635,631,625,802,797,2203,1613,749,795,814,5424,792,5811,630,2833) "
									                + "and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
									                + "' and pg.program_id= " + programId + " and ord.patient_id =  "
									                + patientId);
									
									List<Integer> patientIds4 = query4.list();
									
									if (patientIds4.size() == 0) {
										
										try {
											
											SQLQuery queryDate3 = session
											        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
											                + maxObsDateTimeCD4Count.get(0)
											                + "' and person_id = "
											                + patientId);
											
											List<Date> lastPharmacyDates = queryDate3.list();
											
											if (((maxReturnVisitDay.get(0)) != null)
											        && (maxEnocunterDateTime.get(0) != null)) {
												
												if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime() && (maxEnocunterDateTime.get(0).getTime()) <= endDate
												        .getTime())
												        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate
												                .getTime() && (maxReturnVisitDay.get(0).getTime()) <= endDate
												                .getTime())) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											}
											
											else if ((maxReturnVisitDay.get(0)) == null) {
												
												if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
												        .getTime()
												        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
													
													if (RegimenUtils
													        .getRegimenHistory(
													            Context.getPatientService().getPatient(patientId))
													        .getRegimenList().size() != 0)
														regimens = RegimenUtils.getRegimenHistory(
														    Context.getPatientService().getPatient(patientId))
														        .getRegimenList();
													
													for (Regimen r : regimens) {
														components = r.getComponents();
													}
													
													patientSatatus = new Object[] {
													        Context.getPatientService().getPatient(patientId),
													        maxEnocunterDateTime.get(0), lastEncountDate,
													        maxReturnVisitDay.get(0), lastReturnVisitDay,
													        getRegimensAsString(components), regimen,
													        lastPharmacyDates.get(0), lastPharmacyVisitDate };
													listPatientHistory.add(patientSatatus);
													
												}
											} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
											
											{
												
												if (RegimenUtils
												        .getRegimenHistory(Context.getPatientService().getPatient(patientId))
												        .getRegimenList().size() != 0)
													regimens = RegimenUtils.getRegimenHistory(
													    Context.getPatientService().getPatient(patientId)).getRegimenList();
												
												for (Regimen r : regimens) {
													components = r.getComponents();
												}
												
												patientSatatus = new Object[] {
												        Context.getPatientService().getPatient(patientId),
												        maxEnocunterDateTime.get(0), lastEncountDate,
												        maxReturnVisitDay.get(0), lastReturnVisitDay,
												        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
												        lastPharmacyVisitDate };
												listPatientHistory.add(patientSatatus);
												
											}
											
										}
										
										catch (Exception e) {
											// TODO: handle exception
										}
										
									}
								}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
			//		SQLQuery queryWhoStageAdultOneAndTwo = session
			//		        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			//		                + "inner join person pe on pg.patient_id = pe.person_id "
			//		                + "inner join patient pa on pg.patient_id = pa.patient_id "
			//		                + "inner join obs o on pg.patient_id = o.person_id "
			//		                + "inner join encounter en on pg.patient_id = en.patient_id "
			//		                + "where pg.patient_id in (select person_id from person "
			//		                + getPatientsAttributes(gender, minAge, maxAge) + ") "
			//		                + " and o.concept_id <> 1811 and (pg.voided = 0 and pe.voided = 0 and o.voided = 0 "
			//		                + " and pa.voided = 0 and en.voided = 0) and (o.concept_id = 5497 "
			//		                + " and (cast(o.obs_datetime as DATE)) >= '" + df.format(startDate)
			//		                + "' and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate) + "' ) "
			//		                + " and o.concept_id = 1480 and o.value_coded = 1220 or o.value_coded= 1221 "
			//		                + " and o.concept_id = 730 " + " and DATE_FORMAT(FROM_DAYS(TO_DAYS('" + df.format(endDate)
			//		                + "') - TO_DAYS(pe.birthdate)), '%Y')+0 < 14 " + " and pg.program_id= " + programId
			//		                + " and pg.date_completed is null ");
			//		List<Integer> patientIds1 = query1.list();
			//		
			//		List<Date> maxReturnVisitDay = new ArrayList<Date>();
			//		
			//		for (Integer patientId : patientIds1) {
			//			
			//			SQLQuery query2 = session
			//			        .createSQLQuery("select distinct o.person_id from obs o where o.concept_id = 1811 and o.person_id="
			//			                + patientId);
			//			
			//			List<Integer> patientIds2 = query2.list();
			//			
			//			if (patientIds2.size() == 0) {
			//				
			//				try {
			//					
			//					SQLQuery query2Date = session
			//					        .createSQLQuery("select cast(max(obs_datetime) as DATE) from obs where concept_id = 5497 and (select cast(max(obs_datetime) as DATE)) >= '"
			//					                + df.format(startDate)
			//					                + "' and (select cast(max(obs_datetime) as DATE)) <= '"
			//					                + df.format(endDate)
			//					                + "' and concept_id <> 1811 and voided=0 and person_id = "
			//					                + patientId);
			//					List<Date> maxObsDateTimeCD4Count = query2Date.list();
			//					
			//					SQLQuery queryWhoStage = session
			//					        .createSQLQuery("select max(value_coded) from obs where concept_id = 1480 and value_coded in (1220,1221) and person_id="
			//					                + patientId);
			//					
			//					List<Integer> patientIdsWhoStage = queryWhoStage.list();
			//					
			//					SQLQuery queryCD4Percent = session
			//					        .createSQLQuery("select value_numeric from obs where concept_id = 730 and person_id="
			//					                + patientId);
			//					
			//					List<Integer> patientIdsCD4Percent = queryWhoStage.list();
			//					
			//					SQLQuery query3 = session
			//					        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
			//					                + maxObsDateTimeCD4Count.get(0)
			//					                + "' and concept_id <> 1811 and voided=0 and person_id = " + patientId);
			//					
			//					List<Double> maxValueNumericCD4Count = query3.list();
			//					
			//					SQLQuery queryDate1 = session
			//					        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where (select cast(max(encounter_datetime)as DATE))<= '"
			//					                + df.format(endDate) + "' and patient_id = " + patientId);
			//					
			//					List<Date> maxEnocunterDateTime = queryDate1.list();
			//					
			//					SQLQuery queryDate2 = session.createSQLQuery("select cast(max(value_datetime) as DATE )"
			//					        + "from obs where (select cast(max(value_datetime)as DATE))<= '" + df.format(endDate)
			//					        + "' and concept_id = 5096 and person_id = " + patientId);
			//					
			//					maxReturnVisitDay = queryDate2.list();
			//					
			//					Double val = (maxValueNumericCD4Count.size() > 0) ? maxValueNumericCD4Count.get(0) : 1000;
			//					
			//					if (val < 1000.0) {
			//						if ((patientIdsWhoStage.get(0) == 1220) || (patientIdsWhoStage.get(0) == 1221))
			//							
			//							if (patientIdsCD4Percent.get(0) < 25)
			//
			//							{
			//								
			//								SQLQuery query4 = session
			//								        .createSQLQuery("select distinct pg.patient_id from patient_program pg "
			//								                + "inner join person pe on pg.patient_id = pe.person_id "
			//								                + "inner join patient pa on pg.patient_id = pa.patient_id "
			//								                + "inner join obs o on pe.person_id = o.person_id "
			//								                + "inner join encounter en on pg.patient_id = en.patient_id "
			//								                + "inner join orders ord on pg.patient_id = ord.patient_id "
			//								                + "inner join drug_order do on ord.order_id = do.order_id "
			//								                + "inner join drug d on do.drug_inventory_id = d.drug_id "
			//								                + "where pg.patient_id in (select person_id from person "
			//								                + getPatientsAttributes(gender, minAge, maxAge)
			//								                + ") "
			//								                + " and d.concept_id in (796,633,628,794,635,631,625,802,797,2203,1613,749,795,814,5424,792,5811,630,2833) "
			//								                + "and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate)
			//								                + "' and pg.program_id= " + programId
			//								                + " and pg.date_completed is null and ord.patient_id =  " + patientId);
			//								
			//								List<Integer> patientIds4 = query4.list();
			//								
			//								if (patientIds4.size() == 0) {
			//									
			//									try {
			//										
			//										SQLQuery queryDate3 = session
			//										        .createSQLQuery("select value_numeric from obs where concept_id = 5497 and obs_datetime = '"
			//										                + maxObsDateTimeCD4Count.get(0) + "' and person_id = " + patientId);
			//										
			//										List<Date> lastPharmacyDates = queryDate3.list();
			//										
			//										if ((maxReturnVisitDay.get(0)) != null) {
			//											
			//											if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
			//											        .getTime() && (maxEnocunterDateTime.get(0).getTime()) <= endDate
			//											        .getTime())
			//											        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate
			//											                .getTime() && (maxReturnVisitDay.get(0).getTime()) <= endDate
			//											                .getTime())) {
			//												
			//												if (RegimenUtils.getRegimenHistory(
			//												    Context.getPatientService().getPatient(patientId)).getRegimenList()
			//												        .size() != 0)
			//													regimens = RegimenUtils.getRegimenHistory(
			//													    Context.getPatientService().getPatient(patientId)).getRegimenList();
			//												
			//												for (Regimen r : regimens) {
			//													components = r.getComponents();
			//												}
			//												
			//												patientSatatus = new Object[] {
			//												        Context.getPatientService().getPatient(patientId),
			//												        maxEnocunterDateTime.get(0), lastEncountDate,
			//												        maxReturnVisitDay.get(0), lastReturnVisitDay,
			//												        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
			//												        lastPharmacyVisitDate };
			//												listPatientHistory.add(patientSatatus);
			//												
			//											}
			//										}
			//
			//										else if ((maxReturnVisitDay.get(0)) == null) {
			//											
			//											if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate
			//											        .getTime()
			//											        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
			//												
			//												if (RegimenUtils.getRegimenHistory(
			//												    Context.getPatientService().getPatient(patientId)).getRegimenList()
			//												        .size() != 0)
			//													regimens = RegimenUtils.getRegimenHistory(
			//													    Context.getPatientService().getPatient(patientId)).getRegimenList();
			//												
			//												for (Regimen r : regimens) {
			//													components = r.getComponents();
			//												}
			//												
			//												patientSatatus = new Object[] {
			//												        Context.getPatientService().getPatient(patientId),
			//												        maxEnocunterDateTime.get(0), lastEncountDate,
			//												        maxReturnVisitDay.get(0), lastReturnVisitDay,
			//												        getRegimensAsString(components), regimen, lastPharmacyDates.get(0),
			//												        lastPharmacyVisitDate };
			//												listPatientHistory.add(patientSatatus);
			//												
			//											}
			//										} else if ((maxReturnVisitDay.get(0).getTime() > endDate.getTime()))
			//
			//										{
			//											
			//											if (RegimenUtils.getRegimenHistory(
			//											    Context.getPatientService().getPatient(patientId)).getRegimenList().size() != 0)
			//												regimens = RegimenUtils.getRegimenHistory(
			//												    Context.getPatientService().getPatient(patientId)).getRegimenList();
			//											
			//											for (Regimen r : regimens) {
			//												components = r.getComponents();
			//											}
			//											
			//											patientSatatus = new Object[] {
			//											        Context.getPatientService().getPatient(patientId),
			//											        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
			//											        lastReturnVisitDay, getRegimensAsString(components), regimen,
			//											        lastPharmacyDates.get(0), lastPharmacyVisitDate };
			//											listPatientHistory.add(patientSatatus);
			//											
			//										}
			//										
			//									}
			//									
			//									catch (Exception e) {
			//										// TODO: handle exception
			//									}
			//									
			//								}
			//							}
			//					}
			//				}
			//				catch (Exception e) {
			//					e.printStackTrace();
			//				}
			//				
			//			}
			//		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return listPatientHistory;
	}
	
	/**
	 * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsNewOnSecondLineRegimenBetweenDate(int,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Object[]> getAllPatientsNewOnSecondLineRegimenBetweenDate(int programId, Date startDate, Date endDate,
	                                                                      String gender, Date minAge, Date maxAge) {
		// TODO Auto-generated method stub
		
		List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Session session = getSessionFactory().getCurrentSession();
			String lastEncountDate = new String("Last Encounter Date");
			String lastReturnVisitDay = new String("Last Return Visit Date");
			String regimen = new String("Patient Regimen");
			List<Regimen> regimens = new ArrayList<Regimen>();
			Set<RegimenComponent> components = new HashSet<RegimenComponent>();
			
			Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
			
			SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
			        + "inner join person pe on pg.patient_id = pe.person_id "
			        + "inner join patient pa on pg.patient_id = pa.patient_id "
			        + "inner join orders ord on pg.patient_id = ord.patient_id "
			        + "inner join drug_order do on ord.order_id = do.order_id "
			        + "inner join drug d on do.drug_inventory_id = d.drug_id "
			        + "where pg.patient_id in (select person_id from person "
			        + getPatientsAttributes(gender, minAge, maxAge) + ") " + " and ord.concept_id IN ("
			        + GlobalProperties.gpGetListOfSecondLineDrugs() + ") "
			        + "and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
			        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
			        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
			        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
			
			List<Integer> patientIds1 = query1.list();
			for (Integer patientId : patientIds1) {
				
				SQLQuery query2 = session.createSQLQuery("select distinct o.person_id from obs o where o.concept_id = "
				        + Integer.parseInt(GlobalProperties.gpGetExitFromCareConceptId())
				        + " and (cast(o.obs_datetime as DATE)) <= '" + df.format(endDate)
				        + "' and o.voided = 0 and o.person_id=" + patientId);
				
				List<Integer> patientIds2 = query2.list();
				
				if (patientIds2.size() == 0)
				
				{
					
					SQLQuery queryMinStartDate = session
					        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
					                + " inner join drug_order do on ord.order_id = do.order_id "
					                + " inner join drug d on do.drug_inventory_id = d.drug_id "
					                + " where ord.concept_id IN ("
					                + GlobalProperties.gpGetListOfSecondLineDrugs()
					                + ") "
					                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
					                + patientId);
					
					List<Date> patientIdsMinStartDate = queryMinStartDate.list();
					
					if ((patientIdsMinStartDate.get(0).getTime() >= startDate.getTime())
					        && (patientIdsMinStartDate.get(0).getTime() <= endDate.getTime())) {
						
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						if (((maxReturnVisitDay.get(0)) != null) && (maxEnocunterDateTime.get(0) != null)) {
							
							if (((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxEnocunterDateTime
							        .get(0).getTime()) <= endDate.getTime())
							        || ((maxReturnVisitDay.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime() && (maxReturnVisitDay
							                .get(0).getTime()) <= endDate.getTime())) {
								
								//								if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//								        .getRegimenList().size() != 0)
								//									regimens = RegimenUtils.getRegimenHistory(
								//									    Context.getPatientService().getPatient(patientId)).getRegimenList();
								//								
								//								for (Regimen r : regimens) {
								//									components = r.getComponents();
								//								}
								
								patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay };
								listPatientHistory.add(patientSatatus);
								
							}
						}
						
						else if (((maxReturnVisitDay.get(0)) == null) && (maxEnocunterDateTime.get(0) != null)) {
							
							if ((maxEnocunterDateTime.get(0).getTime()) >= threeMonthsBeforeEndDate.getTime()
							        && (maxEnocunterDateTime.get(0).getTime()) <= endDate.getTime()) {
								
								//								if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
								//								        .getRegimenList().size() != 0)
								//									regimens = RegimenUtils.getRegimenHistory(
								//									    Context.getPatientService().getPatient(patientId)).getRegimenList();
								//								
								//								for (Regimen r : regimens) {
								//									components = r.getComponents();
								//								}
								
								patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
								        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
								        lastReturnVisitDay };
								listPatientHistory.add(patientSatatus);
								
							}
						}
						
						else if ((maxReturnVisitDay.get(0) != null)
						        && (maxReturnVisitDay.get(0).getTime()) > endDate.getTime())
						
						{
							
							//							if (RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							//							        .getRegimenList().size() != 0)
							//								regimens = RegimenUtils.getRegimenHistory(Context.getPatientService().getPatient(patientId))
							//								        .getRegimenList();
							//							
							//							for (Regimen r : regimens) {
							//								components = r.getComponents();
							//							}
							
							patientSatatus = new Object[] { Context.getPatientService().getPatient(patientId),
							        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0),
							        lastReturnVisitDay };
							listPatientHistory.add(patientSatatus);
							
						}
					}
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return listPatientHistory;
	}

	/**
     * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int, java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date, int)
     */
    @Override
    public List<Object[]> getAllPatientsOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int programId,
                                                                                                        Date startDate,
                                                                                                        Date endDate,
                                                                                                        String gender,
                                                                                                        Date minAge,
                                                                                                        Date maxAge) {
	    // TODO Auto-generated method stub
    	List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//try {
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date twelveMonthsBeforeStartDate = UsageStatsUtils.addDaysToDate(startDate, -12);
		Date twelveMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -12);
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		        + "where ((pg.date_completed is null) or (cast(pg.date_completed as DATE) > ' " + df.format(endDate)  + " ')) "
		        + " and pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfARVsDrugs() + ") "
		        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
		        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
		        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
		        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			
				
				SQLQuery queryMinStartDate = session
				        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
				                // + " inner join drug_order do on ord.order_id = do.order_id "
				                // + " inner join drug d on do.drug_inventory_id = d.drug_id "
				                + " where ord.concept_id IN ("
				                + GlobalProperties.gpGetListOfARVsDrugs()
				                + ") "
				                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
				                + patientId);
				
				List<Date> patientIdsMinStartDate = queryMinStartDate.list();
				
				if (patientIdsMinStartDate.get(0) != null) {
					
					if ((patientIdsMinStartDate.get(0).getTime() >= twelveMonthsBeforeStartDate.getTime())
					        && patientIdsMinStartDate.get(0).getTime() <= twelveMonthsBeforeEndDate.getTime()) {
											
						
						/*SQLQuery queryTransferInDate = session.createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
								+ Integer.parseInt(GlobalProperties.gpGetTransferredInConceptId()) + " and value_coded = "
						        + Integer.parseInt(GlobalProperties.gpGetyesAsAnswerToTransferredInConceptId())
						        + " and (select cast(max(obs_datetime)as DATE)) is not null and (select cast(max(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id=" + patientId);
						
						List<Date> patientIdsTransferInDate = queryTransferInDate.list();
						
						if (patientIdsTransferInDate.get(0)!= null) {
							
							
							if (patientIdsMinStartDate.get(0).getTime() >= patientIdsTransferInDate.get(0).getTime()) {*/
							
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
					
				}
				
			}
			
		//}
		
		//}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		return listPatientHistory;
    }

	/**
     * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllPatientsEvenLostOnFollowUPOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int, java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public List<Object[]> getAllPatientsEvenLostOnFollowUPOnTreatment12MonthsAfterInitialisationBetweenTheReportingPeriod(int programId,
                                                                                                                          Date startDate,
                                                                                                                          Date endDate,
                                                                                                                          String gender,
                                                                                                                          Date minAge,
                                                                                                                          Date maxAge) {
	    // TODO Auto-generated method stub
    	List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//try {
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date twelveMonthsBeforeStartDate = UsageStatsUtils.addDaysToDate(startDate, -12);
		Date twelveMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -12);
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		       // + "where ((pg.date_completed is null) or (cast(pg.date_completed as DATE) > ' " + df.format(endDate)  + " ')) "
		        + " where pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfARVsDrugs() + ") "
		        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
		        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
		        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
		        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			
				
				SQLQuery queryMinStartDate = session
				        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
				                // + " inner join drug_order do on ord.order_id = do.order_id "
				                // + " inner join drug d on do.drug_inventory_id = d.drug_id "
				                + " where ord.concept_id IN ("
				                + GlobalProperties.gpGetListOfARVsDrugs()
				                + ") "
				                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
				                + patientId);
				
				List<Date> patientIdsMinStartDate = queryMinStartDate.list();
				
				if (patientIdsMinStartDate.get(0) != null) {
					
					if ((patientIdsMinStartDate.get(0).getTime() >= twelveMonthsBeforeStartDate.getTime())
					        && patientIdsMinStartDate.get(0).getTime() <= twelveMonthsBeforeEndDate.getTime()) {
											
						
						/*SQLQuery queryTransferInDate = session.createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
								+ Integer.parseInt(GlobalProperties.gpGetTransferredInConceptId()) + " and value_coded = "
						        + Integer.parseInt(GlobalProperties.gpGetyesAsAnswerToTransferredInConceptId())
						        + " and (select cast(max(obs_datetime)as DATE)) is not null and (select cast(max(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id=" + patientId);
						
						List<Date> patientIdsTransferInDate = queryTransferInDate.list();
						
						if (patientIdsTransferInDate.get(0)!= null) {
							
							
							if (patientIdsMinStartDate.get(0).getTime() >= patientIdsTransferInDate.get(0).getTime()) {*/
							
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
					
				}
				
			}
			
		//}
		
		//}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		return listPatientHistory;
    }

	/**
     * @see org.openmrs.module.programOver.db.ProgramOverviewDAO#getAllNewPregnantWomenOnARVs(int, java.util.Date, java.util.Date, java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public List<Object[]> getAllNewPregnantWomenOnARVs(int programId, Date startDate, Date endDate, String gender,
                                                       Date minAge, Date maxAge) {
	    // TODO Auto-generated method stub
    	List<Object[]> listPatientHistory = new ArrayList<Object[]>();
		Object patientSatatus[] = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//try {
		Session session = getSessionFactory().getCurrentSession();
		String lastEncountDate = new String("Last Encounter Date");
		String lastReturnVisitDay = new String("Last Return Visit Date");
		String regimen = new String("Patient Regimen");
		List<Regimen> regimens = new ArrayList<Regimen>();
		Set<RegimenComponent> components = new HashSet<RegimenComponent>();
		Date threeMonthsBeforeEndDate = UsageStatsUtils.addDaysToDate(endDate, -3);
		
		SQLQuery query1 = session.createSQLQuery("select distinct pg.patient_id from patient_program pg "
		        + "inner join person pe on pg.patient_id = pe.person_id "
		        + "inner join patient pa on pg.patient_id = pa.patient_id "
		        + "inner join orders ord on pg.patient_id = ord.patient_id "
		        //+ "where ((pg.date_completed is null) or(cast(pg.date_completed as DATE)> ' " + df.format(endDate)  + " ')) "
		        + " where pg.patient_id in (select person_id from person " + getPatientsAttributes(gender, minAge, maxAge)
		        + ") " + " and ord.concept_id IN (" + GlobalProperties.gpGetListOfARVsDrugs() + ") "
		        + " and pg.voided = 0 and pe.voided = 0 and ord.voided = 0 "
		        + "and pa.voided = 0 and (cast(ord.date_activated as DATE)) >= '" + df.format(startDate)
		        + "' and (cast(ord.date_activated as DATE)) <= '" + df.format(endDate) + "' and pg.program_id= " + programId
		        + " and pg.date_enrolled <= '" + df.format(endDate) + "' ");
		
		List<Integer> patientIds1 = query1.list();
		
		for (Integer patientId : patientIds1) {
			
				
				SQLQuery queryMinStartDate = session
				        .createSQLQuery("select (cast(min(ord.date_activated)as Date)) from orders ord "
				                // + " inner join drug_order do on ord.order_id = do.order_id "
				                // + " inner join drug d on do.drug_inventory_id = d.drug_id "
				                + " where ord.concept_id IN ("
				                + GlobalProperties.gpGetListOfARVsDrugs()
				                + ") "
				                + " and (select (cast(min(ord.date_activated)as Date))) is not null and ord.voided = 0 and ord.patient_id = "
				                + patientId);
				
				List<Date> patientIdsMinStartDate = queryMinStartDate.list();
				
				if (patientIdsMinStartDate.get(0) != null) {
					
					if ((patientIdsMinStartDate.get(0).getTime() >= startDate.getTime())
					        && patientIdsMinStartDate.get(0).getTime() <= endDate.getTime()) {
											
						
						SQLQuery queryTransferInDate = session.createSQLQuery("select cast(max(obs_datetime)as DATE) from obs where concept_id = "
								+ Integer.parseInt(GlobalProperties.gpGetTransferredInConceptId()) + " and value_coded = "
						        + Integer.parseInt(GlobalProperties.gpGetyesAsAnswerToTransferredInConceptId())
						        + " and (select cast(max(obs_datetime)as DATE)) is not null and (select cast(max(obs_datetime)as DATE)) <= "
				                + "'" + df.format(endDate) + "'" + " and voided = 0 and person_id=" + patientId);
						
						List<Date> patientIdsTransferInDate = queryTransferInDate.list();
						
						if (patientIdsTransferInDate.get(0)!= null) {
							
							
							if (patientIdsMinStartDate.get(0).getTime() >= patientIdsTransferInDate.get(0).getTime()) {
							
						SQLQuery queryDate1 = session
						        .createSQLQuery("select cast(max(encounter_datetime)as DATE) from encounter where "
						                + "(select(cast(max(encounter_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and (select cast(max(encounter_datetime)as DATE)) is not null and voided = 0 and patient_id = "
						                + patientId);
						
						List<Date> maxEnocunterDateTime = queryDate1.list();
						
						SQLQuery queryDate2 = session
						        .createSQLQuery("select cast(max(value_datetime) as DATE ) "
						                + "from obs where (select(cast(max(value_datetime)as Date))) <= '"
						                + df.format(endDate)
						                + "' and concept_id = "
						                + Integer.parseInt(GlobalProperties.gpGetReturnVisitDateConceptId())
						                + " and (select cast(max(value_datetime) as DATE )) is not null and voided = 0 and person_id = "
						                + patientId);
						
						List<Date> maxReturnVisitDay = queryDate2.list();
						
						patientSatatus = new Object[] {
						        Context.getPatientService().getPatient(patientId).getPatientIdentifier(),
						        maxEnocunterDateTime.get(0), lastEncountDate, maxReturnVisitDay.get(0), lastReturnVisitDay };
						listPatientHistory.add(patientSatatus);
						
					}
					
				}
				
			}
			
		}
		
		}
		/*catch (Exception e) {
			e.printStackTrace();
		}*/
		return listPatientHistory;
    }
	
}
