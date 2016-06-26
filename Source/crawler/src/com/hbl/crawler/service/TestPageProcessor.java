package com.hbl.crawler.service;

import java.io.File;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class TestPageProcessor implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	private String destUrl = "http://court.hefei.gov.cn"; 
	private String destDir = "J:/source/magic-output";
	public Logger logger = Logger.getLogger(TestPageProcessor.class);
	
	public String getDestUrl() {
		return destUrl;
	}

	public void setDestUrl(String url) {
		destUrl = url;
	}

	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String dir) {
		destDir = dir;
	}
public void createResultDir() {
		
		File file = new File(destDir);
		if (!file.exists() && !file.isDirectory()) {
			if (file.mkdir() == false) {
				logger.error("创建路径失败" + "dir-name = "
						+ destDir);
			}
		}
		String[] strs = destUrl.split("//");
		if (strs == null || strs.length < 2) {
			logger.error("地址有误" + "url = " + destUrl);
		}
		String resultDir = destDir + "/" + strs[1];
		file = new File(resultDir);

		if (!file.exists() && !file.isDirectory()) {
			if (file.mkdir() == false) {
				logger.error("" + "dir-name = "
						+ resultDir);
			}
		}
	}
	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		String text = page.getHtml().xpath("title").toString();
		if (!(text == null | text == "")) {
			int k = -1;
			k = text.indexOf("合肥");
			if (k < 0) {
				page.setSkip(true);
			} else {
				System.out.println(text);
				page.putField("author",
						page.getUrl().regex("http://court\\.hefei\\.gov\\.cn.*")
								.toString());
				page.putField("name", page.getHtml().xpath("").toString());
				if (page.getResultItems().get("name") == null) {
					// skip this page
					page.setSkip(true);
				}
				page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
			}
		}
		page.addTargetRequests(page.getHtml().links()
				.regex("http://court\\.hefei\\.gov\\.cn.*").all());
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}
	
	public static void main(String[] args) {
		String dir = "J:/source/magic-output";
		String url = "http://court.hefei.gov.cn";
		TestPageProcessor webPageProcessor = new TestPageProcessor();

		webPageProcessor.setDestDir(dir);
		webPageProcessor.setDestUrl(url);
		webPageProcessor.createResultDir();
		Spider.create(webPageProcessor)
				.addUrl(webPageProcessor.getDestUrl())
				.addPipeline(new FilePipeline(webPageProcessor.getDestDir()))
				.thread(10).run();
	}

}
