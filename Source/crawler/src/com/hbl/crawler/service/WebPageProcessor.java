package com.hbl.crawler.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import com.hbl.crawler.common.FileUtil;
import com.hbl.crawler.common.StringUtil;
import com.hbl.crawler.entity.TaskInfoBean;

public class WebPageProcessor implements PageProcessor {

	private Site site;
	private TaskInfoBean task;
	public Logger logger = Logger.getLogger(WebPageProcessor.class);
	
	public WebPageProcessor(TaskInfoBean task){
		this.task = task;
		this.site = Site.me().setCharset(task.getCharset()).setRetryTimes(3).setSleepTime(1000);
	}

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		String htmlStr = page.getHtml().toString();
		htmlStr.replaceAll("<.*?>", " ");
		Pattern p = Pattern.compile(task.getContextRegEx(), Pattern.DOTALL);
		Matcher m = p.matcher(htmlStr);
		if(!m.matches()){
			page.setSkip(true);
		}else{
			String context = simpleProcessHtml(htmlStr); 
			page.putField("name", page.getHtml().xpath("body").toString());
			if (page.getResultItems().get("name") == null) {
				// skip this page 
				page.setSkip(true);
			}
		}
		page.addTargetRequests(page.getHtml().links().regex(task.getTargetUrlRegEx()).all());
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}
	
	public TaskInfoBean getTask() {
		return task;
	}

	public void setTask(TaskInfoBean task) {
		this.task = task;
	}
	
	/**
	 * 简单初步抽取html有用信息
	 * @param htmlStr
	 * @return
	 */
	private String simpleProcessHtml(String htmlStr){
		String desStr = htmlStr;
		desStr = desStr.replaceAll("<script(?:[^<]++|<(?!/script>))*+</script>", "<script>"); 
		while(desStr.contains("</script>")){ 
			desStr = desStr.replaceAll("<script(?:[^<]++|<(?!/script>))*+</script>", "<script>"); 
		}
		desStr = StringUtil.stringReplace(desStr, "&nbsp;", " ");
		desStr = StringUtil.stringReplace(desStr, "&amp;", "&");
		desStr = StringUtil.stringReplace(desStr, "&lt;", "<");
		desStr = StringUtil.stringReplace(desStr, "&gt;", ">");
		desStr = StringUtil.stringReplace(desStr, "\\s\\s\\s*", " ");
		desStr = desStr.replaceAll("<.*?>", " ");
		return desStr;
	}
	
	/**
	 * 抽取法律类字段信息
	 * @param htmlStr
	 * @param caseRuleList
	 * @return
	 */
	private List<Map<String, String>> getLawNoticeList(String htmlStr, List<Map<String, String>> caseRuleList){
		List<Map<String, String>> caseList = new ArrayList<Map<String, String>>();
		if(caseRuleList == null || caseRuleList.size() <1){
			return caseList;
		}
		for (Map<String, String> caseRuleMap:caseRuleList) {
			List<Map<String, String>> columnCaseList = getColumnCaseList(htmlStr, caseRuleMap);
			if(columnCaseList != null && columnCaseList.size() > 0){
				caseList.addAll(columnCaseList);
			}
		}
		return caseList;
	}
	
	/**
	 * 抽取法律类字段信息
	 * @param htmlStr
	 * @param caseRuleList
	 * @return
	 */
	private List<Map<String, String>> getColumnCaseList(String htmlStr, Map<String, String> caseRuleMap){
		List<Map<String, String>> caseList = new ArrayList<Map<String, String>>();
		if(caseRuleMap == null || caseRuleMap.size() <1){
			return caseList;
		}
		// 判断第一个开始属性，作为多个案件之间的间隔
		for (Map.Entry<String, String> entry : caseRuleMap.entrySet()) {
            
        }
		return caseList;
	}

	public static void main(String[] args) {
		TaskInfoBean task = new TaskInfoBean();
		task.setDestUrl("http://court.hefei.gov.cn/contents/17/148.html");
		task.setCharset("utf-8");
		task.setTargetUrlRegEx("http://court\\.hefei\\.gov\\.cn/contents/17.*");
		task.setContextRegEx(".*(开庭公告|送达公告|减刑假释公告|执行公告|立案公告|听证公告|破产文书|清算公告|拍卖变卖公告).*");
		String outputPath = "F:/workspace/output";
		WebPageProcessor webPageProcessor = new WebPageProcessor(task);
		FileUtil.createDir(outputPath);
		Spider.create(webPageProcessor)
				.addUrl(task.getDestUrl())
				.addPipeline(new FilePipeline(outputPath))
				.thread(10).run();
	}
}
