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

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.SessionFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 *
 */
public class PatientDownloadController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private SessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String programIdKey = "";
		
		if (request.getParameter("checkType") != null) {
			
			programIdKey = request.getParameter("checkType");
			
			//		Date lastMidnight = UsageStatsUtils.getPreviousMidnight(null);
			//		Date twoWeekAgo = UsageStatsUtils.addDaysToDate(lastMidnight, -14);
			
			List<Object[]> listPatientHistory = (List<Object[]>) request.getSession().getAttribute(
			    "patientSize_" + (Integer.parseInt(request.getParameter("lineNumber")) - 1));
			
			
			doDownload(request, response, listPatientHistory, "patients List.csv", programIdKey);
		}
		return null;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param request
	 * @param response
	 * @param patients
	 * @param filename
	 * @param title
	 * @throws IOException
	 */
	private void doDownload(HttpServletRequest request, HttpServletResponse response, List<Object[]> listPatientHistory,
	                        String filename, String title) throws IOException {		
		ServletOutputStream outputStream = response.getOutputStream();		
		List<Object> nameOfeventDate = new ArrayList<Object>();
		List<Object> returnVisitDates = new ArrayList<Object>();
		List<Object> consultationDates = new ArrayList<Object>();		
		
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		outputStream.println("" + title);
		outputStream.println();
		
		Object eventDate = null;
		Object eventDate1 = null;
		
		Object consultatioDatetitle = null;	
		
		for (Object[] objects : listPatientHistory) {
			//the object defined below holds the title of added columns
			if (objects.length > 3 && objects.length <= 5) {
				nameOfeventDate.add(objects[2]);
				returnVisitDates.add(objects[4]);
				eventDate = nameOfeventDate.get(0);
				eventDate1 = returnVisitDates.get(0);
				//drugTitles.add(objects[6]);
				//regimenTitle = drugTitles.get(0);
				outputStream.println("Identifier , Given Name , Family Name , Age , Gender , " + eventDate
				        + ", " + eventDate1 + "");
				
			}
			if (objects.length == 3) {
				nameOfeventDate.add(objects[2]);
				eventDate = nameOfeventDate.get(0);
				outputStream.println("Identifier , Given Name , Family Name , Age , Gender , " + eventDate + "");
				
			}
			if (objects.length > 5){
				//System.out.println(">>>>>>>eligibility");
				nameOfeventDate.add(objects[2]);
				returnVisitDates.add(objects[4]);
				eventDate = nameOfeventDate.get(0);
				eventDate1 = returnVisitDates.get(0);
				//drugTitles.add(objects[6]);
				//regimenTitle = drugTitles.get(0);
				consultationDates.add(objects[6]);
				consultatioDatetitle = consultationDates.get(0);
				//startingARVDate.add(objects[8]);
				//CD4CountDate.add(objects[10]);
				
				
				outputStream.println("Identifier , Given Name , Family Name , Age , Gender , " + eventDate
				        + " , " + eventDate1 + "," + consultatioDatetitle  +  "");
			}
						//outputStream
					     //   .println("Identifier , Given Name , Family Name , Age , Gender , Birth Day , last encounter date , return visit day , regimen");
			
			outputStream.println();
			for (Object[] object : listPatientHistory) {
				PatientIdentifier patientIdentifier = (PatientIdentifier) object [0];
				Patient patient = patientIdentifier.getPatient();
				if (object.length > 3 && object.length <= 5) {
					
					
					
					Date encounterDate = (Date) object[1];
					Date returnVisitDay = (Date) object[3];
	
					if(patient.getGivenName()!=null){
					
					outputStream.println(patient.getPatientId().toString() + " , " + patient.getGivenName().toString()
			        + " , " + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
			        + patient.getGender().toString() + " , " 
			        + encounterDate + " , " + returnVisitDay);
					}
					else{
						
						outputStream.println(patient.getPatientId().toString() + " , " + ""
					        + " , " + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
					        + patient.getGender().toString() + " , " 
					        + encounterDate + " , " + returnVisitDay);
					}
					
					
					/*outputStream.println(patient.getPatientId().toString() + " , " + patient.getGivenName().toString() !=null ? patient.getGivenName().toString() : " "
					        + " , " + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
					        + patient.getGender().toString() + " , " 
					        + encounterDate + " , " + returnVisitDay);*/
					
				}
				
				if (object.length == 3) {
					
					Date encounterDate = (Date) object[1];
					outputStream.println(patient.getPatientId().toString() + " , " + patient.getGivenName().toString()
					        + " , " + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
					        + patient.getGender().toString() + " , "
					        + encounterDate);
					
				}
				if (object.length > 5) {		
								
					Date encounterDate = (Date) object[1];
					Date returnVisitDay = (Date) object[3];
					Date consultationDate = (Date) object[5];
					
					outputStream.print(patient.getPatientId().toString() + " , " + patient.getGivenName().toString() + " , "
					        + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
					        + patient.getGender().toString() + " , "
					        + encounterDate + " , " + returnVisitDay);
				
					
					outputStream.print(" , " + consultationDate);
					
					outputStream.println();					

					
				}
				
				if (object.length == 1){
					
					
					outputStream.println(patient.getPatientId().toString() + " , " + patient.getGivenName().toString()
					        + " , " + patient.getFamilyName().toString() + " , " + patient.getAge().toString() + " , "
					        + patient.getGender().toString());
					
				}
				
			}
			
			outputStream.flush();
			outputStream.close();
		}
		
	}
}
