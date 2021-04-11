package com.powerboot.consts;

import com.powerboot.response.AbountUsContentResponse;
import com.powerboot.response.AboutUsResponse;

import java.util.ArrayList;
import java.util.List;

public class AboutUsConsts {

    public static AboutUsResponse aboutUsResponse = new AboutUsResponse();

    static {
        aboutUsResponse.setFirstTitle("1111");
        aboutUsResponse.setSecondTitle("(22 33 44)");

        List<AbountUsContentResponse> paragraphList = new ArrayList<>();
        AbountUsContentResponse a1 = new AbountUsContentResponse();
        a1.setTip("What is?");
        List<String> content1 = new ArrayList<>();
        content1.add("dd.dd (English: dd.ff, ff.) is a ");
        a1.setContentList(content1);
        paragraphList.add(a1);

        AbountUsContentResponse a2 = new AbountUsContentResponse();
        a2.setTip("What is?");
        List<String> content2 = new ArrayList<>();
        content2.add("");
        a2.setContentList(content2);
        paragraphList.add(a2);

        AbountUsContentResponse a3 = new AbountUsContentResponse();
        a3.setTip("Is ?");
        List<String> content3 = new ArrayList<>();
        content3.add("The bad.");
        a3.setContentList(content3);
        paragraphList.add(a3);
        aboutUsResponse.setParagraphList(paragraphList);

    }

}
