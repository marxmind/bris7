package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.List;

import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;

import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="organigramView", eager=true)
@ViewScoped
public class OrganizationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 545685895654641L;
	
	private OrganigramNode rootNode;
    private OrganigramNode selection;
 
    private boolean zoom = true;
    private String style = "width: 100%";
    private int leafNodeConnectorHeight = 20;
    private boolean autoScrollToSelection = true;
 
    private String employeeName;
 
    @PostConstruct
    public void init() {
    	
    	Login in = Login.getUserLogin();
		boolean isOk = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOk = true;
		}else{
			if(Features.isEnabled(Feature.ORGANIZATION)){
				isOk = true;
			}
		}
    	
		if(isOk){
	    	String barangay = ReadConfig.value(Bris.BARANGAY_NAME);
	    	
	        selection = new DefaultOrganigramNode(null, "Mark Italia", null);
	 
	        rootNode = new DefaultOrganigramNode("root", barangay, null);
	        rootNode.setCollapsible(false);
	        rootNode.setDroppable(false);
	        
	        List<Employee> emps = Employee.retrieve(" AND emp.isactiveemp=1 AND emp.isresigned=0", new String[0]);
	        
	        String captain = "";
	        for(Employee e : emps){
	        	if(Positions.CAPTAIN.getId()==e.getPosition().getId()){
	        		captain = "HON. "+ e.getFirstName().toUpperCase() + " " + e.getMiddleName().substring(0, 1).toUpperCase()+". " + e.getLastName().toUpperCase();
	        	}
	        }
	        
	        OrganigramNode executive = addDivision(rootNode, captain );
	 
	        OrganigramNode teamExe = addDivision(executive, Positions.CAPTAIN.getName(),"Barangay Staff");
	        OrganigramNode admin = addDivision(teamExe, "Administrative");
	        
	        for(Employee e : emps){
	        	if(e.getIsOfficial()==0){
	        		addDivision(admin,e.getPosition().getName(), e.getFirstName().toUpperCase() + " " + e.getMiddleName().substring(0, 1).toUpperCase()+". " + e.getLastName().toUpperCase());
	        	}
	        }
	        
	        OrganigramNode officials = addDivision(teamExe, "Officials");
	        for(Employee e : emps){
	        	if(e.getIsOfficial()==1 && Positions.CAPTAIN.getId()!=e.getPosition().getId()){
	        		addDivision(officials, e.getCommittee().getName(), "HON. "+e.getFirstName().toUpperCase() + " " + e.getMiddleName().substring(0, 1).toUpperCase()+". " + e.getLastName().toUpperCase());
	        	}
	        }
		}
    }
 
    protected OrganigramNode addDivision(OrganigramNode parent, String name, String... employees) {
        OrganigramNode divisionNode = new DefaultOrganigramNode("division", name, parent);
        divisionNode.setDroppable(true);
        divisionNode.setDraggable(true);
        divisionNode.setSelectable(true);
 
        if (employees != null) {
            for (String employee : employees) {
                OrganigramNode employeeNode = new DefaultOrganigramNode("employee", employee, divisionNode);
                employeeNode.setDraggable(true);
                employeeNode.setSelectable(true);
            }
        }
 
        return divisionNode;
    }
 
    public void nodeDragDropListener(OrganigramNodeDragDropEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSummary(event.getOrganigramNode().getData() + "' moved from " + event.getSourceOrganigramNode().getData() + " to '" + event.getTargetOrganigramNode().getData() + "'");
        message.setSeverity(FacesMessage.SEVERITY_INFO);
 
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void nodeSelectListener(OrganigramNodeSelectEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSummary(event.getOrganigramNode().getData() + "' selected.");
        message.setSeverity(FacesMessage.SEVERITY_INFO);
 
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSummary(event.getOrganigramNode().getData() + "' collapsed.");
        message.setSeverity(FacesMessage.SEVERITY_INFO);
 
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void nodeExpandListener(OrganigramNodeExpandEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSummary(event.getOrganigramNode().getData() + "' expanded.");
        message.setSeverity(FacesMessage.SEVERITY_INFO);
 
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void removeDivision() {
        // re-evaluate selection - might be a differenct object instance if viewstate serialization is enabled
        OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
        setMessage(currentSelection.getData() + " removing division.", FacesMessage.SEVERITY_INFO);
    }
 
    public void removeEmployee() {
        // re-evaluate selection - might be a differenct object instance if viewstate serialization is enabled
        OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
        currentSelection.getParent().getChildren().remove(currentSelection);
    }
 
    public void addEmployee() {
        // re-evaluate selection - might be a differenct object instance if viewstate serialization is enabled
        OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
 
        OrganigramNode employee = new DefaultOrganigramNode("employee", employeeName, currentSelection);
        employee.setDraggable(true);
        employee.setSelectable(true);
    }
 
    private void setMessage(String msg, FacesMessage.Severity severity) {
        FacesMessage message = new FacesMessage();
        message.setSummary(msg);
        message.setSeverity(severity);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public OrganigramNode getRootNode() {
        return rootNode;
    }
 
    public void setRootNode(OrganigramNode rootNode) {
        this.rootNode = rootNode;
    }
 
    public OrganigramNode getSelection() {
        return selection;
    }
 
    public void setSelection(OrganigramNode selection) {
        this.selection = selection;
    }
 
    public boolean isZoom() {
        return zoom;
    }
 
    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }
 
    public String getEmployeeName() {
        return employeeName;
    }
 
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
 
    public String getStyle() {
        return style;
    }
 
    public void setStyle(String style) {
        this.style = style;
    }
 
    public int getLeafNodeConnectorHeight() {
        return leafNodeConnectorHeight;
    }
 
    public void setLeafNodeConnectorHeight(int leafNodeConnectorHeight) {
        this.leafNodeConnectorHeight = leafNodeConnectorHeight;
    }
 
    public boolean isAutoScrollToSelection() {
        return autoScrollToSelection;
    }
 
    public void setAutoScrollToSelection(boolean autoScrollToSelection) {
        this.autoScrollToSelection = autoScrollToSelection;
    }
}