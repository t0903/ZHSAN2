package com.zhsan.common;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Node;

/**
 * Created by Peter on 7/3/2015.
 */
public class Utility {
    private Utility() {};

    public static Rectangle readRectangleFromXml(Node node) {
        Rectangle rect = new Rectangle();
        rect.setX(Integer.parseInt(node.getAttributes().getNamedItem("X").getNodeValue()));
        rect.setY(Integer.parseInt(node.getAttributes().getNamedItem("Y").getNodeValue()));
        rect.setWidth(Integer.parseInt(node.getAttributes().getNamedItem("Width").getNodeValue()));
        rect.setHeight(Integer.parseInt(node.getAttributes().getNamedItem("Height").getNodeValue()));
        return rect;
    }

}