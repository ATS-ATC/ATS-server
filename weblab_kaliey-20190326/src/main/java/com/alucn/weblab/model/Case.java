package com.alucn.weblab.model;

public class Case {
	private String feature_number;
	private String release;
	private String lab_number;
	private String special_data;
	private String SelfTag;
	private String submit_date;
	private String status_owner;
	private String case_status;
	private String EPPSM;
	private String code_changed_spa;
	private String call_type;
	private String mate;
	private String case_level;
	private String case_name;
	private String customer;
	private String functionality;
	private String AMA;
	private String jira_id;
	private String porting_release;
	private String base_data;
	private String service;
	private String author;
	public String getFeature_number() {
		return feature_number;
	}
	public void setFeature_number(String feature_number) {
		this.feature_number = feature_number;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getLab_number() {
		return lab_number;
	}
	public void setLab_number(String lab_number) {
		this.lab_number = lab_number;
	}
	public String getSpecial_data() {
		return special_data;
	}
	public void setSpecial_data(String special_data) {
		this.special_data = special_data;
	}
	public String getSelfTag() {
		return SelfTag;
	}
	public void setSelfTag(String selfTag) {
		SelfTag = selfTag;
	}
	public String getSubmit_date() {
		return submit_date;
	}
	public void setSubmit_date(String submit_date) {
		this.submit_date = submit_date;
	}
	public String getStatus_owner() {
		return status_owner;
	}
	public void setStatus_owner(String status_owner) {
		this.status_owner = status_owner;
	}
	public String getCase_status() {
		return case_status;
	}
	public void setCase_status(String case_status) {
		this.case_status = case_status;
	}
	public String getEPPSM() {
		return EPPSM;
	}
	public void setEPPSM(String ePPSM) {
		EPPSM = ePPSM;
	}
	public String getCode_changed_spa() {
		return code_changed_spa;
	}
	public void setCode_changed_spa(String code_changed_spa) {
		this.code_changed_spa = code_changed_spa;
	}
	public String getCall_type() {
		return call_type;
	}
	public void setCall_type(String call_type) {
		this.call_type = call_type;
	}
	public String getMate() {
		return mate;
	}
	public void setMate(String mate) {
		this.mate = mate;
	}
	public String getCase_level() {
		return case_level;
	}
	public void setCase_level(String case_level) {
		this.case_level = case_level;
	}
	public String getCase_name() {
		return case_name;
	}
	public void setCase_name(String case_name) {
		this.case_name = case_name;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getFunctionality() {
		return functionality;
	}
	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}
	public String getAMA() {
		return AMA;
	}
	public void setAMA(String aMA) {
		AMA = aMA;
	}
	public String getJira_id() {
		return jira_id;
	}
	public void setJira_id(String jira_id) {
		this.jira_id = jira_id;
	}
	public String getPorting_release() {
		return porting_release;
	}
	public void setPorting_release(String porting_release) {
		this.porting_release = porting_release;
	}
	public String getBase_data() {
		return base_data;
	}
	public void setBase_data(String base_data) {
		this.base_data = base_data;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Case(String feature_number, String release, String lab_number, String special_data, String selfTag,
			String submit_date, String status_owner, String case_status, String ePPSM, String code_changed_spa,
			String call_type, String mate, String case_level, String case_name, String customer, String functionality,
			String aMA, String jira_id, String porting_release, String base_data, String service, String author) {
		super();
		this.feature_number = feature_number;
		this.release = release;
		this.lab_number = lab_number;
		this.special_data = special_data;
		SelfTag = selfTag;
		this.submit_date = submit_date;
		this.status_owner = status_owner;
		this.case_status = case_status;
		EPPSM = ePPSM;
		this.code_changed_spa = code_changed_spa;
		this.call_type = call_type;
		this.mate = mate;
		this.case_level = case_level;
		this.case_name = case_name;
		this.customer = customer;
		this.functionality = functionality;
		AMA = aMA;
		this.jira_id = jira_id;
		this.porting_release = porting_release;
		this.base_data = base_data;
		this.service = service;
		this.author = author;
	}
	public Case() {
		super();
	}
	@Override
	public String toString() {
		return "Case [feature_number=" + feature_number + ", release=" + release + ", lab_number=" + lab_number
				+ ", special_data=" + special_data + ", SelfTag=" + SelfTag + ", submit_date=" + submit_date
				+ ", status_owner=" + status_owner + ", case_status=" + case_status + ", EPPSM=" + EPPSM
				+ ", code_changed_spa=" + code_changed_spa + ", call_type=" + call_type + ", mate=" + mate
				+ ", case_level=" + case_level + ", case_name=" + case_name + ", customer=" + customer
				+ ", functionality=" + functionality + ", AMA=" + AMA + ", jira_id=" + jira_id + ", porting_release="
				+ porting_release + ", base_data=" + base_data + ", service=" + service + ", author=" + author + "]";
	}
	
}
