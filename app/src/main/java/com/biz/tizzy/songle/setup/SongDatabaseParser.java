package com.biz.tizzy.songle.setup;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by tizzy on 12/5/17.
 */

public class SongDatabaseParser {

    public static String[] getSongDetails(String songNum, NodeList nodeList) {

        String[] songDetails = new String[2]; // Title, Artist

        try {
            for (int i = 0; i < nodeList.getLength(); i++) {
                // Cycle down through the document
                Node songNode = nodeList.item(i);

                // Convert to element
                Element songElement = (Element)songNode;

                // Get Number
                NodeList songNumber = songElement.getElementsByTagName("Number");
                Element numberElement = (Element)songNumber.item(0);
                NodeList numberList = numberElement.getChildNodes();
                String number = ((Node)numberList.item(0)).getNodeValue();

                // Get Artist
                NodeList artist = songElement.getElementsByTagName("Artist");
                Element artistElement = (Element)artist.item(0);
                NodeList artistList = artistElement.getChildNodes();
                String artistName = ((Node)artistList.item(0)).getNodeValue();

                // Get Title
                NodeList title = songElement.getElementsByTagName("Title");
                Element titleElement = (Element)title.item(0);
                NodeList titleList = titleElement.getChildNodes();
                String songTitle = ((Node)titleList.item(0)).getNodeValue();

                if (number.equals(songNum)) {
                    songDetails[0] = artistName;
                    songDetails[1] = songTitle;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return songDetails;
    }
}
