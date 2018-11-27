package brownshome.apss.scraper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Material {
	private final static Pattern tableElementSearch = Pattern.compile("<tr><td width='18%'><b>([^<>]*)</b></td><td>([^<]*)</td></tr>");
	
	private final String name;
	private Map<String, String> data = null;
	
	public Material(String name) {
		this.name = name;
	}
	
	public Map<String, String> loadData(DatabaseSiteDownloader downloader) throws IOException {
		if(data != null)
			return data;
		
		String page = downloader.getMaterialData(name.replaceAll(" ", "%20"));
		data = new HashMap<>();
		
		tableElementSearch.matcher(page).results().forEach(result -> {
			data.put(result.group(1), result.group(2));
		});
		
		return data;
	}

	public String getName() {
		return name;
	}
}
