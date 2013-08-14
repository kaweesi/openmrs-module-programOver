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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.programOver.advice.UsageStatsUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 *This controller is used to get the the enrolled program statics between Range of date(startDate
 * and endDate)
 */
public class ProgramOverFormController extends ParameterizableViewController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	static String msgToDisplay;
	
	public static String getMsgToDisplay() {
		return msgToDisplay;
	}
	
	/**
	 * @param msgToDisplay the msgToDisplay to set
	 */
	public static void setMsgToDisplay(String msgToDisplay) {
		ProgramOverFormController.msgToDisplay = msgToDisplay;
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		int programId = Integer.parseInt(request.getParameter("programId"));
		String programName = Context.getProgramWorkflowService().getProgram(programId).getName();
		String check[] = request.getParameterValues("id"); 
		String[] checkType = request.getParameterValues("checkType");
	
		ProgramCriteraList pg = new ProgramCriteraList();
		Map<String, Integer> patientNumbers = new HashMap<String, Integer>();
		List<Object[]> listPatientHistory = null;
		
		Date lastMidnight = UsageStatsUtils.getPreviousMidnight(null);
		Date twoWeekAgo = UsageStatsUtils.addDaysToDate(lastMidnight, -14);
		
		Date startDate = UsageStatsUtils.getDateParameter(request, "startDate", twoWeekAgo);
		Date endDate = UsageStatsUtils.getDateParameter(request, "endDate", lastMidnight);
		
		String numMonths = null;
		
		int numberOfMonths = 0;
		if (request.getParameter("numberOfMonths") != null) {
			numMonths = request.getParameter("numberOfMonths");
			numberOfMonths = UsageStatsUtils.getNumberOfMonths(numMonths);
			
		}
		
		String mnAge = "";
		String mxAge = "";
		
		mnAge = request.getParameter("minAge");
		mxAge = request.getParameter("maxAge");
		String gender = request.getParameter("gender");
		Date minAge = null;
		Date maxAge = null;
		
		if (mxAge != null && mxAge.length() != 0) {
			maxAge = UsageStatsUtils.calculateAge((Integer.parseInt(mxAge)),endDate);
		}
		if (mnAge != null && mnAge.length() != 0) {
			minAge = UsageStatsUtils.calculateAge((Integer.parseInt(mnAge)),endDate);
		}
		
		String[] criteriaWithParameters = pg.getAllCriteriaBasedOnProgram(programId);
		String programIdKey = "";
		String allCriteriaChecking = "";
		if (request.getParameter("checkType") != null) {
			programIdKey = request.getParameter("checkType");
			
		}
		
		int i = 0;
		ProgramPatientsController pc = new ProgramPatientsController();
		if (request.getParameter("checkAllCriteria") != null) {
			allCriteriaChecking = request.getParameter("checkAllCriteria");
			
		}
		if (allCriteriaChecking.equals("all")) {
			Map<String, List<Object[]>> allInOne = pc.getAllCriteria(programId, startDate, endDate, gender, minAge, maxAge,
			    numberOfMonths);
			for (String key : allInOne.keySet()) {
				
				listPatientHistory = allInOne.get(key);
				
				patientNumbers.put(key, listPatientHistory.size());
				model.put("patientNumbers", patientNumbers);
				request.getSession().setAttribute("patientSize_" + i, listPatientHistory);
				i++;
			}
			
		}
		
		for (String key : criteriaWithParameters) {
			
			if (programIdKey.equals(key)) {
				
				listPatientHistory = pc.checkTypeController(programIdKey, programId, startDate, endDate, gender, minAge,
				    maxAge, numberOfMonths);
				patientNumbers.put(key, listPatientHistory.size());
				
				model.put("patientNumbers", patientNumbers);
				request.getSession().setAttribute("patientSize_" + i, listPatientHistory);
				i++;
			}
			
		}
		
		//log.debug("++++++++++This " + programName + "is found");

		// calculate number of patient by year for selected indicators
		Map<String, Map<Integer, Integer>> listOfNumberOfPatientByYear = new TreeMap<String, Map<Integer,Integer>>();
	if (check!=null) {
		for (int k = 0; k <check.length; k++) {
			if (checkType != null && checkType.length != 0) {
				for (int j = 0; j < checkType.length; j++) {
					listOfNumberOfPatientByYear.put(checkType[j], ProgramPatientsController.getAllPatientSizeByYear(
					    checkType[j], programId, startDate, endDate, gender, minAge, maxAge, numberOfMonths));
				}
				model.put("listOfNumberOfPatientByYear", listOfNumberOfPatientByYear);
				
			}
	        
        }
		
	    
    }	
		
		
		//log.info(">>>checked SIze by yearCCCCCCCC>>>>>patient by year"+listOfNumberOfPatientByYear.size());
		model.put("programId", programId);
		model.put("programName", programName);
		model.put("criteriaSet", criteriaWithParameters);
		model.put("programIdKey", programIdKey);
		model.put("startDate", startDate);
		model.put("endDate", endDate);
		model.put("programIdKey", programIdKey);
		model.put("minAge", minAge);
		model.put("maxAge", maxAge);
		model.put("gender", gender);
		model.put("msgToDisplay", getMsgToDisplay());
		model.put("checkedValue",check);

		return new ModelAndView(getViewName(), model);
		
	}
}
	
