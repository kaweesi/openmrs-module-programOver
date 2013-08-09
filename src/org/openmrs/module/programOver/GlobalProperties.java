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
package org.openmrs.module.programOver;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openmrs.api.context.Context;


/**
 *
 */
public class GlobalProperties {
		
	
	public static String gpGetListOfARVsDrugs(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfARVsDrugs");
		 }
	
	public static List<Integer> gpGetListOfARVsDrugsAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfARVsDrugs(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	public static String gpGetListOfFirstLineDrugs(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfFirstLineDrugs");
		 }
	
	public static List<Integer> gpGetListOfFirstLineDrugsAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfFirstLineDrugs(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	public static String gpGetListOfSecondLineDrugs(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfSecondLineDrugs");
		 }
	
	public static List<Integer> gpGetListOfSecondLineDrugsAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfSecondLineDrugs(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	

	public static String gpGetListOfProphylaxisDrugs(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfProphylaxisDrugs");
		 }
	
	public static List<Integer> gpGetListOfProphylaxisDrugsAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfProphylaxisDrugs(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	
	public static String gpGetListOfTBDrugs(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfFirstLineDrugs");
		 }
	
	public static List<Integer> gpGetListOfTBDrugsAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfTBDrugs(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	
	public static String gpGetListOfAnswersToResultOfHIVTest(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfFirstLineDrugs");
		 }
	
	public static List<Integer> gpGetListOfAnswersToResultOfHIVTestAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfAnswersToResultOfHIVTest(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	public static String gpGetListOfAnswersToRapidPlasminReagent(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.listOfFirstLineDrugs");
		 }
	
	public static List<Integer> gpGetListOfAnswersToRapidPlasminReagentAsIntegers(){
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  StringTokenizer tokenizer = new StringTokenizer(gpGetListOfAnswersToRapidPlasminReagent(),",");
		  while (tokenizer.hasMoreTokens()) {
		   Integer id = Integer.parseInt(tokenizer.nextToken());
		         list.add(id);
		        }
		  return list;
		 }
	
	
	public static String gpGetreactiveAsfAnswerToRapidPlasminReagentConceptIdConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.reactiveAsfAnswerToRapidPlasminReagentConceptId");
		 }
	
	public static String gpGetExitFromCareConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.exitFromCareConceptId");
		 }
	
	public static String gpGetExitFromCareDiedConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.exitFromCareDiedConceptId");
		 }
	
	public static String gpGetExitFromTransferredOutConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.exitFromCareTransferredOutConceptId");
		 }
	
	public static String gpGetTransferredInConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.transferredInConceptId");
		 }
	
	public static String gpGetyesAsAnswerToTransferredInConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.yesAsAnswerToTransferredInConceptId");
		 }
	
	public static String gpGetResultForHIVTestConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.resultForHIVTestConceptId");
		 }
	
	public static String gpGetPositiveAsResultToHIVTestConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.positiveConceptId");
		 }
	
	public static String gpGetNegativeAsResultToHIVTestConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.negativeConceptId");
		 }
	
	public static String gpGetIndeterminateAsResultToHIVTestConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.indeterminateConceptId");
		 }
	
	public static String gpGetDateResultOfHIVTestReceivedConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.dateResultOfHIVTestReceived");
		 }
	
	public static String gpGetCD4CountConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.cd4CountConceptId");
		 }
	
	public static String gpGetTBScreeningConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.tbScreeningConceptId");
		 }
	
	public static String gpGetWhoStageConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageConceptId");
		 }
	
	public static String gpGetCurrentWhoHIVStageConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.currentWhoHIVStageConceptId");
		 }
	
	public static String gpGetWhoStageAtTransferInConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageAtTransferInConceptId");
		 }
	
	public static String gpGetWhoStageOneAdultConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageOneAdultConceptId");
		 }
	
	public static String gpGetWhoStageTwoAdultConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageTwoAdultConceptId");
		 }
	
	public static String gpGetWhoStageThreeAdultConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageThreeAdultConceptId");
		 }
	
	public static String gpGetWhoStageFourAdultConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageFourAdultConceptId");
		 }
	
	public static String gpGetWhoStageOnePedsConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageOnePedsConceptId");
		 }
	
	public static String gpGetWhoStageTwoPedsConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageTwoPedsConceptId");
		 }
	public static String gpGetWhoStageThreePedsConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageThreePedsConceptId");
		 }
	public static String gpGetWhoStageFourPedsConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.whoStageFourPedsConceptId");
		 }
	public static String gpGetUnknownStageConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.unknownWhoStageConceptId");
		 }
	
	
	public static String gpGetHIVProgramId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.hivProgramId");
		 }
	
	public static String gpGetPMTCTProgramId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.pmtctProgramId");
		 }
	
	public static String gpGetPCREncounterId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.pcrEncounterId");
		 }
	
	public static String gpGetSerologyAt9MonthId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.serologyTestAt9MonthId");
		 }
	
	public static String gpGetSerologyAt18MonthId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.serologyTestAt18MonthId");
		 }
	
	public static String gpGetCPNEncounterId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.cpnEncounterId");
		 }
	
	public static String gpGetMaternityEncounterId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.maternityEncounterId");
		 }
	
	public static String gpGetMotherFollowUpEncounterId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.motherFollowUpEncounterId");
		 }
	
	public static String gpGetRapidPlasminReagentConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.rapidPlasminReagentConceptId");
		 }
	
	public static String gpGetReturnVisitDateConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.returnVisitDateConceptId");
		 }
	
	public static String gpGetBreastedExclusivelyConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.breastedExclusivelyConceptId");
		 }
	
	public static String gpGetUsingFormulaConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.usingFormulaConceptId");
		 }
	
	public static String gpGetTestingStatusOfPartnerConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.testingStatusOfPartnerConceptId");
		 }
	
	public static String gpGetEstimatedDateOfConfinementConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.estimatedDateOfConfinement");
		 }
	
	public static String gpGetHIVTestInDeliveryRoomConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.HIVTestInDeliveryRoom");
		 }
	
	public static String gpGetReasonForVisitConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.reasonForVisitConceptId");
		 }
	
	public static String gpGetPharmacyVisitAsAnswerToReasonForVisitConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.pharmacyVisitAsAnswerToReasonForVisitConceptId");
		 }
	
	public static String gpGetOutPatientConsultationAsAnswerToReasonForVisitConceptId(){
		  return Context.getAdministrationService().getGlobalProperty("programOver.outPatientConsultationAsAnswerToReasonForVisitConceptId");
		 }
}
