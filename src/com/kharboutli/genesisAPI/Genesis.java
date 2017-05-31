package com.kharboutli.genesisAPI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/*
 * Class to represent a full "Genesis object". This class will contain
 * the the main HTML for a student that has been scraped from the
 * Genesis Parent Portal. In order to construct an object of this type,
 * the constructor will make calls to authenticate the user. This class
 * will only have getters to return the data.
 */
public class Genesis {
	
	private HtmlPage homePage;
	private HtmlPage gradebookPage;
	private String homePageContent;
	private String gradebookPageContent;
	
	public Genesis(String email, String password) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		WebClient webCli = new WebClient();
		webCli.getOptions().setCssEnabled(false);
		webCli.getOptions().setAppletEnabled(false);
		webCli.getOptions().setJavaScriptEnabled(false);

		HtmlPage authPage = webCli.getPage("http://parents.westfieldnjk12.org/genesis/parents?gohome=true");
		HtmlForm logonForm = authPage.getFormByName("logon");

		HtmlEmailInput emailInput = logonForm.getInputByName("j_username");
		HtmlPasswordInput passwordInput = logonForm.getInputByName("j_password");
		HtmlSubmitInput submitButton = logonForm.getInputByValue("Login");

		emailInput.setText(email);
		passwordInput.setText(password);
		homePage = submitButton.click();

		String gradebookURL = homePage.getUrl().toString().replace("tab2=studentsummary",
				"tab2=gradebook&tab3=weeklysummary");
		gradebookPage = webCli.getPage(gradebookURL);
		
		homePageContent = homePage.getWebResponse().getContentAsString();
		gradebookPageContent = gradebookPage.getWebResponse().getContentAsString();
		
		webCli.close();
	}
	
	//TODO: parsing...
	public String findName()
	{
		Element body = Jsoup.parse(homePageContent).body();
		return body.select("span[style*=font-weight]").select("span[style*=color]").first().text();
	}
	
	public int findGrade()
	{
		Element body = Jsoup.parse(homePageContent).body();
		String strGrade = body.select("td[style*=font-size]").select("td[rowspan]").last().text();
		return Integer.parseInt(strGrade.replace(" ", ""));
	}
	
	public String findStudentID()
	{
		Element body = Jsoup.parse(homePageContent).body();
		return body.select("td[style*=font-size]").select("td[style*=white-space]").select("td[text-transform]").first().text();
	}
	
	public Course[] generateCourses()
	{
		ArrayList arr = new ArrayList<Course>();
		Element body = Jsoup.parse(gradebookPageContent).body();
		Elements els = body.select("table[class=list]").select("table[border*=0]").first().getElementsByTag("tr");
		for(int c = 0; c <= els.indexOf(els.last()); c++)
		{
			//TODO: loop through each, create course object for each tr
			
		}
		return (Course[]) arr.toArray();
	}
}
