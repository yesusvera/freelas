//package br.com.rsm.market.payment;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.InetAddress;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.google.appengine.repackaged.org.codehaus.jackson.annotate.JsonValue;
//
////import com.google.appengine.repackaged.com.google.api.client.util.IOUtils;
//
////@WebServlet("/callback_handler")
//public class CallbackHandlerServlet extends HttpServlet {
//    private static final String ROOT = "https://blockchain.info/";
//    private static final String CALLBACK_URL = "https://mydomain.com/callback_handler";
//    private static final String MY_BITCOIN_ADDRESS = "1A8JiWcwvpY7tAopUkSnGuEYHmzGYfZPiq";
//
//    private static String fetchURL(String URL) throws Exception {
//        URL url = new URL(URL);
//
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        connection.setConnectTimeout(10000);
//        connection.setReadTimeout(10000);
//
//        connection.setInstanceFollowRedirects(false);
//
//        connection.connect();
//
//        if (connection.getResponseCode() != 200) {
//            throw new Exception("Invalid HTTP Response code " + connection.getResponseCode());
//        }
//
//        return null;//IOUtils.toString(connection.getInputStream(), "UTF-8");
//    }
//
//    /**
//     *  Generate a Unique payment address for a User to send payment to
//     * @param myAddress Your bitcoin address
//     * @param callback The callback URL which will be notified when a payment is received
//     * @param anonymous Whether the transaction should be anonymous or not
//     * @param params Extra parameters to be passed to the callback URL
//     * @return
//     * @throws Exception
//     */
//    public static String generatePaymentAddress(String myAddress, String callback, boolean anonymous, Map<String, String> params) throws Exception {
//        String url = ROOT +  "api/receive?method=create&callback="+ URLEncoder.encode(callback, "UTF-8")+"&anonymous="+anonymous+"&address="+myAddress;
//
//        //Append any custom parameters to the callback
//        for (Map.Entry<String, String> param : params.entrySet()) {
//            url += "&"+param.getKey()+"="+URLEncoder.encode(param.getValue(), "UTF-8");
//        }
//
//        String response = fetchURL(url);
//
//        if (response == null)
//            throw new Exception("Server Returned NULL Response");
//
//        Map<String, Object> obj = (Map<String, Object>) JsonValue.class.cast(response);
//
//        if (obj.get("error") != null)
//            throw new Exception((String) obj.get("error"));
//
//        return (String)obj.get("input_address");
//    }
//
//    //Convert an amount in local currency to BTC
//    //e.g. convertToBTC("USD", 1) returns the value of 1 U.S dollar in BTC
//    public static double convertToBTC(String countryCode, double amount) throws Exception {
//        String response = fetchURL(ROOT +  "tobtc?currency="+countryCode+"&value="+amount);
//
//        if (response != null)
//            return Double.valueOf(response);
//        else
//             throw new Exception("Unknown Response");
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        String value = req.getParameter("value");
//        String transaction_hash = req.getParameter("transaction_hash");
//        String guid = req.getParameter("guid");
//
//        boolean authorized = false;
//
//        //Chekc the ip address of the request matches blockhain.info
//        InetAddress[] ips = InetAddress.getAllByName("blockchain.info");
//        for (InetAddress address : ips) {
//            if (req.getRemoteAddr().equals(address.getHostAddress())) {
//                authorized = true;
//                break;
//            }
//        }
//
//        if (!authorized)
//            return;
//
//        Connection conn ;//BitcoinDatabaseManager.conn();
//        try {
//             PreparedStatement stmt = conn.prepareStatement("insert into user_deposits (tx_hash, guid, value) values(?, ?, ?)");
//             try {
//                 stmt.setString(1, transaction_hash);
//                 stmt.setString(2, guid);
//                 stmt.setLong(3, Long.valueOf(value));
//
//                 if (stmt.executeUpdate() == 1) {
//                    res.getOutputStream().print("*ok*");
//                 }
//             } finally {
//                // BitcoinDatabaseManager.close(stmt);
//             }
////        } 
////        catch(MySQLIntegrityConstraintViolationException e ) {
////            //Duplicate Entry, assume OK
////
////            res.getOutputStream().print("*ok*");
//        } catch (Exception e) {
//            res.setStatus(500);
//
//            e.printStackTrace();
//        } finally {
////            BitcoinDatabaseManager.close(conn);
//        }
//    }
//}