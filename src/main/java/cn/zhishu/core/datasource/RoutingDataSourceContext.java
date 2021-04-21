package cn.zhishu.core.datasource;

/**
 * @description
 * @author: born
 * @date: 2021/4/20 12:10
 */
public class RoutingDataSourceContext  {

    //主数据源key
    public final static String MAIN_KEY = "main";
    public final static String SUIT_SEPERATE = "##";

    //账套数据源缺省库name
    private static String SUID_DS_DEFAULT_KEY = "write";

    //库 key
    static final ThreadLocal<String> threadLocalDataSourceKey = new ThreadLocal<>();

    //产品 key
    static final ThreadLocal<String> threadLocalProductKey = new ThreadLocal<>();

    /**
     * 获取主数据库的key
     * @return
     */
    public static String getMainKey() {
        return MAIN_KEY + SUIT_SEPERATE + MAIN_KEY;
    }

    /**
     * 获取数据库key
     * @return
     */
    public static String getDataSourceRoutingKey() {
        String key = threadLocalDataSourceKey.get();
        String product = threadLocalProductKey.get();
        if (key == null){
            if (product == null)
                key = MAIN_KEY;
            else
                key = SUID_DS_DEFAULT_KEY;
        }
        product = product == null ? MAIN_KEY : product;
        return product + SUIT_SEPERATE + key;
    }

    /**
     * 设置数据库的key
     * @param key
     */
    public static void setThreadLocalDataSourceKey(String key) {
        threadLocalDataSourceKey.set(key);
    }

    /**
     * 清除数据库的key
     */
    public static void clearThreadLocalDataSourceKey() {
        threadLocalDataSourceKey.remove();
        threadLocalProductKey.remove();
    }


    /**
     * 获取产品key
     * @return
     */
    public static String getDataSourceProductKey() {
        String key = threadLocalProductKey.get();
        return key == null ? MAIN_KEY : key;
    }

    /**
     * 设置产品key
     * @param key
     */
    public static void setDataSourceProductKey(String key) {
        threadLocalProductKey.set(key);
    }

    /**
     * 设置账套缺省数据源名
     * @param name
     */
    static void setSuitDsDefaultKey(String name){
        SUID_DS_DEFAULT_KEY = name;
    }

//    public static String getSuitDsWriteKey(){
//        return SUID_DS_DEFAULT_KEY;
//    }




}
