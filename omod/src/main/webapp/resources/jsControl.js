function displayUnit(option){
	
	

	  if ((option == "patientsOnARVWithoutVisitingPharmacyForXDays" )||(option == "patientsWithoutCD4CountsForXDays" ))  {
			document.getElementById('numbers').style.display = '';
			document.getElementById('inputNumbers').style.display = '';
			document.getElementById('From').style.display = 'none';
			document.getElementById('To').style.display = 'none';
			document.getElementById('StartDate').style.display = 'none';
			document.getElementById('EndDate').style.display = 'none';
			document.getElementById('Graphic').style.display = 'none';
			
		}
	   else{
			document.getElementById('numbers').style.display = 'none';
			document.getElementById('inputNumbers').style.display = 'none';
			document.getElementById('From').style.display = '';
			document.getElementById('To').style.display = '';
			document.getElementById('StartDate').style.display = '';
			document.getElementById('EndDate').style.display = '';
			document.getElementById('Graphic').style.display = '';
			
		}
	  if ((option == "PMTCTpatientbetweenthereportingperiod" )
			  ||(option == "CPNPatientsBetweenReportingPeriod" )||(option == "PatientsWhoGaveBirthBetweenTheReportingPeriod" )||(option == "PatientsExpectedInPMTCTBetweenTheReportingPeriod" ))  {
		  document.getElementById('gender').style.display = 'none';
			document.getElementById('genderInput').style.display = 'none';
			
		}
	  
	  if ((option == "newPatientsOnARVs" )||(option == "newPatientsOnProphylaxis" )||(option == "transferredPatientsBetweenTwoDates" )||(option == "diedPatientsBetweenTwoDates" )
			  ||(option == "consultedPatientsBetweenTwoDates" )||(option == "patientsWhocameForpharmacyVisitBetweenTwoDates" )||(option == "patientsEnrolledInAProgram" )||(option == "patientsEligibleOnARVsButNotYetStarted" ))  {
		  document.getElementById('From').style.display = '';	 
		  document.getElementById('StartDate').style.display = '';
		  document.getElementById('To').style.display = '';
		  document.getElementById('EndDate').style.display = '';
			
		}
	  if((option == "PatientCurrentlyinpmtct")||(option == "PositivePatientsInPMTCT" )||(option == "NegativePatientsInPMTCT" ))
	  {
		  document.getElementById('gender').style.display = 'none';
		  document.getElementById('genderInput').style.display = 'none';
		  document.getElementById('Graphic').style.display = 'none';
		  document.getElementById('From').style.display = 'none';
		  document.getElementById('StartDate').style.display = 'none';
		  
	  }
	  if((option == "PMTCTpatientbetweenthereportingperiod")||(option == "CPNPatientsBetweenReportingPeriod" )||(option == "PatientsWhoGaveBirthBetweenTheReportingPeriod" )
			  ||(option == "PatientsExpectedInPMTCTBetweenTheReportingPeriod" ))
	  {
		  document.getElementById('gender').style.display = 'none';
		  document.getElementById('genderInput').style.display = 'none';
		  document.getElementById('Graphic').style.display = 'none';
		  document.getElementById('From').style.display = '';
		  document.getElementById('StartDate').style.display = '';
		  
	  }
		  
	  
	  
	  
  }






//function printSpecial() {
//	if (document.getElementById != null) {
//		var html = '<HTML>\n<HEAD>\n';
//		if (document.getElementsByTagName != null) {
//			var headTags = document.getElementsByTagName("head");
//			if (headTags.length > 0)
//				html += headTags[0].innerHTML;
//		}
//
//		html += '\n</HE>\n<BODY>\n';
//
//		var printReadyElem = document.getElementById("placeholder");
//
//		if (printReadyElem != null) {
//			html += printReadyElem.innerHTML;
//		} else {
//			alert("Could not find the printReady function");
//			return;
//		}
//
//		html += '\n</BO>\n</HT>';
//
//		var printWin = window.open("", "printSpecial");
//		printWin.document.open();
//		printWin.document.write(html);
//		printWin.document.close();
//		if (gAutoPrint)
//			printWin.print();
//	} else {
//		alert("The print ready feature is only available if you are using an browser. Please update your browswer.");
//	}
//}



