package com.gwt.ss.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtLoginAsync {

    void j_gwt_security_check(String username, String password, AsyncCallback<Void> callback);

    /**
     * Maintain singleton {@link GwtLoginAsync GwtLoginAsync} instance.<br/>
     * 管控{@link GwtLoginAsync GwtLoginAsync}單一實例工具
     */
    public static final class Util {

        private static GwtLoginAsync instance;
        private static String processUrl = "j_spring_security_check";

        public static String getProcessUrl() {
            return processUrl;
        }

        public static void setProcessUrl(String processUrl) {
            assert processUrl != null && !processUrl.isEmpty();
            if (!Util.processUrl.equals(processUrl)) {
                Util.processUrl = processUrl;
                if (instance != null) {
                    setServiceEntryPoint(instance , processUrl);
                }
            }
        }

        private static void setServiceEntryPoint(GwtLoginAsync instance, String processUrl) {
            ServiceDefTarget target = (ServiceDefTarget) instance;
            String hpbl = GWT.getHostPageBaseURL();
            int idx = hpbl.indexOf("//");
            String hostContextPath = hpbl.substring(0, idx + 2);
            hpbl = hpbl.substring(idx + 2);
            idx = hpbl.indexOf("/");
            if (idx > -1) {
                hostContextPath += hpbl.substring(0, idx + 1);
                hpbl = hpbl.substring(idx + 1);
                idx = hpbl.indexOf("/");
                if (idx > -1) {
                    hostContextPath += hpbl.substring(0, idx);
                } else {
                    hostContextPath += hpbl;
                }
            } else {
                hostContextPath += hpbl;
            }

            if (processUrl.startsWith("/")) {
                processUrl = hostContextPath + processUrl;
            } else if (processUrl.toLowerCase().startsWith("http://") || processUrl.toLowerCase().startsWith("https://")) {
                //do nothing
            } else {
                processUrl = hostContextPath + "/" + processUrl;
            }
            target.setServiceEntryPoint(processUrl);
            GWT.log("Set Login Service Entry Point as " + target.getServiceEntryPoint());
        }

        /**
         * return {@link GwtLoginAsync GwtLoginAsync} instance with specified service entry point.<br/>
         * 指定服務進入點並取得{@link GwtLoginAsync GwtLoginAsync} 實例
         * @param processUrl Spring login processing url
         * @return
         */
        public static GwtLoginAsync getInstance(String processUrl) {
            assert processUrl != null && !processUrl.isEmpty();
            setProcessUrl(processUrl);
            return getInstance();
        }

        /**
         * return {@link GwtLoginAsync GwtLoginAsync} instance with default service entry point &quot;j_spring_security_check&quot;<br/>
         * 取得預設進入點為&quot;j_spring_security_check&quot;之{@link GwtLoginAsync GwtLoginAsync} 實例
         * @return
         */
        public static GwtLoginAsync getInstance() {
            if (instance == null) {
                instance = (GwtLoginAsync) GWT.create(GwtLogin.class);
                setServiceEntryPoint(instance, getProcessUrl());
            }
            return instance;
        }

        private Util() {
            // Utility class should not be instanciated
        }
    }
}
