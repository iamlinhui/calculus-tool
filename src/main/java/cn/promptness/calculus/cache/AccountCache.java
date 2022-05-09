package cn.promptness.calculus.cache;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountCache {

    private static final Logger log = LoggerFactory.getLogger(AccountCache.class);

    private static final String ACCOUNT_FILE = "account.dat";

    private static final Map<String, String> HEADER_MAP = new ConcurrentHashMap<>();

    public static void read() {
        File account = new File(ACCOUNT_FILE);
        if (account.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(account.toPath()))) {
                Object object;
                while ((object = ois.readObject()) != null) {
                    BasicClientCookie2 cookie = (BasicClientCookie2) object;
                    HEADER_MAP.put(cookie.getName(), cookie.getValue());
                }
            } catch (IOException | ClassNotFoundException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void cache() {
        File account = new File(ACCOUNT_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(account.toPath()))) {
            for (Map.Entry<String, String> entry : HEADER_MAP.entrySet()) {
                BasicClientCookie2 cookie = new BasicClientCookie2(entry.getKey(), entry.getValue());
                oos.writeObject(cookie);
            }
            oos.writeObject(null);
            oos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Set-Cookie: oa_session=9ldk347t38apb9ksgfs5nalk95; expires=Sun, 10-Jan-2021 08:59:36 GMT; path=/; domain=.oa.fenqile.com; httponly
     * Set-Cookie: oa_token_id=z0HpnPL%2FgXMwDXuKCd8G8KEG9MAGGCoaKZnctblq7s3TeTQXF3DwbuQQL2CcpQrlwn9ubrj0537SgQ31o7ndCQ%3D%3D; path=/; domain=.oa.fenqile.com
     * Set-Cookie: mid=31412; path=/; domain=.oa.fenqile.com
     *
     * @author lynn
     * @date 2021/1/9 21:00
     * @since v1.0.0
     */
    public static void flashHeader(List<Header> headers) {
        HEADER_MAP.clear();
        addHeader(headers);
    }

    public static void addHeader(List<Header> headers) {
        for (Header header : headers) {
            String value = header.getValue();
            String cookieString = value.split(";")[0];
            HEADER_MAP.put(cookieString.split("=")[0], cookieString.split("=")[1]);
        }
    }

    public static boolean haveAccount() {
        return !CollectionUtils.isEmpty(HEADER_MAP);
    }

    public static void logout() {
        HEADER_MAP.clear();
    }

    public static String getUid() {
        return HEADER_MAP.getOrDefault("mid", "");
    }

    public static List<Cookie> getHeaderList() {
        List<Cookie> cookieList = new ArrayList<>();
        for (Map.Entry<String, String> entry : HEADER_MAP.entrySet()) {
            Cookie cookie = new BasicClientCookie2(entry.getKey(), entry.getValue());
            cookieList.add(cookie);
        }
        return cookieList;
    }
}
