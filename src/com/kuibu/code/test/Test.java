package com.kuibu.code.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args){
		Pattern p = Pattern.compile("((.)\\2*)");
		String s="122aa,,,,    s09";
		Matcher m=p.matcher(s);
		while(m.find()){
			System.out.println("{"+m.group()+"}");
		}		
	}
}
