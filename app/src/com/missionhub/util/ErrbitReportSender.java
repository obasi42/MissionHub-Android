package com.missionhub.util;

import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ErrbitReportSender implements ReportSender {

    private TreeMap<String, String> customValues;

    public static enum ErrbitReportField {
        ENVIRONMENT_NAME, USER_ID, USER_NAME, USER_EMAIL, USER_USERNAME
    }

    public ErrbitReportSender() {

    }

    @Override
    public void send(CrashReportData errorContent) throws ReportSenderException {
        String errbitUrl = ACRA.getConfig().formUri();
        String errbitApiKey = ACRA.getConfig().formKey();

        if (errbitUrl.endsWith("/")) {
            errbitUrl = errbitUrl.substring(0, errbitUrl.length() - 1);
        }
        errbitUrl += "/notifier_api/v2/notices";

        try {
            URL url = new URL(errbitUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

            Document report = createErrbitXml(errorContent, errbitApiKey);
            OutputStream os = conn.getOutputStream();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(report), new StreamResult(os));
            os.flush();
            os.close();

            int response = conn.getResponseCode();
            Log.d(ErrbitReportSender.class.getSimpleName(), "Sent exception to " + errbitUrl + ". Got response code " + String.valueOf(response));
        } catch (Exception e) {
            throw new ReportSenderException("Error while sending via ErrbitReportSender", e);
        }
    }

    private Document createErrbitXml(CrashReportData report, String errbitApiKey) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root (notice)
            Document doc = docBuilder.newDocument();
            Element notice = doc.createElement("notice");
            notice.setAttribute("version", "2.3");
            doc.appendChild(notice);

            // api key
            notice.appendChild(createElement(doc, "api-key", errbitApiKey));

            // notifier
            Element notifier = doc.createElement("notifier");
            notifier.appendChild(createElement(doc, "name", "Errbit ACRA Sender"));
            notifier.appendChild(createElement(doc, "version", "0.1"));
            notifier.appendChild(doc.createElement("url"));
            notice.appendChild(notifier);

            // error
            Element error = doc.createElement("error");
            error.appendChild(createElement(doc, "class", report.getThrowable().getClass().getName()));
            error.appendChild(createElement(doc, "message", report.getThrowable().getMessage()));
            Element errorBacktrace = doc.createElement("backtrace");
            Throwable current = report.getThrowable();
            while (current != null) {
                try {
                    StackTraceElement[] stackTrace = current.getStackTrace();
                    for (StackTraceElement el : stackTrace) {
                        errorBacktrace.appendChild(createStacktraceLineElement(doc, el));
                    }
                    current = current.getCause();
                    if (current != null) {
                        Element line = doc.createElement("line");
                        try {
                            line = createStacktraceLineElement(doc, current.getStackTrace()[0]);
                        } catch (Exception e) {
                            line.setAttribute("number", "");
                        }
                        line.setAttribute("file", "### CAUSED BY ###: " + current.toString());
                        errorBacktrace.appendChild(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            error.appendChild(errorBacktrace);
            notice.appendChild(error);

            // request
            Element request = doc.createElement("request");
            request.appendChild(doc.createElement("url"));
            request.appendChild(doc.createElement("component"));
            request.appendChild(doc.createElement("action"));

            Element cgi = doc.createElement("cgi-data");
            for (Element element : getCGIElements(doc, report)) {
                cgi.appendChild(element);
            }
            request.appendChild(cgi);

            notice.appendChild(request);

            // server-environment
            Element env = doc.createElement("server-environment");
            env.appendChild(createElement(doc, "project-root", report.getProperty(ReportField.FILE_PATH)));
            env.appendChild(createElement(doc, "environment-name", getCustomValue(report, ErrbitReportField.ENVIRONMENT_NAME)));
            env.appendChild(createElement(doc, "app-version", report.getProperty(ReportField.APP_VERSION_CODE)));
            env.appendChild(createElement(doc, "hostname", report.getProperty(ReportField.DEVICE_ID)));
            notice.appendChild(env);

            // current user
            Element user = doc.createElement("current-user");
            user.appendChild(createElement(doc, "id", getCustomValue(report, ErrbitReportField.USER_ID)));
            user.appendChild(createElement(doc, "name", getCustomValue(report, ErrbitReportField.USER_NAME)));
            user.appendChild(createElement(doc, "email", getCustomValue(report, ErrbitReportField.USER_EMAIL)));
            user.appendChild(createElement(doc, "username", getCustomValue(report, ErrbitReportField.USER_USERNAME)));
            notice.appendChild(user);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        return element;
    }

    private Element createStacktraceLineElement(Document doc, StackTraceElement element) {
        Element line = doc.createElement("line");
        try {
            line.setAttribute("method", element.getClassName() + "." + element.getMethodName());
            line.setAttribute("file", element.getFileName() == null ? "Unknown" : element.getFileName());
            line.setAttribute("number", String.valueOf(element.getLineNumber()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return line;
    }

    private Element createVarListElement(Document doc, String key, String value) {
        Element var = doc.createElement("var");
        var.setAttribute("key", key);
        var.setTextContent(value);
        return var;
    }

    public TreeMap<String, String> getCustomDataMap(CrashReportData data) {
        if (customValues == null) {
            TreeMap<String, String> map = new TreeMap<String, String>();
            String customData = data.getProperty(ReportField.CUSTOM_DATA);
            if (customData != null) {
                String[] lines = customData.split("\n");
                for (String line : lines) {
                    String[] kvLine = line.split("=");
                    try {
                        map.put(kvLine[0].toUpperCase(), kvLine.length == 1 || kvLine[1] == null ? "" : kvLine[1].replace("\\\\n", "\n"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (this) {
                if (customValues == null) {
                    customValues = map;
                }
            }
        }
        return customValues;
    }

    public String getCustomValue(CrashReportData data, ErrbitReportField field) {
        String value = null;
        try {
            value = getCustomDataMap(data).get(field.name().toUpperCase());
        } catch (Exception e) { /* ignore */ }
        return value == null ? "" : value;
    }

    private List<Element> getCGIElements(Document doc, CrashReportData data) {
        TreeMap<String, String> custom = new TreeMap<String, String>(getCustomDataMap(data));

        // remove errbit fields
        for (ErrbitReportField field : ErrbitReportField.values()) {
            custom.remove(field.name().toUpperCase());
        }

        // add other crash data
        ReportField[] ignore = new ReportField[]{ReportField.CUSTOM_DATA, ReportField.FILE_PATH, ReportField.APP_VERSION_CODE, ReportField.DEVICE_ID, ReportField.STACK_TRACE};
        List<ReportField> ignoreList = Arrays.asList(ignore);
        for (ReportField field : ReportField.values()) {
            if (ignoreList.contains(field)) {
                continue;
            }
            custom.put(field.name().toUpperCase(), data.getProperty(field));
        }

        List<Element> elements = new ArrayList<Element>();
        for (String key : custom.keySet()) {
            elements.add(createVarListElement(doc, key, custom.get(key)));
        }
        return elements;
    }

    public static void putErrbitData(ErrbitReportField field, String value) {
        ACRA.getErrorReporter().putCustomData(field.name(), value);
    }

    public static void removeErrbitData(ErrbitReportField field) {
        ACRA.getErrorReporter().removeCustomData(field.name());
    }

}
