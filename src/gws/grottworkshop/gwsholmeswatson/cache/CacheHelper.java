package gws.grottworkshop.gwsholmeswatson.cache;



public class CacheHelper {

	public static String getFileNameFromUrl(String url) {
        // replace all special URI characters with a single + symbol
        return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
    }

  
}
