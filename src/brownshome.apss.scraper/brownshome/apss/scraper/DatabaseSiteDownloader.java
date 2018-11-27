package brownshome.apss.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** This class */
public class DatabaseSiteDownloader {
	private final URL baseUrl;
	
	public DatabaseSiteDownloader(URL baseUrl) {
		this.baseUrl = baseUrl;
	}

	public List<Material> listMaterials() throws IOException {
		URL sidebarUrl = new URL(baseUrl, "spacemat/treeview.php");
		
		HttpURLConnection connection = (HttpURLConnection) sidebarUrl.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
		connection.setInstanceFollowRedirects(true);
		
		connection.connect();
		
		String data;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			data = reader.lines().collect(Collectors.joining("\n"));
		}
		
		Pattern regex = Pattern.compile("<a target=\"materials\" href=\"datasearch.php\\?name=([^\"]*)\">");
		Matcher matcher = regex.matcher(data);
		
		return matcher.results()
				.map(result -> result.group(1))
				.map(Material::new)
				.collect(Collectors.toList());
	}

	public String getMaterialData(String name) throws IOException {
		URL pageUrl = new URL(baseUrl, "spacemat/datasearch.php?name=" + name);
		
		HttpURLConnection connection = (HttpURLConnection) pageUrl.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
		connection.setInstanceFollowRedirects(true);
		
		connection.connect();
		
		String data;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			data = reader.lines().collect(Collectors.joining("\n"));
		}

		System.err.println("Downloaded " + name);
		
		return data;
	}
}
