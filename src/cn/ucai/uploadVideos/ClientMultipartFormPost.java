package cn.ucai.uploadVideos;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

public class ClientMultipartFormPost {
	/** 视频所在路径 **/
	public static final String strFilePath = "E:\\优才学院\\20160425\\day14";
	/** 章节ID **/
	public static final String chaId = "5326";

	/**
	 * 获得视频指定时长，单位是秒（s）
	 * 
	 * @param filePath
	 *            某视频绝对路径
	 * @return 
	 */
	public static String getMinSeconds(String filePath) {
		long ms = 0;
		try {
			File source = new File(filePath);
			Encoder encoder = new Encoder();
			MultimediaInfo m = encoder.getInfo(source);
			ms = m.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ms / 1000 + "";
	}

	/**
	 * 得到格式化后的日期，为每天上传视频时的日期
	 * 
	 * @return
	 */
	public static String getFormatDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 30);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(cal.getTime());
	}

	public static void main(String[] args) {
		login("你的账号名", "你的密码", "undefined");

		File file = new File(strFilePath);
		List<File> list = Arrays.asList(file.listFiles());
		for (File f : list) {
			System.out.println(f.getName());
			String filePath = f.getAbsolutePath();
			String fileWholeName = f.getName();
			System.out.println(filePath);
			System.out.println(fileWholeName);
			String num = fileWholeName.substring(0, fileWholeName.indexOf("、"));
			System.out.println(num);
			String fileName = fileWholeName.substring(fileWholeName.indexOf("、") + 1, fileWholeName.lastIndexOf("."));
			System.out.println(fileName);
			uploadForm(fileName, num, getFormatDate(), filePath, chaId);
		}
	}

	static CloseableHttpClient httpclient = HttpClients.createDefault();

	/**
	 * 上传表单到ucai服务器
	 * 
	 * @param title
	 *            小节标题
	 * @param num
	 *            小节顺序
	 * @param time
	 *            小节上传时间，如"2016-05-03 18:35"
	 * @param filePath
	 *            小节文件路径
	 * @param chId
	 *            章节id
	 */
	public static void uploadForm(String title, String num, String time, String filePath, String chId) {
		try {
			HttpPost httppost = new HttpPost(
					"http://www.ucai.cn/index.php?app=fullstack&mod=Assistadmin&act=doEditChapter");
			httppost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
			// httppost.setHeader("Cookie", cookie);
			httppost.setHeader("Referer",
					"http://www.ucai.cn/index.php?app=fullstack&mod=Assistadmin&act=schedule&cid=351");
			httppost.setHeader("X-Requested-With", "XMLHttpRequest");
			StringBody comment1 = new StringBody(new String(title.getBytes(), "iso-8859-1"), ContentType.TEXT_PLAIN);
			StringBody comment2 = new StringBody("", ContentType.TEXT_PLAIN);
			StringBody comment3 = new StringBody(num, ContentType.TEXT_PLAIN);
			StringBody comment4 = new StringBody(getMinSeconds(filePath), ContentType.TEXT_PLAIN);
			StringBody comment5 = new StringBody("1", ContentType.TEXT_PLAIN);
			StringBody comment6 = new StringBody("0", ContentType.TEXT_PLAIN);
			StringBody comment7 = new StringBody("0", ContentType.TEXT_PLAIN);
			StringBody comment8 = new StringBody(time, ContentType.TEXT_PLAIN);
			StringBody comment9 = new StringBody("", ContentType.TEXT_PLAIN);
			StringBody comment10 = new StringBody("", ContentType.TEXT_PLAIN);
			StringBody comment11 = new StringBody(chId, ContentType.TEXT_PLAIN);
			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("title", comment1)
					.addPart("summary", comment2).addPart("orderno", comment3).addPart("dtime", comment4)
					.addPart("cptype", comment5).addPart("flag", comment6).addPart("fanxian", comment7)
					.addPart("stime", comment8).addPart("tuid", comment9).addPart("chid", comment10)
					.addPart("sid", comment11).build();
			httppost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			System.out.println(EntityUtils.toString(resEntity));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void login(String account, String password, String remember) {
		// CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost("http://www.ucai.cn/index.php?app=fullstack&mod=Public&act=doLogin");
			httppost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
			StringBody comment1 = new StringBody(account, ContentType.TEXT_PLAIN);
			StringBody comment2 = new StringBody(password, ContentType.TEXT_PLAIN);
			StringBody comment3 = new StringBody(remember, ContentType.TEXT_PLAIN);
			HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("account", comment1)
					.addPart("password", comment2).addPart("remember", comment3).build();
			httppost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			System.out.println(EntityUtils.toString(resEntity));
			Header[] headArr = httppost.getAllHeaders();
			for (Header header : headArr) {
				System.out.println(header.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
