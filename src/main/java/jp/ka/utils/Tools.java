package jp.ka.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	private static DatabaseReader client;
	static {
		try {
			// 曲线救国，war 内部文件没有路径打包后找不到
			ClassPathResource cpr = new ClassPathResource("static/GeoLite2-Country.mmdb");
			InputStream is = cpr.getInputStream();
			File database = File.createTempFile("GeoLite2-Country", ".mmdb");
			FileUtils.copyInputStreamToFile(is, database);
			IOUtils.close(is);

			// File database = ResourceUtils.getFile("classpath:static/GeoLite2-Country.mmdb");
			client = new DatabaseReader.Builder(database).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String geoip(String ip) {
		try {
			String realIP = ip.split(",")[0];
			if (realIP.equals("-")) {
				return "None";
			}

			InetAddress ipAddress = InetAddress.getByName(realIP); // 只取第一个真实用户ip

			CountryResponse response = client.country(ipAddress);
			Country country = response.getCountry();

			if (country.getIsoCode() == null) {
				return "NoFound";
			}
			return country.getIsoCode();
		} catch (Exception e) {
			// e.printStackTrace();
			String extension = FilenameUtils.getExtension(e.getClass().getName());
			return extension.replaceAll("Exception", "");
		}
	}

	public static Date formatDate(String str) {
		Pattern compile = Pattern.compile("^(.*)/(.*)/(.*):(.*):(.*):(.*) (.*)$");
		Matcher matcher = compile.matcher(str);
		if (matcher.find()) {
			String target = String.format("%s-%s-%sT%s:%s:%s%s",
					matcher.group(3), monthToNum(matcher.group(2)), matcher.group(1),
					matcher.group(4), matcher.group(5), matcher.group(6),
					matcher.group(7));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

			try {
				return sdf.parse(target);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static Date formatDateSimple(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			String format = sdf.format(formatDate(str));
			return sdf.parse(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String monthToNum(String str) {
		String target = "";

		switch (str) {
			case "Jan": {
				target = "01";
				break;
			}
			case "Feb": {
				target = "02";
				break;
			}
			case "Mar": {
				target = "03";
				break;
			}
			case "Apr": {
				target = "04";
				break;
			}
			case "May": {
				target = "05";
				break;
			}
			case "Jun": {
				target = "06";
				break;
			}
			case "Jul": {
				target = "07";
				break;
			}
			case "Aug": {
				target = "08";
				break;
			}
			case "Sep": {
				target = "09";
				break;
			}
			case "Oct": {
				target = "10";
				break;
			}
			case "Nov": {
				target = "11";
				break;
			}
			case "Dec": {
				target = "12";
				break;
			}
		}

		return target;
	}

	public static Model chartModel(Model model, Map<String, Object>  data) {
		if (data == null) {
			return null;
		}

		model.addAttribute("os", data.get("os"));
		model.addAttribute("browser", data.get("browser"));
		model.addAttribute("httpCode", data.get("httpCode"));
		model.addAttribute("visitTime", data.get("visitTime"));
		model.addAttribute("ip", data.get("ip"));
		model.addAttribute("country", data.get("country"));
		model.addAttribute("realIP", data.get("realIP"));
		model.addAttribute("realCountry", data.get("realCountry"));
		model.addAttribute("method", data.get("method"));
		model.addAttribute("fileType", data.get("fileType"));
		model.addAttribute("count", data.get("count"));

		return model;
	}

}