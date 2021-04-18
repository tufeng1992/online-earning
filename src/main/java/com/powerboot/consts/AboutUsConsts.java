package com.powerboot.consts;

import com.powerboot.response.AbountUsContentResponse;
import com.powerboot.response.AboutUsResponse;

import java.util.ArrayList;
import java.util.List;

public class AboutUsConsts {

    public static AboutUsResponse aboutUsResponse = null;

    public static AboutUsResponse init(String firstTitle, String secondTitle, String a1Tip, String c1,
                            String a2Tip, String c2, String a3Tip, String c3) {
        if (null == aboutUsResponse) {
            aboutUsResponse = new AboutUsResponse();
        }
        aboutUsResponse.setFirstTitle(firstTitle);
        aboutUsResponse.setSecondTitle(secondTitle);

        List<AbountUsContentResponse> paragraphList = new ArrayList<>();
        AbountUsContentResponse a1 = new AbountUsContentResponse();
        a1.setTip(a1Tip);
        List<String> content1 = new ArrayList<>();
        content1.add(c1);
        a1.setContentList(content1);
        paragraphList.add(a1);

        AbountUsContentResponse a2 = new AbountUsContentResponse();
        a2.setTip(a2Tip);
        List<String> content2 = new ArrayList<>();
        content2.add(c2);
        a2.setContentList(content2);
        paragraphList.add(a2);

        AbountUsContentResponse a3 = new AbountUsContentResponse();
        a3.setTip(a3Tip);
        List<String> content3 = new ArrayList<>();
        content3.add(c3);
        a3.setContentList(content3);
        paragraphList.add(a3);
        aboutUsResponse.setParagraphList(paragraphList);
        return aboutUsResponse;
    }

}
