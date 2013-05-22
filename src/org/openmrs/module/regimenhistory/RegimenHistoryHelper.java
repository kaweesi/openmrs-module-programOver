package org.openmrs.module.regimenhistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;

public class RegimenHistoryHelper {
     
    private List<RegimenComponent> components;
    
    public RegimenHistoryHelper() {
        components = new ArrayList<RegimenComponent>();
    }
    
    public void addComponent(Concept drug, Date startDate, Date endDate) {
        components.add(new RegimenComponent(drug, startDate, endDate));
    }
    
    public void addComponent(DrugOrder order) {
        addComponent(order.getConcept(), order.getStartDate(), order.isDiscontinued(new Date()) ? order.getDiscontinuedDate() : order.getAutoExpireDate());
    }
    
    public SortedMap<Date, Set<Concept>> getAsRegimenList() {
        List<RegimenEvent> events = new ArrayList<RegimenEvent>();
        for (RegimenComponent component : components) {
            if (component.getStartDate() == null)
                throw new RuntimeException("found null startDate");
            events.add(RegimenEvent.startEvent(component.getStartDate(), component.getGeneric()));
            if (component.getStopDate() != null)
                events.add(RegimenEvent.stopEvent(component.getStopDate(), component.getGeneric()));
        }
        Collections.sort(events, new Comparator<RegimenEvent>() {
            public int compare(RegimenEvent left, RegimenEvent right) {
                return left.getDate().compareTo(right.getDate());
            }
        });

        SortedMap<Date, Set<Concept>> ret = new TreeMap<Date, Set<Concept>>();
        
        Set<Concept> runningRegimen = new HashSet<Concept>();
        Date runningDate = null;
        for (RegimenEvent e : events) {
            if (runningDate != null && !runningDate.equals(e.getDate()))
                ret.put(runningDate, new HashSet<Concept>(runningRegimen));

            if (e.getDrugStarted() != null)
                runningRegimen.add(e.getDrugStarted());
            if (e.getDrugStopped() != null)
                runningRegimen.remove(e.getDrugStopped());
            runningDate = e.getDate();
        }
        if (runningDate != null)
            ret.put(runningDate, new HashSet<Concept>(runningRegimen));

        return ret;
    }

}
