package ru.func.weathersender.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.func.weathersender.entity.Sensor;
import ru.func.weathersender.util.Location;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author func 06.01.2020
 */
public class XmlSensorParser implements SensorDataParser {

    private DocumentBuilder documentBuilder;
    private Document document;

    public XmlSensorParser() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);

        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @Override
    public String parseSensorToFormat(List<Sensor> sensors) {
        document = documentBuilder.newDocument();

        Element rootElement = document.createElement("data");

        document.appendChild(rootElement);

        sensors.forEach(sensor -> {
            Element contentElement = document.createElement("sensor");

            addNewElementToRoot(contentElement, "location", sensor.getLocation());
            addNewElementToRoot(contentElement, "temperature", sensor.getTemperature().toString());
            addNewElementToRoot(contentElement, "pressure", sensor.getPressure().toString());
            addNewElementToRoot(contentElement, "humidity", sensor.getHumidity().toString());

            rootElement.appendChild(contentElement);
        });

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(baos);

            Transformer transformer = buildTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(dataOutputStream));

            dataOutputStream.flush();

            return baos.toString(StandardCharsets.UTF_8.name());
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Transformer buildTransformer() throws TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());

        return transformer;
    }

    private void addNewElementToRoot(Element parent, String name, String text) {
        Element location = document.createElement(name);
        location.setTextContent(text);
        parent.appendChild(location);
    }
}