package brownshome.apss.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
	private static final URL DEFAULT_URL;
	
	static {
		try {
			DEFAULT_URL = new URL("http", "www.spacematdb.com", "/spacemat");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 1. Reads in data from the website front page to read out the list of materials.
	 * 2. Browse to the site for each of those materials and scrape out the requested terms.
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DatabaseSiteDownloader downloader = new DatabaseSiteDownloader(DEFAULT_URL);
		
		List<Material> materials = downloader.listMaterials();
		
		materials.parallelStream().forEach(m -> {
			try {
				m.loadData(downloader);
			} catch (IOException e) {
				System.out.println("Error downloading " + m.getName());
			}
		});
		
		//Find the set of headers, then generate the CSV file
		Set<String> headers = new LinkedHashSet<>();
		
		for(Material m : materials) {
			headers.addAll(m.loadData(downloader).keySet());
		}
		
		//Generate the CSV file
		StringBuilder csv = new StringBuilder();
		
		csv.append("\"Name\",");
		
		csv.append(headers.stream()
				.map(Main::escapeToCSV)
				.collect(Collectors.joining(",")))
		.append("\n");
		
		for(Material m : materials) {
			csv.append(escapeToCSV(m.getName()))
			.append(",");
			
			csv.append(
					headers.stream()
					.map(m.loadData(downloader)::get)
					.map(s -> s == null ? "" : s)
					.map(Main::escapeToCSV)
					.collect(Collectors.joining(",")))
			.append("\n");
		}
		
		Files.writeString(Paths.get("out.csv"), csv);
	}

	private static String escapeToCSV(String string) {
		return "\"" + string.replace("\"", "\"\"") + "\"";
	}
}