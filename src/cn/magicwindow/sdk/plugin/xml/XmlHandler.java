package cn.magicwindow.sdk.plugin.xml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by tony on 16/8/3.
 */
public class XmlHandler<T> {

    public T parse(Class<T> resultClass, String xmlString){
        Serializer serializer = new Persister();

        try {
            return serializer.read(resultClass, xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
