package com.jonathonfly.myfirstapp.webservice.site.parser;

import java.io.InputStream;
import java.util.List;

import com.jonathonfly.myfirstapp.webservice.site.model.SiteInfoModel;

public interface SiteParser {
	 /** 
     * ���������� �õ�SiteInfoModel���󼯺� 
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<SiteInfoModel> parse(InputStream is) throws Exception;  
      
    /** 
     * ���л�SiteInfoModel���󼯺� �õ�XML��ʽ���ַ� 
     * @param siteList 
     * @return 
     * @throws Exception 
     */  
    public String serialize(List<SiteInfoModel> siteList) throws Exception;
}
