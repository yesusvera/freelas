package br.com.ibracon.idr.form.util;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestProxy {

	public static void main(String s[]) {
		TestProxy.dump("http://www.yahoo.com");
		System.out.println("**************");
		TestProxy.dump("https://www.paypal.com");
		System.out.println("**************");
	}

	public static void dump(String URLName) {
		try {
			DataInputStream di = null;
			FileOutputStream fo = null;
			byte[] b = new byte[1];

			// PROXY
			System.setProperty("http.proxyHost", "cache.bb.com.br");
			System.setProperty("http.proxyPort", "80");

			URL u = new URL(URLName);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			//
			// it's not the greatest idea to use a sun.misc.* class
			// Sun strongly advises not to use them since they can
			// change or go away in a future release so beware.
			//
			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
//			String encodedUserPwd = encoder.encode("mydomain\\c1152632:81367996".getBytes());
			String encodedUserPwd = encoder.encode("c1152632:81367996".getBytes());
			con.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
			// PROXY ----------

			di = new DataInputStream(con.getInputStream());
			while (-1 != di.read(b, 0, 1)) {
				System.out.print(new String(b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
