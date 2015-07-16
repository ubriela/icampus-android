package edu.usc.imsc.trucks;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import edu.usc.imsc.util.Tools;

/**
 * @author Ling
 * 
 */
public class TruckXMLParser {

	private Context ctx;

	public TruckXMLParser(Context ctx) {
		super();
		this.ctx = ctx;
	}

	public Map<Integer, Truck> parseXMLFile(InputStream inputStream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			Log.e(Tools.TAG, "enter parseXMLFile for trucks");
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(inputStream);

			// get the root element
			Element docEle = dom.getDocumentElement();
			Map<Integer, Truck> foodTrucks = new HashMap<Integer, Truck>();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("result");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Log.e(Tools.TAG, "found " + nl.getLength()
							+ " foodtruck results");

					// get the event element
					Element el = (Element) nl.item(i);

					// get the event object
					Truck truck = parseTruck(el);
					Log.e(Tools.TAG, "putting " + truck.toString()
							+ " to return values.");
					foodTrucks.put(truck.getId(), truck);
				}
			}
			return foodTrucks;
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	/**
	 * The format of a <result> node should contains <id></id> <name></name>
	 * <tweet></tweet> <lat></lat> <long></long> <time></time>
	 * 
	 * @param resultElement
	 * @return
	 */
	private Truck parseTruck(Element resultElement) {
		NodeList nl = resultElement.getElementsByTagName("id");
		int id = -1;
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			id = Integer.parseInt(nl.item(0).getTextContent().trim());
		} else {
			Log.i(Tools.TAG, "the result contains two ids");
		}
		nl = resultElement.getElementsByTagName("name");
		String name = "null";
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			name = nl.item(0).getTextContent();
		} else {
			Log.i(Tools.TAG,
					"the result contains no name tag or more than one name tag");
		}
		nl = resultElement.getElementsByTagName("tweet");
		String tweet = "null";
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			tweet = nl.item(0).getTextContent();
		} else {
			Log.i(Tools.TAG, "the result contains two tweets");
		}
		nl = resultElement.getElementsByTagName("lat");
		double lat = -1;
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			lat = Double.parseDouble(nl.item(0).getTextContent());
		} else {
			Log.i(Tools.TAG, "the result contains two lats");
		}
		nl = resultElement.getElementsByTagName("long");
		double lon = -1;
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			lon = Double.parseDouble(nl.item(0).getTextContent());
		} else {
			Log.i(Tools.TAG, "the result contains two longs");
		}
		GeoPoint location = new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6));
		nl = resultElement.getElementsByTagName("time");
		String time = "null";
		if (nl.getLength() == 1) {
			Log.i(Tools.TAG, "getNodeName=" + nl.item(0).getNodeName());
			Log.i(Tools.TAG, "getTextContent=" + nl.item(0).getTextContent());
			time = nl.item(0).getTextContent();
		} else {
			Log.i(Tools.TAG, "the result contains two times");
		}
		return new Truck(id, location, name, tweet, time);
	}
}
