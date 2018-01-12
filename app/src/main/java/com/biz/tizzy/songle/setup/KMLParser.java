package com.biz.tizzy.songle.setup;

import com.biz.tizzy.songle.map.MapPoint;

import org.w3c.dom.*;

/**
 * Created by tizzy on 10/30/17.
 */

public class KMLParser {

    public static MapPoint[] getCoordinates(NodeList listOfPlacemarks) {

        MapPoint[] mapPoints = new MapPoint[listOfPlacemarks.getLength()];

        try {
            for (int i = 0; i < listOfPlacemarks.getLength(); i++) {
                // Cycle down through the document
                Node placemarkNode = listOfPlacemarks.item(i);

                // Convert to element
                Element placemarkElement = (Element)placemarkNode;

                // Get interest
                NodeList description = placemarkElement.getElementsByTagName("description");
                Element descriptionElement = (Element)description.item(0);
                NodeList interestList = descriptionElement.getChildNodes();
                String interest = ((Node)interestList.item(0)).getNodeValue();

                // Get lyric
                NodeList name = placemarkElement.getElementsByTagName("name");
                Element nameElement = (Element)name.item(0);
                NodeList lyricList = nameElement.getChildNodes();
                String lyricLocation = ((Node)lyricList.item(0)).getNodeValue();

                NodeList coordinatesList = placemarkElement.getElementsByTagName("coordinates");
                Element coordinateElement = (Element)coordinatesList.item(0);
                // Single element contains all 3 coordinates

                // Return a list of all node elements
                NodeList coordinateList = coordinateElement.getChildNodes();

                // System.out.println("coordinates : " + ((Node)coordinateList.item(0)).getNodeValue().trim());

                mapPoints[i] = extractLatLon(coordinateList.item(0), interest, lyricLocation);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return mapPoints;
    }

    private static MapPoint extractLatLon(Node coordinateNode, String interest, String lyric) {

        String coordString = coordinateNode.getNodeValue();
        String[] parts = coordString.split(",");

        MapPoint mapPoint = new MapPoint(Double.parseDouble(parts[1]), Double.parseDouble(parts[0]));
        mapPoint.setInterest(interest);
        mapPoint.setLyricLocation(lyric);

        return mapPoint;
    }


}
