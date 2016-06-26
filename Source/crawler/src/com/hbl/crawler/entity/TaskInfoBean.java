package com.hbl.crawler.entity;


public class TaskInfoBean{
	
	/**
	 * 目标站点或地址
	 */
	private String destUrl;
	
	/**
	 * 抓取站点编码格式
	 */
	private String charset;
	
	/**
	 * 目标地址正则匹配规则
	 */
	private String targetUrlRegEx; 
	
	/**
	 * 目标内容正则匹配规则
	 */
	private String contextRegEx;
	
	public String getDestUrl() {
		return destUrl;
	}
	
	public void setDestUrl(String destUrl) {
		this.destUrl = destUrl;
	}
	
	public String getCharset() {
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getTargetUrlRegEx() {
		return targetUrlRegEx;
	}
	
	public void setTargetUrlRegEx(String targetUrlRegEx) {
		this.targetUrlRegEx = targetUrlRegEx;
	}
	
	public String getContextRegEx() {
		return contextRegEx;
	}
	
	public void setContextRegEx(String contextRegEx) {
		this.contextRegEx = contextRegEx;
	}
}
