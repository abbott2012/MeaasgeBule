package com.guoji.mobile.cocobee.utils;

public class EncryptionUtils {

	public static String encoding(String pid, String current) {
		char[] p = pid.toCharArray();
		StringBuffer mendZero = new StringBuffer();
		for (int i = 0; i < 15 - p.length; i++) {
			mendZero.append("0");
		}
		pid = mendZero + pid;
		String str = current + pid;
		char[] s = str.toCharArray();
		StringBuffer out = new StringBuffer();
		String[] c = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		for (int i = 0; i < s.length; i++) {
			int location = Integer.parseInt(s[i] + "");
			out.append(c[location + 16]);
		}
		String dest = out.toString();
		String first = dest.substring(dest.length() - 10, dest.length());
		String second = dest.substring(0, dest.length() - 10);
		return first + second;

	}
	
	public static String decoding(String encry) {
		 String[] c = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		    String pid=encry.substring(encry.length()-5, encry.length())+encry.substring(0,10);
		    char[] p=pid.toCharArray();
		    int location=0;
            for(int i=0;i<p.length;i++){
            	if(p[i]=='Q'){
            		location++;
            		continue;
            	}else{
            		break;
            	}
            }     		    
	      pid=pid.substring(location, pid.length());
	      char[] dest=pid.toCharArray();
	      StringBuffer out=new StringBuffer();
          for(int i=0;i<dest.length;i++){
        	  for(int j=0;j<c.length;j++){
        		  if((dest[i]+"").equals(c[j])){
                        out.append((j-16)+"");
                        break;
        		  }
        	  }
          }
	      return out.toString();
	}
}
